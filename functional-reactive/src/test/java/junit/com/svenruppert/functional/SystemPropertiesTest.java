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

import com.svenruppert.functional.SystemProperties;
import com.svenruppert.functional.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemPropertiesTest {

  private static final String KEY = "exampleKey";
  private static final String QUALIFIED = SystemPropertiesTest.class.getName() + "." + KEY;

  private static void withProperty(String value, Runnable body) {
    String prev = System.getProperty(QUALIFIED);
    System.setProperty(QUALIFIED, value);
    try {
      body.run();
    } finally {
      if (prev != null) {
        System.setProperty(QUALIFIED, prev);
      } else {
        System.clearProperty(QUALIFIED);
      }
    }
  }

  private static void withoutProperty(Runnable body) {
    String prev = System.getProperty(QUALIFIED);
    System.clearProperty(QUALIFIED);
    try {
      body.run();
    } finally {
      if (prev != null) {
        System.setProperty(QUALIFIED, prev);
      }
    }
  }

  @Test
  @DisplayName("systemPropertyResult(Class) Success when present")
  void stringPresent() {
    withProperty("hello", () -> {
      Result<String, String> r =
          SystemProperties.systemPropertyResult(SystemPropertiesTest.class).apply(KEY);
      assertTrue(r.isSuccess());
      assertEquals("hello", r.getOrThrow());
    });
  }

  @Test
  @DisplayName("systemPropertyResult(Class) Failure when missing")
  void stringMissing() {
    withoutProperty(() -> {
      Result<String, String> r =
          SystemProperties.systemPropertyResult(SystemPropertiesTest.class).apply(KEY);
      assertTrue(r.isFailure());
      String err = ((Result.Failure<String, String>) r).error();
      assertTrue(err.contains(QUALIFIED), () -> "error did not mention key: " + err);
    });
  }

  @Test
  @DisplayName("systemPropertyResult(Class, default) returns default when missing")
  void stringDefaultUsed() {
    withoutProperty(() -> {
      Result<String, String> r = SystemProperties
          .systemPropertyResult(SystemPropertiesTest.class, "fallback")
          .apply(KEY);
      assertTrue(r.isSuccess());
      assertEquals("fallback", r.getOrThrow());
    });
  }

  @Test
  @DisplayName("systemPropertyResult() BiFunction qualifies key correctly")
  void biFunctionQualifies() {
    withProperty("biValue", () -> {
      Result<String, String> r =
          SystemProperties.systemPropertyResult().apply(SystemPropertiesTest.class, KEY);
      assertTrue(r.isSuccess());
      assertEquals("biValue", r.getOrThrow());
    });
  }

  @Test
  @DisplayName("systemPropertyBooleanResult Success on 'true'")
  void booleanTrue() {
    withProperty("true", () -> {
      Result<Boolean, String> r =
          SystemProperties.systemPropertyBooleanResult(SystemPropertiesTest.class).apply(KEY);
      assertTrue(r.isSuccess());
      assertEquals(Boolean.TRUE, r.getOrThrow());
    });
  }

  @Test
  @DisplayName("systemPropertyBooleanResult Failure when key missing")
  void booleanMissing() {
    withoutProperty(() -> {
      Result<Boolean, String> r =
          SystemProperties.systemPropertyBooleanResult(SystemPropertiesTest.class).apply(KEY);
      assertTrue(r.isFailure());
    });
  }

  @Test
  @DisplayName("systemPropertyBooleanResult with default returns default-parsed value")
  void booleanWithDefault() {
    withoutProperty(() -> {
      Result<Boolean, String> r = SystemProperties
          .systemPropertyBooleanResult(SystemPropertiesTest.class, "true")
          .apply(KEY);
      assertTrue(r.isSuccess());
      assertEquals(Boolean.TRUE, r.getOrThrow());
    });
  }

  @Test
  @DisplayName("systemPropertyIntResult Success on numeric")
  void intSuccess() {
    withProperty("123", () -> {
      Result<Integer, String> r =
          SystemProperties.systemPropertyIntResult(SystemPropertiesTest.class).apply(KEY);
      assertTrue(r.isSuccess());
      assertEquals(123, r.getOrThrow());
    });
  }

  @Test
  @DisplayName("systemPropertyIntResult Failure on non-numeric value")
  void intParseFailure() {
    withProperty("nope", () -> {
      Result<Integer, String> r =
          SystemProperties.systemPropertyIntResult(SystemPropertiesTest.class).apply(KEY);
      assertTrue(r.isFailure());
      String err = ((Result.Failure<Integer, String>) r).error();
      assertTrue(err.startsWith("NumberFormatException - "), () -> "unexpected error: " + err);
    });
  }

  @Test
  @DisplayName("systemPropertyIntResult with default returns parsed default")
  void intDefault() {
    withoutProperty(() -> {
      Result<Integer, String> r = SystemProperties
          .systemPropertyIntResult(SystemPropertiesTest.class, "42")
          .apply(KEY);
      assertTrue(r.isSuccess());
      assertEquals(42, r.getOrThrow());
    });
  }

  @Test
  @DisplayName("systemPropertyDoubleResult Success on numeric")
  void doubleSuccess() {
    withProperty("3.14", () -> {
      Result<Double, String> r =
          SystemProperties.systemPropertyDoubleResult(SystemPropertiesTest.class).apply(KEY);
      assertTrue(r.isSuccess());
      assertEquals(3.14, r.getOrThrow());
    });
  }

  @Test
  @DisplayName("systemPropertyDoubleResult Failure on garbage value")
  void doubleParseFailure() {
    withProperty("garbage", () -> {
      Result<Double, String> r =
          SystemProperties.systemPropertyDoubleResult(SystemPropertiesTest.class).apply(KEY);
      assertTrue(r.isFailure());
    });
  }

  @Test
  @DisplayName("systemPropertyDoubleResult with default returns parsed default")
  void doubleDefault() {
    withoutProperty(() -> {
      Result<Double, String> r = SystemProperties
          .systemPropertyDoubleResult(SystemPropertiesTest.class, "1.5")
          .apply(KEY);
      assertTrue(r.isSuccess());
      assertEquals(1.5, r.getOrThrow());
    });
  }
}
