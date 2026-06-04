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

import com.svenruppert.functional.StringFunctions;
import com.svenruppert.functional.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringFunctionsAtResultTest {

  @Test
  @DisplayName("atResult: success on valid 1-based index")
  void successFirstChar() {
    Result<String, String> r = StringFunctions.atResult().apply("foobar", 1);
    assertTrue(r.isSuccess());
    assertEquals("f", r.getOrThrow());
  }

  @Test
  @DisplayName("atResult: success on second char (1-based)")
  void successSecondChar() {
    Result<String, String> r = StringFunctions.atResult().apply("foobar", 2);
    assertTrue(r.isSuccess());
    assertEquals("o", r.getOrThrow());
  }

  @Test
  @DisplayName("atResult: success on last char")
  void successLastChar() {
    Result<String, String> r = StringFunctions.atResult().apply("ABCD", 4);
    assertTrue(r.isSuccess());
    assertEquals("D", r.getOrThrow());
  }

  @Test
  @DisplayName("atResult: null value yields failure (no NPE)")
  void nullValue() {
    Result<String, String> r = StringFunctions.atResult().apply(null, 1);
    assertTrue(r.isFailure());
    assertEquals("value should not be null", ((Result.Failure<String, String>) r).error());
  }

  @Test
  @DisplayName("atResult: empty value yields failure")
  void emptyValue() {
    Result<String, String> r = StringFunctions.atResult().apply("", 1);
    assertTrue(r.isFailure());
    assertEquals("value should not be empty", ((Result.Failure<String, String>) r).error());
  }

  @Test
  @DisplayName("atResult: index above length yields 'out of bounds'")
  void indexAboveLength() {
    Result<String, String> r = StringFunctions.atResult().apply("ABCD", 5);
    assertTrue(r.isFailure());
    assertEquals("index out of bounds", ((Result.Failure<String, String>) r).error());
  }

  @Test
  @DisplayName("atResult: index zero yields 'out of lower bounds'")
  void indexZero() {
    Result<String, String> r = StringFunctions.atResult().apply("ABCD", 0);
    assertTrue(r.isFailure());
    assertEquals("index out of lower bounds", ((Result.Failure<String, String>) r).error());
  }

  @Test
  @DisplayName("atResult: negative index yields 'out of lower bounds'")
  void indexNegative() {
    Result<String, String> r = StringFunctions.atResult().apply("ABCD", -5);
    assertTrue(r.isFailure());
    assertEquals("index out of lower bounds", ((Result.Failure<String, String>) r).error());
  }
}
