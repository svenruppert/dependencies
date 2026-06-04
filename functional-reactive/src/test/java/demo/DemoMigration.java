/*
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the EUPL, Version 1.2 (the "Licence");
 * you may not use this file except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package demo;

/*-
 * #%L
 * SRU - Functional
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2026 Sven Ruppert
 * %%
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * #L%
 */

import com.svenruppert.functional.result.Result;
import com.svenruppert.functional.result.Results;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Demonstrates how to migrate from the legacy
 * {@link com.svenruppert.functional.model.Result Result&lt;T&gt;} to the modern
 * {@link Result Result&lt;T, E&gt;} using the {@link Results} bridge.
 *
 * <p>Migration paths:
 * <ol>
 *   <li>Producer migrated, consumer not yet — wrap modern result via
 *       {@code Results.toLegacy(modern)} before returning.</li>
 *   <li>Consumer migrated, producer not yet — lift legacy result via
 *       {@code Results.fromLegacy(legacy, errorIfEmpty)} before consuming.</li>
 *   <li>Typed errors on the modern side — use the overload
 *       {@code Results.toLegacy(modern, errorRenderer)} that flattens
 *       the typed error to a String.</li>
 * </ol>
 */
@SuppressWarnings("removal")
public class DemoMigration {

  @Test
  void modernSuccessLoweredToLegacy() {
    Result<String, String> modern = Result.success("ok");
    com.svenruppert.functional.model.Result<String> legacy = Results.toLegacy(modern);
    assertTrue(legacy.isPresent());
    assertEquals("ok", legacy.get());
  }

  @Test
  void modernFailureLoweredToLegacy() {
    Result<String, String> modern = Result.failure("boom");
    com.svenruppert.functional.model.Result<String> legacy = Results.toLegacy(modern);
    assertTrue(legacy.isAbsent());
    legacy.ifFailed(msg -> assertEquals("boom", msg));
  }

  @Test
  void legacySuccessLiftedToModern() {
    com.svenruppert.functional.model.Result<String> legacy =
        com.svenruppert.functional.model.Result.success("hello");
    Result<String, String> modern = Results.fromLegacy(legacy, "missing");
    assertTrue(modern.isSuccess());
    assertEquals("hello", modern.getOrThrow());
  }

  @Test
  void legacyFailureLiftedToModernPreservesMessage() {
    com.svenruppert.functional.model.Result<String> legacy =
        com.svenruppert.functional.model.Result.failure("upstream-error");
    Result<String, String> modern = Results.fromLegacy(legacy, "fallback");
    assertTrue(modern.isFailure());
    assertEquals("upstream-error", ((Result.Failure<String, String>) modern).error());
  }

  @Test
  void legacyEmptySuccessLiftedAsFailure() {
    // Legacy Result allows success(null); modern forbids it. The bridge maps
    // null success to a failure using the supplied fallback error.
    com.svenruppert.functional.model.Result<String> emptyLegacy =
        com.svenruppert.functional.model.Result.ofNullable(null);
    Result<String, String> modern = Results.fromLegacy(emptyLegacy, "was empty");
    assertTrue(modern.isFailure());
  }

  @Test
  void typedErrorFlattenedToLegacy() {
    // Modern Result with a typed error (an enum here) is flattened by
    // mapping the error to a String at the boundary.
    enum Code { NOT_FOUND, INVALID }
    Result<Integer, Code> modern = Result.failure(Code.NOT_FOUND);
    com.svenruppert.functional.model.Result<Integer> legacy =
        Results.toLegacy(modern, code -> "error-code:" + code.name());
    assertTrue(legacy.isAbsent());
    legacy.ifFailed(msg -> assertEquals("error-code:NOT_FOUND", msg));
  }
}
