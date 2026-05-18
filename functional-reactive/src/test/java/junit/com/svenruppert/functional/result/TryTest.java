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
import com.svenruppert.functional.result.Try;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TryTest {

  @Test
  void ofProducesSuccessWhenSupplierReturnsValue() {
    Result<Integer, Throwable> r = Try.of(() -> 42);
    assertTrue(r.isSuccess());
    assertEquals(42, r.getOrThrow());
  }

  @Test
  void ofProducesFailureWithOriginalThrowableForCheckedException() {
    IOException cause = new IOException("boom");
    Result<Integer, Throwable> r = Try.of(() -> { throw cause; });
    assertTrue(r.isFailure());
    Throwable caught = ((Result.Failure<Integer, Throwable>) r).error();
    assertSame(cause, caught, "Try.of must preserve the original throwable instance");
  }

  @Test
  void ofProducesFailureForRuntimeException() {
    Result<Integer, Throwable> r = Try.of(() -> { throw new IllegalStateException("nope"); });
    assertTrue(r.isFailure());
    Throwable caught = ((Result.Failure<Integer, Throwable>) r).error();
    assertInstanceOf(IllegalStateException.class, caught);
    assertEquals("nope", caught.getMessage());
  }

  @Test
  void ofPreservesStacktrace() {
    Result<Integer, Throwable> r = Try.of(() -> { throw new RuntimeException("trace-me"); });
    Throwable caught = ((Result.Failure<Integer, Throwable>) r).error();
    assertTrue(caught.getStackTrace().length > 0, "stacktrace must be preserved");
  }

  @Test
  void ofRethrowsErrorsRatherThanCapturingThem() {
    OutOfMemoryError oom = new OutOfMemoryError("fake");
    OutOfMemoryError thrown = assertThrows(OutOfMemoryError.class,
        () -> Try.of(() -> { throw oom; }));
    assertSame(oom, thrown, "Errors must propagate; only Throwables below Error are captured");
  }

  @Test
  void ofComposesWithResultPipeline() {
    String got = Try.of(() -> Integer.parseInt("21"))
        .map(i -> i * 2)
        .mapError(Throwable::getMessage)
        .fold(v -> "value=" + v, m -> "err=" + m);
    assertEquals("value=42", got);
  }

  @Test
  void ofComposesPipelineOnFailure() {
    String got = Try.of(() -> Integer.parseInt("not-a-number"))
        .map(i -> i * 2)
        .mapError(Throwable::getClass)
        .fold(v -> "value=" + v, c -> "err=" + c.getSimpleName());
    assertEquals("err=NumberFormatException", got);
  }
}
