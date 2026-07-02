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

import com.svenruppert.functional.result.Result;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultTest {

  private enum Err { NOT_FOUND, INVALID }

  @Test
  void successHoldsValueAndReportsState() {
    Result<String, Err> r = Result.success("hello");
    assertTrue(r.isSuccess());
    assertFalse(r.isFailure());
    assertInstanceOf(Result.Success.class, r);
    assertEquals("hello", ((Result.Success<String, Err>) r).value());
  }

  @Test
  void failureHoldsErrorAndReportsState() {
    Result<String, Err> r = Result.failure(Err.NOT_FOUND);
    assertFalse(r.isSuccess());
    assertTrue(r.isFailure());
    assertInstanceOf(Result.Failure.class, r);
    assertEquals(Err.NOT_FOUND, ((Result.Failure<String, Err>) r).error());
  }

  @Test
  void successRejectsNullValue() {
    assertThrows(NullPointerException.class, () -> Result.<String, Err>success(null));
  }

  @Test
  void failureRejectsNullError() {
    assertThrows(NullPointerException.class, () -> Result.<String, Err>failure(null));
  }

  @Test
  void ofNullableProducesSuccessForNonNull() {
    assertTrue(Result.ofNullable("v", Err.NOT_FOUND).isSuccess());
  }

  @Test
  void ofNullableProducesFailureForNull() {
    Result<String, Err> r = Result.ofNullable((String) null, Err.NOT_FOUND);
    assertTrue(r.isFailure());
    assertEquals(Err.NOT_FOUND, ((Result.Failure<String, Err>) r).error());
  }

  @Test
  void ofNullableWithSupplierLazilyEvaluatesError() {
    AtomicBoolean called = new AtomicBoolean();
    Result.ofNullable("v", () -> {
      called.set(true);
      return Err.NOT_FOUND;
    });
    assertFalse(called.get(), "supplier must not run when value is non-null");
  }

  @Test
  void getOrThrowReturnsValueOnSuccess() {
    assertEquals(42, Result.<Integer, Err>success(42).getOrThrow());
  }

  @Test
  void getOrThrowRaisesOnFailure() {
    Result<Integer, Err> r = Result.failure(Err.NOT_FOUND);
    NoSuchElementException ex = assertThrows(NoSuchElementException.class, r::getOrThrow);
    assertTrue(ex.getMessage().contains("NOT_FOUND"));
  }

  @Test
  void getOrElseReturnsFallbackOnFailure() {
    assertEquals(7, Result.<Integer, Err>failure(Err.NOT_FOUND).getOrElse(7));
  }

  @Test
  void getOrElseFromErrorComputesFromError() {
    int got = Result.<Integer, Err>failure(Err.INVALID).getOrElse(e -> e == Err.INVALID ? -1 : 0);
    assertEquals(-1, got);
  }

  @Test
  void foldDispatchesToSuccessBranch() {
    int got = Result.<Integer, Err>success(3).fold(v -> v * 2, e -> -1);
    assertEquals(6, got);
  }

  @Test
  void foldDispatchesToFailureBranch() {
    int got = Result.<Integer, Err>failure(Err.NOT_FOUND).fold(v -> v * 2, e -> -1);
    assertEquals(-1, got);
  }

  @Test
  void peekRunsOnSuccessOnly() {
    AtomicReference<String> seen = new AtomicReference<>();
    AtomicBoolean failureSeen = new AtomicBoolean();
    Result<String, Err> r = Result.success("hi");
    Result<String, Err> back = r.peek(seen::set).peekFailure(e -> failureSeen.set(true));
    assertEquals("hi", seen.get());
    assertFalse(failureSeen.get());
    assertSame(r, back, "peek/peekFailure must return the same instance for chaining");
  }

  @Test
  void peekFailureRunsOnFailureOnly() {
    AtomicReference<Err> seen = new AtomicReference<>();
    AtomicBoolean successSeen = new AtomicBoolean();
    Result<String, Err> r = Result.failure(Err.INVALID);
    r.peek(v -> successSeen.set(true)).peekFailure(seen::set);
    assertEquals(Err.INVALID, seen.get());
    assertFalse(successSeen.get());
  }

  @Test
  void recoverConvertsFailureToSuccess() {
    Result<Integer, Err> r = Result.<Integer, Err>failure(Err.NOT_FOUND).recover(e -> 0);
    assertTrue(r.isSuccess());
    assertEquals(0, r.getOrThrow());
  }

  @Test
  void recoverIsIdentityOnSuccess() {
    Result<Integer, Err> r = Result.success(5);
    assertSame(r, r.recover(e -> -1));
  }

  @Test
  void recoverWithCanReturnFailure() {
    Result<Integer, Err> r = Result.<Integer, Err>failure(Err.NOT_FOUND)
        .recoverWith(e -> Result.failure(Err.INVALID));
    assertTrue(r.isFailure());
    assertEquals(Err.INVALID, ((Result.Failure<Integer, Err>) r).error());
  }

  @Test
  void recoverWithRejectsNullResult() {
    Result<Integer, Err> r = Result.failure(Err.NOT_FOUND);
    assertThrows(NullPointerException.class, () -> r.recoverWith(e -> null));
  }

  @Test
  void mapTransformsValueOnSuccess() {
    Result<Integer, Err> r = Result.<String, Err>success("hello").map(String::length);
    assertEquals(5, r.getOrThrow());
  }

  @Test
  void mapPropagatesFailureUnchanged() {
    Result<Integer, Err> r = Result.<String, Err>failure(Err.NOT_FOUND).map(String::length);
    assertTrue(r.isFailure());
    assertEquals(Err.NOT_FOUND, ((Result.Failure<Integer, Err>) r).error());
  }

  @Test
  void flatMapChainsSuccess() {
    Result<Integer, Err> r = Result.<String, Err>success("42")
        .flatMap(s -> Result.success(Integer.parseInt(s)));
    assertEquals(42, r.getOrThrow());
  }

  @Test
  void flatMapPropagatesUpstreamFailure() {
    Result<Integer, Err> r = Result.<String, Err>failure(Err.INVALID)
        .flatMap(s -> Result.success(Integer.parseInt(s)));
    assertTrue(r.isFailure());
    assertEquals(Err.INVALID, ((Result.Failure<Integer, Err>) r).error());
  }

  @Test
  void flatMapCanProduceFailure() {
    Result<Integer, Err> r = Result.<String, Err>success("nope")
        .flatMap(s -> Result.failure(Err.INVALID));
    assertTrue(r.isFailure());
  }

  @Test
  void flatMapRejectsNullResult() {
    Result<String, Err> r = Result.success("x");
    assertThrows(NullPointerException.class, () -> r.flatMap(v -> null));
  }

  @Test
  void mapErrorTransformsFailureOnly() {
    Result<Integer, String> r = Result.<Integer, Err>failure(Err.INVALID).mapError(Enum::name);
    assertEquals("INVALID", ((Result.Failure<Integer, String>) r).error());
  }

  @Test
  void mapErrorIsIdentityOnSuccess() {
    Result<Integer, String> r = Result.<Integer, Err>success(1).mapError(Enum::name);
    assertEquals(1, r.getOrThrow());
  }

  @Test
  void toOptionalReflectsSuccessOrFailure() {
    assertEquals(Optional.of("hi"), Result.<String, Err>success("hi").toOptional());
    assertEquals(Optional.empty(), Result.<String, Err>failure(Err.NOT_FOUND).toOptional());
  }

  @Test
  void streamYieldsSingleElementOrEmpty() {
    assertEquals(1L, Result.<String, Err>success("x").stream().count());
    assertEquals(0L, Result.<String, Err>failure(Err.NOT_FOUND).stream().count());
  }

  @Test
  void switchExpressionExhaustivenessCompilesAndDispatches() {
    Result<Integer, Err> r = Result.success(10);
    String description = switch (r) {
      case Result.Success<Integer, Err>(Integer v) -> "ok=" + v;
      case Result.Failure<Integer, Err>(Err e) -> "err=" + e;
    };
    assertEquals("ok=10", description);
  }

  @Test
  void chainedPipelineWorks() {
    String got = Result.<String, Err>success(" 21 ")
        .map(String::trim)
        .map(Integer::parseInt)
        .map(i -> i * 2)
        .fold(v -> "value=" + v, e -> "err=" + e);
    assertEquals("value=42", got);
  }

  @Test
  void chainedPipelineShortCircuitsOnFailure() {
    AtomicBoolean reached = new AtomicBoolean();
    String got = Result.<String, Err>failure(Err.INVALID)
        .map(String::trim)
        .map(s -> {
          reached.set(true);
          return s.length();
        })
        .fold(v -> "value=" + v, Enum::name);
    assertEquals("INVALID", got);
    assertFalse(reached.get(), "downstream mappers must not run after a failure");
  }

  @Test
  void streamCanBeUsedInPipelines() {
    long count = Stream.of(
            Result.<Integer, Err>success(1),
            Result.<Integer, Err>failure(Err.NOT_FOUND),
            Result.<Integer, Err>success(3))
        .flatMap(Result::stream)
        .count();
    assertEquals(2L, count);
  }
}
