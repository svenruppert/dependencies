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
package junit.com.svenruppert.functional.model;

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

import com.svenruppert.functional.model.Result;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultFailurePropagationTest {

  private static final String ORIGINAL_MESSAGE = "original failure";

  @Test
  void mapOnFailureStaysFailureAndDoesNotInvokeMapper() {
    AtomicBoolean mapperCalled = new AtomicBoolean(false);
    Result<String> failure = Result.failure(ORIGINAL_MESSAGE);

    Result<Integer> mapped = failure.map(s -> {
      mapperCalled.set(true);
      return s.length();
    });

    assertFalse(mapperCalled.get(), "map function must not run on a failure");
    assertTrue(mapped.isFailure(), "result of map on failure must remain a failure");
  }

  @Test
  void mapOnFailurePreservesOriginalErrorMessage() {
    Result<String> failure = Result.failure(ORIGINAL_MESSAGE);
    Result<Integer> mapped = failure.map(String::length);

    AtomicReference<String> seenMessage = new AtomicReference<>();
    mapped.ifFailed(seenMessage::set);
    assertEquals(ORIGINAL_MESSAGE, seenMessage.get(),
        "map on failure must preserve the original error message");
  }

  @Test
  void flatMapOnFailureStaysFailureAndDoesNotInvokeMapper() {
    AtomicBoolean mapperCalled = new AtomicBoolean(false);
    Result<String> failure = Result.failure(ORIGINAL_MESSAGE);

    Result<Integer> mapped = failure.flatMap(s -> {
      mapperCalled.set(true);
      return Result.success(s.length());
    });

    assertFalse(mapperCalled.get(), "flatMap function must not run on a failure");
    assertTrue(mapped.isFailure(), "result of flatMap on failure must remain a failure");
  }

  @Test
  void flatMapOnFailurePreservesOriginalErrorMessage() {
    Result<String> failure = Result.failure(ORIGINAL_MESSAGE);
    Result<Integer> mapped = failure.flatMap(s -> Result.success(s.length()));

    AtomicReference<String> seenMessage = new AtomicReference<>();
    mapped.ifFailed(seenMessage::set);
    assertEquals(ORIGINAL_MESSAGE, seenMessage.get(),
        "flatMap on failure must preserve the original error message");
  }

  @Test
  void thenCombineOnFailureDoesNotInvokeCombinerAndStaysFailure() {
    AtomicBoolean combinerCalled = new AtomicBoolean(false);
    Result<String> failure = Result.failure(ORIGINAL_MESSAGE);

    Result<String> combined = failure.thenCombine("X", (a, b) -> {
      combinerCalled.set(true);
      return Result.success(a + b);
    });

    assertFalse(combinerCalled.get(), "thenCombine combiner must not run on a failure");
    assertTrue(combined.isFailure(),
        "thenCombine on failure must short-circuit to a failure, not throw");
  }

  @Test
  void thenCombineFlatOnFailureDoesNotInvokeCombinerAndStaysFailure() {
    AtomicBoolean combinerCalled = new AtomicBoolean(false);
    Result<String> failure = Result.failure(ORIGINAL_MESSAGE);

    Result<String> combined = failure.thenCombineFlat("X", (a, b) -> {
      combinerCalled.set(true);
      return a + b;
    });

    assertFalse(combinerCalled.get(), "thenCombineFlat combiner must not run on a failure");
    assertTrue(combined.isFailure(),
        "thenCombineFlat on failure must short-circuit to a failure, not throw");
  }

  @Test
  void ifPresentOnFailureIsNotInvoked() {
    AtomicBoolean called = new AtomicBoolean(false);
    Result<String> failure = Result.failure(ORIGINAL_MESSAGE);

    failure.ifPresent(v -> called.set(true));

    assertFalse(called.get(), "ifPresent must not invoke consumer on a failure");
  }

  @Test
  void ifFailedOnFailureReceivesOriginalMessage() {
    AtomicReference<String> seen = new AtomicReference<>();
    Result.<String>failure(ORIGINAL_MESSAGE).ifFailed(seen::set);
    assertEquals(ORIGINAL_MESSAGE, seen.get());
  }

  @Test
  void ifFailedOnSuccessIsNotInvoked() {
    AtomicBoolean called = new AtomicBoolean(false);
    Result.success("ok").ifFailed(msg -> called.set(true));
    assertFalse(called.get(), "ifFailed must not invoke consumer on a success");
  }

  @Test
  void mapChainShortCircuitsAtFirstFailure() {
    AtomicBoolean secondMapperCalled = new AtomicBoolean(false);
    Result<String> failure = Result.failure(ORIGINAL_MESSAGE);

    Result<String> chained = failure
        .map(String::trim)
        .map(s -> {
          secondMapperCalled.set(true);
          return s + "!";
        });

    assertFalse(secondMapperCalled.get(),
        "downstream map must not run after upstream failure");
    assertTrue(chained.isFailure());
  }
}
