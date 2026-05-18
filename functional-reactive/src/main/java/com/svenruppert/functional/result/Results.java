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
package com.svenruppert.functional.result;

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

import com.svenruppert.functional.model.Result.Failure;

import java.util.Objects;

/**
 * Bridges between the legacy {@code com.svenruppert.functional.model.Result<T>} (String-only error)
 * and the modern {@link Result Result&lt;T, E&gt;}. Intended for gradual migration of callers.
 *
 * <p>Empty legacy results (success holding {@code null}) are mapped to failures with the supplied
 * error, since the modern type forbids null-success.
 */
public final class Results {

  private Results() {
  }

  public static <T> Result<T, String> fromLegacy(com.svenruppert.functional.model.Result<T> legacy,
                                                 String errorIfEmpty) {
    Objects.requireNonNull(legacy);
    Objects.requireNonNull(errorIfEmpty);
    if (legacy instanceof Failure<T> f) {
      String[] captured = new String[1];
      f.ifFailed(msg -> captured[0] = msg);
      return Result.failure(captured[0] != null ? captured[0] : errorIfEmpty);
    }
    T value = legacy.toOptional().orElse(null);
    return value != null ? Result.success(value) : Result.failure(errorIfEmpty);
  }

  public static <T> com.svenruppert.functional.model.Result<T> toLegacy(Result<T, String> modern) {
    Objects.requireNonNull(modern);
    return modern.fold(
        com.svenruppert.functional.model.Result::success,
        com.svenruppert.functional.model.Result::failure);
  }

  public static <T, E> com.svenruppert.functional.model.Result<T> toLegacy(Result<T, E> modern,
                                                                          java.util.function.Function<? super E, String> errorRenderer) {
    Objects.requireNonNull(modern);
    Objects.requireNonNull(errorRenderer);
    return modern.fold(
        com.svenruppert.functional.model.Result::success,
        e -> com.svenruppert.functional.model.Result.failure(errorRenderer.apply(e)));
  }
}
