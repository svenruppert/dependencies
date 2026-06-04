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
package demo;

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
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Modern counterpart of {@link DemoResult}, exercising the
 * {@link Result Result&lt;T, E&gt;} API.
 */
public class DemoResultModern {

  @Test
  void constructionAndQueries() {
    Result<String, String> success = Result.success("ok");
    Result<Integer, String> failure = Result.failure("Ooops");

    assertTrue(success.isSuccess());
    assertTrue(failure.isFailure());

    assertEquals("ok", success.getOrThrow());
    assertEquals(0, failure.getOrElse(0));
  }

  @Test
  void ofNullableTreatsNullAsFailure() {
    Result<String, String> none = Result.ofNullable(null, "was null");
    Result<String, String> some = Result.ofNullable("value", "was null");
    assertTrue(none.isFailure());
    assertTrue(some.isSuccess());
  }

  @Test
  void mapAndFlatMapShortCircuitOnFailure() {
    Result<Integer, String> failure = Result.failure("nope");
    Result<Integer, String> mapped = failure.map(i -> i * 2);
    Result<Integer, String> flat = failure.flatMap(i -> Result.success(i + 1));

    assertTrue(mapped.isFailure());
    assertTrue(flat.isFailure());
  }

  @Test
  void mapErrorChangesTheErrorChannel() {
    Result<Integer, String> original = Result.failure("text");
    Result<Integer, Integer> mapped = original.mapError(String::length);
    assertEquals(4, ((Result.Failure<Integer, Integer>) mapped).error());
  }

  @Test
  void recoverProducesSuccessFromFailure() {
    Result<Integer, String> r = Result.<Integer, String>failure("err")
        .recover(err -> 42);
    assertTrue(r.isSuccess());
    assertEquals(42, r.getOrThrow());
  }

  @Test
  void recoverWithChainsAnotherResult() {
    Result<Integer, String> r = Result.<Integer, String>failure("primary")
        .recoverWith(err -> Result.success(7));
    assertEquals(7, r.getOrThrow());
  }

  @Test
  void foldConvertsToASingleValue() {
    String s = Result.<Integer, String>success(2)
        .fold(v -> "ok:" + v, e -> "err:" + e);
    assertEquals("ok:2", s);
  }

  @Test
  void toOptionalAndStreamReflectSuccess() {
    Optional<String> opt = Result.<String, String>success("x").toOptional();
    assertEquals(Optional.of("x"), opt);
    long count = Result.<String, String>success("x").stream().count();
    assertEquals(1, count);
    long emptyCount = Result.<String, String>failure("e").stream().count();
    assertEquals(0, emptyCount);
  }

  @Test
  void mapAsyncRunsOnSuppliedExecutor() {
    Result<Integer, String> seed = Result.success(3);
    CompletableFuture<Result<Integer, String>> fut =
        seed.mapAsync(i -> i * 10);
    Result<Integer, String> r = fut.join();
    assertEquals(30, r.getOrThrow());
  }

  @Test
  void typedErrorEnum() {
    enum Code { MISSING, INVALID }
    Result<Integer, Code> r = Result.failure(Code.MISSING);
    assertSame(Code.MISSING, ((Result.Failure<Integer, Code>) r).error());
  }
}
