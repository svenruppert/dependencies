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

import com.svenruppert.functional.model.Result;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Conversion helpers producing {@link Result legacy} or
 * {@link com.svenruppert.functional.result.Result modern} {@code Result} values.
 *
 * <p>The legacy {@code convertToX} methods remain functional but are deprecated;
 * use the modern {@code convertToXResult} variants which return
 * {@code Result<X, String>} and reject {@code null} success values.
 */
public interface Converting {

  // -------------------------------------------------------------------------
  // Modern variants: Result<X, String>
  // -------------------------------------------------------------------------

  static <T> Function<T, com.svenruppert.functional.result.Result<String, String>>
  convertToStringResult(Function<T, String> func) {
    Objects.requireNonNull(func);
    return t -> tryApply(() -> func.apply(t));
  }

  static <T> Function<T, com.svenruppert.functional.result.Result<String, String>>
  convertToStringResult() {
    return convertToStringResult(String::valueOf);
  }

  static <T> Function<T, com.svenruppert.functional.result.Result<Boolean, String>>
  convertToBooleanResult(Function<T, Boolean> func) {
    Objects.requireNonNull(func);
    return t -> tryApply(() -> func.apply(t));
  }

  static <T> Function<T, com.svenruppert.functional.result.Result<Boolean, String>>
  convertToBooleanResult() {
    return Converting.<T>convertToStringResult()
        .andThen(r -> r.flatMap(s -> tryApply(() -> Boolean.parseBoolean(s))));
  }

  static <T> Function<T, com.svenruppert.functional.result.Result<Integer, String>>
  convertToIntegerResult(Function<T, Integer> func) {
    Objects.requireNonNull(func);
    return t -> tryApply(() -> func.apply(t));
  }

  static <T> Function<T, com.svenruppert.functional.result.Result<Integer, String>>
  convertToIntegerResult() {
    return Converting.<T>convertToStringResult()
        .andThen(r -> r.flatMap(s -> tryApply(() -> Integer.parseInt(s))));
  }

  static <T> Function<T, com.svenruppert.functional.result.Result<Double, String>>
  convertToDoubleResult(Function<T, Double> func) {
    Objects.requireNonNull(func);
    return t -> tryApply(() -> func.apply(t));
  }

  static <T> Function<T, com.svenruppert.functional.result.Result<Double, String>>
  convertToDoubleResult() {
    return Converting.<T>convertToStringResult()
        .andThen(r -> r.flatMap(s -> tryApply(() -> Double.parseDouble(s))));
  }

  // -------------------------------------------------------------------------
  // Legacy variants: Result<X>  (deprecated)
  // -------------------------------------------------------------------------

  /**
   * @deprecated Use {@link #convertToStringResult(Function)} which returns the modern
   * {@code Result<String, String>}. The modern variant rejects {@code null} success
   * values where this legacy variant would silently produce {@code Result.success(null)}.
   */
  @Deprecated(forRemoval = true)
  @SuppressWarnings("removal")
  static <T>
  Function<T, Result<String>> convertToString(Function<T, String> func) {
    return Transformations.asCheckedFunc(func);
  }

  /**
   * @deprecated Use {@link #convertToStringResult()}.
   */
  @Deprecated(forRemoval = true)
  static <T>
  Function<T, Result<String>> convertToString() {
    return convertToString(String::valueOf);
  }

  /**
   * @deprecated Use {@link #convertToBooleanResult(Function)}.
   */
  @Deprecated(forRemoval = true)
  @SuppressWarnings("removal")
  static <T>
  Function<T, Result<Boolean>> convertToBoolean(Function<T, Boolean> func) {
    return Transformations.asCheckedFunc(func);
  }

  /**
   * @deprecated Use {@link #convertToBooleanResult()}.
   */
  @Deprecated(forRemoval = true)
  static <T>
  Function<T, Result<Boolean>> convertToBoolean() {
    return Converting
        .<T>convertToString()
        .andThen(s -> s.map(Boolean::parseBoolean));
  }

  /**
   * @deprecated Use {@link #convertToIntegerResult(Function)}.
   */
  @Deprecated(forRemoval = true)
  @SuppressWarnings("removal")
  static <T>
  Function<T, Result<Integer>> convertToInteger(Function<T, Integer> func) {
    return Transformations.asCheckedFunc(func);
  }

  /**
   * @deprecated Use {@link #convertToIntegerResult()}.
   */
  @Deprecated(forRemoval = true)
  static <T>
  Function<T, Result<Integer>> convertToInteger() {
    return Converting
        .<T>convertToString()
        .andThen(s -> s.map(Integer::parseInt));
  }

  /**
   * @deprecated Use {@link #convertToDoubleResult(Function)}.
   */
  @Deprecated(forRemoval = true)
  @SuppressWarnings("removal")
  static <T>
  Function<T, Result<Double>> convertToDouble(Function<T, Double> func) {
    return Transformations.asCheckedFunc(func);
  }

  /**
   * @deprecated Use {@link #convertToDoubleResult()}.
   */
  @Deprecated(forRemoval = true)
  static <T>
  Function<T, Result<Double>> convertToDouble() {
    return Converting
        .<T>convertToString()
        .andThen(s -> s.map(Double::parseDouble));
  }

  // -------------------------------------------------------------------------
  // Internal helpers (modern path)
  // -------------------------------------------------------------------------

  private static <X> com.svenruppert.functional.result.Result<X, String> tryApply(
      Supplier<X> action) {
    try {
      return com.svenruppert.functional.result.Result.ofNullable(action.get(), "value was null");
    } catch (Exception e) {
      return com.svenruppert.functional.result.Result.failure(renderError(e));
    }
  }

  private static String renderError(Exception e) {
    String simpleName = e.getClass().getSimpleName();
    String message = e.getMessage();
    return message != null ? simpleName + " - " + message : simpleName + " - no message";
  }
}
