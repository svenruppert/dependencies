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
import com.svenruppert.functional.result.functions.CheckedBiFunction;
import com.svenruppert.functional.result.functions.CheckedFunction;
import com.svenruppert.functional.result.functions.CheckedSupplier;
import com.svenruppert.functional.result.functions.CheckedTriFunction;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckedFunctionsTest {

  @Test
  void checkedSupplierWrapsCheckedException() {
    IOException cause = new IOException("io");
    CheckedSupplier<Integer> s = () -> { throw cause; };
    Result<Integer, Throwable> r = s.get();
    assertTrue(r.isFailure());
    assertSame(cause, ((Result.Failure<Integer, Throwable>) r).error());
  }

  @Test
  void checkedSupplierPassesThroughSuccess() {
    CheckedSupplier<Integer> s = () -> 42;
    assertEquals(42, s.get().getOrThrow());
  }

  @Test
  void checkedSupplierLetsErrorsPropagate() {
    CheckedSupplier<Integer> s = () -> { throw new OutOfMemoryError("fake"); };
    assertThrows(OutOfMemoryError.class, s::get);
  }

  @Test
  void checkedFunctionWrapsThrownException() {
    CheckedFunction<String, Integer> parse = Integer::parseInt;
    Result<Integer, Throwable> ok = parse.apply("21");
    assertEquals(21, ok.getOrThrow());

    Result<Integer, Throwable> bad = parse.apply("nope");
    assertTrue(bad.isFailure());
    assertInstanceOf(NumberFormatException.class,
        ((Result.Failure<Integer, Throwable>) bad).error());
  }

  @Test
  void checkedBiFunctionWrapsThrownException() {
    CheckedBiFunction<Integer, Integer, Integer> div = (a, b) -> a / b;
    assertEquals(5, div.apply(10, 2).getOrThrow());
    Result<Integer, Throwable> bad = div.apply(10, 0);
    assertTrue(bad.isFailure());
    assertInstanceOf(ArithmeticException.class,
        ((Result.Failure<Integer, Throwable>) bad).error());
  }

  @Test
  void checkedTriFunctionWrapsThrownException() {
    CheckedTriFunction<Integer, Integer, Integer, Integer> sumOrThrow = (a, b, c) -> {
      if (c == 0) throw new IllegalStateException("c is zero");
      return a + b + c;
    };
    assertEquals(6, sumOrThrow.apply(1, 2, 3).getOrThrow());
    Result<Integer, Throwable> bad = sumOrThrow.apply(1, 2, 0);
    assertTrue(bad.isFailure());
    assertInstanceOf(IllegalStateException.class,
        ((Result.Failure<Integer, Throwable>) bad).error());
  }
}
