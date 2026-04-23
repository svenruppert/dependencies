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

import com.svenruppert.functional.reactive.CompletableFutureQueue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class DemoCompletableFutureQueue {


  @Test
  void test001() {
    Function<Integer, CompletableFuture<Integer>> f = CompletableFutureQueue
        .<Integer, Integer>define((a) -> a + 10)
        .thenCombineAsync((b) -> b + 20)
        .thenCombineAsync((c) -> c + 30)
        .resultFunction();

    f.apply(1)
     .thenAcceptAsync(System.out::println)
     .join();
  }

  @Test
  void test002() {
    CompletableFutureQueue
        .<Integer, Integer>define((a) -> a + 10)
        .thenCombineAsync((b) -> b + 20)
        .thenCombineAsync((c) -> c + 30)
        .resultFunction()
        .apply(1)
        .thenAcceptAsync(System.out::println)
        .join();
  }
}