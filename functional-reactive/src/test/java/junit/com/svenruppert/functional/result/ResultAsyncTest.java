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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultAsyncTest {

  @Test
  void mapAsyncRunsMapperOnSuppliedExecutor() throws Exception {
    ExecutorService pool = Executors.newSingleThreadExecutor(r -> {
      Thread t = new Thread(r, "result-async-test");
      t.setDaemon(true);
      return t;
    });
    try {
      AtomicReference<String> threadName = new AtomicReference<>();
      Result<Integer, String> mapped = Result.<Integer, String>success(21)
          .mapAsync(v -> {
            threadName.set(Thread.currentThread().getName());
            return v * 2;
          }, pool)
          .get(2, TimeUnit.SECONDS);

      assertTrue(mapped.isSuccess());
      assertEquals(42, mapped.getOrThrow());
      assertEquals("result-async-test", threadName.get());
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void mapAsyncShortCircuitsFailureWithoutScheduling() throws Exception {
    AtomicBoolean ran = new AtomicBoolean();
    ExecutorService pool = Executors.newSingleThreadExecutor();
    try {
      Result<Integer, String> mapped = Result.<Integer, String>failure("boom")
          .mapAsync(v -> {
            ran.set(true);
            return v * 2;
          }, pool)
          .get(2, TimeUnit.SECONDS);
      assertTrue(mapped.isFailure());
      assertEquals("boom", ((Result.Failure<Integer, String>) mapped).error());
      assertFalse(ran.get(), "mapper must not run when upstream is a failure");
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void mapAsyncWithoutExecutorUsesDefaultPool() throws Exception {
    Result<Integer, String> mapped = Result.<Integer, String>success(7)
        .mapAsync(v -> v + 1)
        .get(2, TimeUnit.SECONDS);
    assertEquals(8, mapped.getOrThrow());
  }

  @Test
  void flatMapAsyncComposesFutures() throws Exception {
    ExecutorService pool = Executors.newSingleThreadExecutor();
    try {
      Result<String, String> got = Result.<Integer, String>success(21)
          .flatMapAsync(v -> CompletableFuture.completedFuture(Result.success("=" + (v * 2))), pool)
          .get(2, TimeUnit.SECONDS);
      assertEquals("=42", got.getOrThrow());
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void flatMapAsyncPropagatesInnerFailure() throws Exception {
    Result<String, String> got = Result.<Integer, String>success(1)
        .flatMapAsync(v -> CompletableFuture.completedFuture(Result.<String, String>failure("inner")))
        .get(2, TimeUnit.SECONDS);
    assertTrue(got.isFailure());
    assertEquals("inner", ((Result.Failure<String, String>) got).error());
  }

  @Test
  void flatMapAsyncShortCircuitsUpstreamFailure() throws Exception {
    AtomicBoolean ran = new AtomicBoolean();
    Result<String, String> got = Result.<Integer, String>failure("upstream")
        .flatMapAsync(v -> {
          ran.set(true);
          return CompletableFuture.completedFuture(Result.success("x"));
        })
        .get(2, TimeUnit.SECONDS);
    assertTrue(got.isFailure());
    assertEquals("upstream", ((Result.Failure<String, String>) got).error());
    assertFalse(ran.get(), "flatMap mapper must not run when upstream is a failure");
  }
}
