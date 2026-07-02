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
package junit.com.svenruppert.functional.reactive;

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

import com.svenruppert.functional.reactive.CompletableFutureQueue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompletableFutureQueueTest {

  @Test
  void defineProducesCompletedFutureWithTransformedValue() throws Exception {
    CompletableFutureQueue<Integer, Integer> queue =
        CompletableFutureQueue.define(i -> i + 1);

    CompletableFuture<Integer> future = queue.resultFunction().apply(41);

    assertEquals(42, future.get(2, TimeUnit.SECONDS));
  }

  @Test
  void thenCombineAsyncChainsTransformations() throws Exception {
    CompletableFutureQueue<Integer, Integer> queue =
        CompletableFutureQueue
            .<Integer, Integer>define(i -> i + 1)
            .thenCombineAsync(i -> i * 2)
            .thenCombineAsync(i -> i - 3);

    Integer result = queue.resultFunction().apply(10).get(2, TimeUnit.SECONDS);

    assertEquals(((10 + 1) * 2) - 3, result);
  }

  @Test
  void exceptionInDefineStageCompletesFutureExceptionally() {
    CompletableFutureQueue<Integer, Integer> queue =
        CompletableFutureQueue.define((Function<Integer, Integer>) i -> {
          throw new IllegalStateException("boom-define");
        });

    ExecutionException ex = assertThrows(ExecutionException.class,
        () -> queue.resultFunction().apply(1).get(2, TimeUnit.SECONDS));

    assertInstanceOf(IllegalStateException.class, ex.getCause(),
        "exception thrown synchronously in define must surface as cause");
    assertEquals("boom-define", ex.getCause().getMessage());
  }

  @Test
  void exceptionInChainedStageCompletesFutureExceptionally() {
    CompletableFutureQueue<Integer, Integer> queue =
        CompletableFutureQueue
            .<Integer, Integer>define(i -> i + 1)
            .thenCombineAsync(i -> {
              throw new IllegalStateException("boom-chained");
            });

    ExecutionException ex = assertThrows(ExecutionException.class,
        () -> queue.resultFunction().apply(1).get(2, TimeUnit.SECONDS));

    Throwable cause = ex.getCause();
    // The async stage wraps in CompletionException; the originating cause must remain reachable.
    while (cause instanceof CompletionException && cause.getCause() != null) {
      cause = cause.getCause();
    }
    assertInstanceOf(IllegalStateException.class, cause);
    assertEquals("boom-chained", cause.getMessage());
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  void thenCombineAsyncFromArrayAppliesAllTransformationsInOrder() throws Exception {
    Function<Integer, Integer>[] steps = new Function[] {
        (Function<Integer, Integer>) i -> i + 1,
        (Function<Integer, Integer>) i -> i * 10,
    };

    CompletableFutureQueue<Integer, Integer> queue =
        CompletableFutureQueue
            .<Integer, Integer>define(i -> i)
            .thenCombineAsyncFromArray(steps);

    Integer result = queue.resultFunction().apply(2).get(2, TimeUnit.SECONDS);
    assertEquals((2 + 1) * 10, result);
  }

  @Test
  void resultFunctionIsReusableAcrossInvocations() throws Exception {
    CompletableFutureQueue<Integer, Integer> queue =
        CompletableFutureQueue
            .<Integer, Integer>define(i -> i + 1)
            .thenCombineAsync(i -> i * 2);

    Function<Integer, CompletableFuture<Integer>> fn = queue.resultFunction();

    assertEquals(4, fn.apply(1).get(2, TimeUnit.SECONDS));
    assertEquals(6, fn.apply(2).get(2, TimeUnit.SECONDS));
    assertEquals(8, fn.apply(3).get(2, TimeUnit.SECONDS));
  }

  @Test
  void thenCombineAsyncWithExecutorRunsOnSuppliedThreadPool() throws Exception {
    java.util.concurrent.ExecutorService pool =
        java.util.concurrent.Executors.newSingleThreadExecutor(r -> {
          Thread t = new Thread(r, "cfq-executor-test");
          t.setDaemon(true);
          return t;
        });
    try {
      java.util.concurrent.atomic.AtomicReference<String> stageThread =
          new java.util.concurrent.atomic.AtomicReference<>();
      CompletableFutureQueue<Integer, Integer> queue =
          CompletableFutureQueue
              .<Integer, Integer>define(i -> i + 1)
              .thenCombineAsync(i -> {
                stageThread.set(Thread.currentThread().getName());
                return i * 2;
              }, pool);

      Integer result = queue.resultFunction().apply(20).get(2, TimeUnit.SECONDS);
      assertEquals(42, result);
      assertEquals("cfq-executor-test", stageThread.get(),
          "stage must execute on the supplied executor, not the common pool");
    } finally {
      pool.shutdownNow();
    }
  }
}
