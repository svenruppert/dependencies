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
package junit.com.svenruppert.functional;

/*-
 * #%L
 * SRU - Functional
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2026 Sven Ruppert
 * %%
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
 * #L%
 */

import com.svenruppert.functional.Converting;
import com.svenruppert.functional.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConvertingModernTest {

  @Test
  @DisplayName("convertToStringResult() success on non-null input")
  void stringResultSuccess() {
    Result<String, String> r = Converting.<Integer>convertToStringResult().apply(1);
    assertTrue(r.isSuccess());
    assertEquals("1", r.getOrThrow());
  }

  @Test
  @DisplayName("convertToStringResult(func) success on custom mapping")
  void stringResultWithFunctionSuccess() {
    Result<String, String> r = Converting
        .<Integer>convertToStringResult(i -> "x" + i)
        .apply(7);
    assertTrue(r.isSuccess());
    assertEquals("x7", r.getOrThrow());
  }

  @Test
  @DisplayName("convertToStringResult(func) captures thrown RuntimeException")
  void stringResultWithFunctionFailure() {
    Result<String, String> r = Converting
        .<Integer>convertToStringResult(i -> { throw new RuntimeException("boom"); })
        .apply(1);
    assertTrue(r.isFailure());
    assertEquals("RuntimeException - boom", ((Result.Failure<String, String>) r).error());
  }

  @Test
  @DisplayName("convertToStringResult(func) maps null result to failure")
  void stringResultWithFunctionNullValue() {
    Result<String, String> r = Converting
        .<Integer>convertToStringResult(i -> null)
        .apply(1);
    assertTrue(r.isFailure());
    assertEquals("value was null", ((Result.Failure<String, String>) r).error());
  }

  @Test
  @DisplayName("convertToBooleanResult() success")
  void booleanResultSuccess() {
    Result<Boolean, String> rTrue =
        Converting.<String>convertToBooleanResult().apply("true");
    Result<Boolean, String> rFalse =
        Converting.<String>convertToBooleanResult().apply("false");
    assertTrue(rTrue.isSuccess());
    assertEquals(Boolean.TRUE, rTrue.getOrThrow());
    assertTrue(rFalse.isSuccess());
    assertEquals(Boolean.FALSE, rFalse.getOrThrow());
  }

  @Test
  @DisplayName("convertToBooleanResult() treats non-bool strings as false (Boolean.parseBoolean contract)")
  void booleanResultParsesNonBoolAsFalse() {
    Result<Boolean, String> r =
        Converting.<String>convertToBooleanResult().apply("hello");
    assertTrue(r.isSuccess());
    assertEquals(Boolean.FALSE, r.getOrThrow());
  }

  @Test
  @DisplayName("convertToBooleanResult(func) captures thrown RuntimeException")
  void booleanResultWithFunctionFailure() {
    Result<Boolean, String> r = Converting
        .<Integer>convertToBooleanResult(i -> { throw new IllegalStateException("nope"); })
        .apply(1);
    assertTrue(r.isFailure());
    assertEquals("IllegalStateException - nope", ((Result.Failure<Boolean, String>) r).error());
  }

  @Test
  @DisplayName("convertToIntegerResult() success on numeric string")
  void integerResultSuccess() {
    Result<Integer, String> r =
        Converting.<String>convertToIntegerResult().apply("42");
    assertTrue(r.isSuccess());
    assertEquals(42, r.getOrThrow());
  }

  @Test
  @DisplayName("convertToIntegerResult() captures NumberFormatException")
  void integerResultFailure() {
    Result<Integer, String> r =
        Converting.<String>convertToIntegerResult().apply("nope");
    assertTrue(r.isFailure());
    String err = ((Result.Failure<Integer, String>) r).error();
    assertTrue(err.startsWith("NumberFormatException - "), () -> "unexpected error: " + err);
  }

  @Test
  @DisplayName("convertToIntegerResult(func) success on custom mapping")
  void integerResultWithFunctionSuccess() {
    Result<Integer, String> r = Converting
        .<String>convertToIntegerResult(s -> s.length())
        .apply("hello");
    assertTrue(r.isSuccess());
    assertEquals(5, r.getOrThrow());
  }

  @Test
  @DisplayName("convertToDoubleResult() success on numeric string")
  void doubleResultSuccess() {
    Result<Double, String> r =
        Converting.<String>convertToDoubleResult().apply("3.14");
    assertTrue(r.isSuccess());
    assertEquals(3.14, r.getOrThrow());
  }

  @Test
  @DisplayName("convertToDoubleResult() captures NumberFormatException")
  void doubleResultFailure() {
    Result<Double, String> r =
        Converting.<String>convertToDoubleResult().apply("not-a-number");
    assertTrue(r.isFailure());
    String err = ((Result.Failure<Double, String>) r).error();
    assertTrue(err.startsWith("NumberFormatException - "), () -> "unexpected error: " + err);
  }

  @Test
  @DisplayName("convertToDoubleResult(func) success on custom mapping")
  void doubleResultWithFunctionSuccess() {
    Result<Double, String> r = Converting
        .<Integer>convertToDoubleResult(i -> i / 2.0)
        .apply(7);
    assertTrue(r.isSuccess());
    assertEquals(3.5, r.getOrThrow());
  }

  @Test
  @DisplayName("renderError uses 'no message' suffix when getMessage() returns null")
  void errorRenderingWithoutMessage() {
    Result<Integer, String> r = Converting
        .<Integer>convertToIntegerResult(i -> { throw new IllegalStateException(); })
        .apply(0);
    assertTrue(r.isFailure());
    assertEquals("IllegalStateException - no message",
        ((Result.Failure<Integer, String>) r).error());
  }
}
