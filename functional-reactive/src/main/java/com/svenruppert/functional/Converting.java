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
package com.svenruppert.functional;

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

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Conversion helpers returning a modern {@link Result Result&lt;X, String&gt;}.
 *
 * <p>Errors are rendered as {@code "ExceptionType - message"}. A {@code null}
 * result is mapped to a failure, since the modern {@code Result} forbids
 * {@code Success(null)}.
 */
public interface Converting {

  static <T> Function<T, Result<String, String>>
  convertToStringResult(Function<T, String> func) {
    Objects.requireNonNull(func);
    return t -> tryApply(() -> func.apply(t));
  }

  static <T> Function<T, Result<String, String>>
  convertToStringResult() {
    return convertToStringResult(String::valueOf);
  }

  static <T> Function<T, Result<Boolean, String>>
  convertToBooleanResult(Function<T, Boolean> func) {
    Objects.requireNonNull(func);
    return t -> tryApply(() -> func.apply(t));
  }

  static <T> Function<T, Result<Boolean, String>>
  convertToBooleanResult() {
    return Converting.<T>convertToStringResult()
        .andThen(r -> r.flatMap(s -> tryApply(() -> Boolean.parseBoolean(s))));
  }

  static <T> Function<T, Result<Integer, String>>
  convertToIntegerResult(Function<T, Integer> func) {
    Objects.requireNonNull(func);
    return t -> tryApply(() -> func.apply(t));
  }

  static <T> Function<T, Result<Integer, String>>
  convertToIntegerResult() {
    return Converting.<T>convertToStringResult()
        .andThen(r -> r.flatMap(s -> tryApply(() -> Integer.parseInt(s))));
  }

  static <T> Function<T, Result<Double, String>>
  convertToDoubleResult(Function<T, Double> func) {
    Objects.requireNonNull(func);
    return t -> tryApply(() -> func.apply(t));
  }

  static <T> Function<T, Result<Double, String>>
  convertToDoubleResult() {
    return Converting.<T>convertToStringResult()
        .andThen(r -> r.flatMap(s -> tryApply(() -> Double.parseDouble(s))));
  }

  // -------------------------------------------------------------------------
  // Internal helpers
  // -------------------------------------------------------------------------

  private static <X> Result<X, String> tryApply(Supplier<X> action) {
    try {
      return Result.ofNullable(action.get(), "value was null");
    } catch (Exception e) {
      return Result.failure(renderError(e));
    }
  }

  private static String renderError(Exception e) {
    String simpleName = e.getClass().getSimpleName();
    String message = e.getMessage();
    return message != null ? simpleName + " - " + message : simpleName + " - no message";
  }
}
