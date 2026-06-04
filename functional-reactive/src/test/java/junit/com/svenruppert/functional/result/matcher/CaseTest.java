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
package junit.com.svenruppert.functional.result.matcher;

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
import com.svenruppert.functional.result.matcher.Case;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.svenruppert.functional.result.matcher.Case.match;
import static com.svenruppert.functional.result.matcher.Case.matchCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CaseTest {

  @Test
  @DisplayName("default case is returned when no matcher matches")
  void defaultCaseUsedWhenNoMatch() {
    Result<String, String> r = match(
        matchCase(() -> Result.success("default")),
        matchCase(() -> false, () -> Result.success("a")),
        matchCase(() -> false, () -> Result.success("b"))
    );
    assertEquals("default", r.getOrThrow());
  }

  @Test
  @DisplayName("first matching case wins")
  void firstMatchWins() {
    Result<String, String> r = match(
        matchCase(() -> Result.success("default")),
        matchCase(() -> true, () -> Result.success("first")),
        matchCase(() -> true, () -> Result.success("second"))
    );
    assertEquals("first", r.getOrThrow());
  }

  @Test
  @DisplayName("typed error: failure short-circuits cleanly")
  void typedErrorPath() {
    Result<Integer, String> r = match(
        matchCase(() -> Result.<Integer, String>failure("no fallback")),
        matchCase(() -> false, () -> Result.success(1)),
        matchCase(() -> true, () -> Result.<Integer, String>failure("nope"))
    );
    assertTrue(r.isFailure());
    assertEquals("nope", ((Result.Failure<Integer, String>) r).error());
  }

  @Test
  @DisplayName("lazy condition: only conditions up to the first match are evaluated")
  void conditionsEvaluatedLazily() {
    AtomicInteger c1 = new AtomicInteger();
    AtomicInteger c2 = new AtomicInteger();
    AtomicInteger c3 = new AtomicInteger();

    Result<String, String> r = match(
        matchCase(() -> Result.success("default")),
        matchCase(() -> { c1.incrementAndGet(); return false; }, () -> Result.success("a")),
        matchCase(() -> { c2.incrementAndGet(); return true; },  () -> Result.success("b")),
        matchCase(() -> { c3.incrementAndGet(); return true; },  () -> Result.success("c"))
    );

    assertEquals("b", r.getOrThrow());
    assertEquals(1, c1.get(), "first condition should be checked");
    assertEquals(1, c2.get(), "matching condition should be checked");
    assertEquals(0, c3.get(), "conditions after the match must not be evaluated");
  }

  @Test
  @DisplayName("lazy result: only the matching case's result supplier is invoked")
  void resultSupplierEvaluatedLazily() {
    AtomicInteger r1 = new AtomicInteger();
    AtomicInteger r2 = new AtomicInteger();
    AtomicInteger rDefault = new AtomicInteger();

    Result<String, String> result = match(
        matchCase(() -> { rDefault.incrementAndGet(); return Result.success("default"); }),
        matchCase(() -> true, () -> { r1.incrementAndGet(); return Result.success("first"); }),
        matchCase(() -> true, () -> { r2.incrementAndGet(); return Result.success("second"); })
    );

    assertEquals("first", result.getOrThrow());
    assertEquals(1, r1.get());
    assertEquals(0, r2.get(), "result supplier of non-matching cases must not be invoked");
    assertEquals(0, rDefault.get(), "default result supplier must not be invoked when a case matches");
  }

  @Test
  @DisplayName("default result supplier only runs when no case matches")
  void defaultRunsOnlyOnNoMatch() {
    AtomicInteger rDefault = new AtomicInteger();
    Result<String, String> r = match(
        matchCase(() -> { rDefault.incrementAndGet(); return Result.success("default"); }),
        matchCase(() -> false, () -> Result.success("a"))
    );
    assertEquals("default", r.getOrThrow());
    assertEquals(1, rDefault.get());
  }

  @Test
  @DisplayName("matchCase rejects null condition and null result supplier")
  void nullSuppliersRejected() {
    assertThrows(NullPointerException.class,
        () -> matchCase(null, () -> Result.success("x")));
    assertThrows(NullPointerException.class,
        () -> matchCase(() -> true, null));
    assertThrows(NullPointerException.class,
        () -> Case.<String, String>matchCase(null));
  }

  @Test
  @DisplayName("match rejects null default case")
  void matchRejectsNullDefault() {
    assertThrows(NullPointerException.class,
        () -> Case.<String, String>match(null));
  }

  @Test
  @DisplayName("match rejects an individual null case in the varargs")
  void matchRejectsNullCaseElement() {
    Case.DefaultCase<String, String> def = matchCase(() -> Result.success("d"));
    @SuppressWarnings("unchecked")
    Case<String, String>[] cases = (Case<String, String>[]) new Case[] { null };
    assertThrows(NullPointerException.class, () -> match(def, cases));
  }

  @Test
  @DisplayName("match rejects null returned from a case result supplier")
  void matchRejectsNullResultFromCase() {
    assertThrows(NullPointerException.class, () -> match(
        matchCase(() -> Result.<String, String>success("d")),
        matchCase(() -> true, () -> null)
    ));
  }
}
