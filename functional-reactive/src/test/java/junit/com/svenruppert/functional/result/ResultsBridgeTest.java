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
package junit.com.svenruppert.functional.result;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultsBridgeTest {

  @Test
  void fromLegacySuccessBecomesModernSuccess() {
    com.svenruppert.functional.model.Result<String> legacy =
        com.svenruppert.functional.model.Result.success("hello");
    Result<String, String> modern = Results.fromLegacy(legacy, "no value");
    assertTrue(modern.isSuccess());
    assertEquals("hello", modern.getOrThrow());
  }

  @Test
  void fromLegacyFailurePreservesMessage() {
    com.svenruppert.functional.model.Result<String> legacy =
        com.svenruppert.functional.model.Result.failure("legacy error");
    Result<String, String> modern = Results.fromLegacy(legacy, "fallback");
    assertTrue(modern.isFailure());
    assertEquals("legacy error", ((Result.Failure<String, String>) modern).error());
  }

  @Test
  void fromLegacyEmptySuccessFallsBackToProvidedError() {
    com.svenruppert.functional.model.Result<String> legacy =
        com.svenruppert.functional.model.Result.ofNullable(null);
    Result<String, String> modern = Results.fromLegacy(legacy, "fallback");
    assertTrue(modern.isFailure(),
        "a legacy success holding null must be treated as a failure in the modern type");
  }

  @Test
  void toLegacyRoundTripsSuccess() {
    Result<Integer, String> modern = Result.success(7);
    com.svenruppert.functional.model.Result<Integer> legacy = Results.toLegacy(modern);
    assertTrue(legacy.isPresent());
    assertEquals(7, legacy.get());
  }

  @Test
  void toLegacyRoundTripsFailure() {
    Result<Integer, String> modern = Result.failure("bad");
    com.svenruppert.functional.model.Result<Integer> legacy = Results.toLegacy(modern);
    assertFalse(legacy.isPresent());
    String[] seen = new String[1];
    legacy.ifFailed(m -> seen[0] = m);
    assertEquals("bad", seen[0]);
  }

  @Test
  void toLegacyWithErrorRendererSupportsNonStringErrors() {
    enum Err { BAD }
    Result<Integer, Err> modern = Result.failure(Err.BAD);
    com.svenruppert.functional.model.Result<Integer> legacy = Results.toLegacy(modern, Enum::name);
    assertFalse(legacy.isPresent());
    String[] seen = new String[1];
    legacy.ifFailed(m -> seen[0] = m);
    assertEquals("BAD", seen[0]);
  }
}
