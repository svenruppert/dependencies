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
package junit.com.svenruppert.functional.tuple;

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

import com.svenruppert.functional.tuple.Pair;
import com.svenruppert.functional.tuple.Quad;
import com.svenruppert.functional.tuple.Quint;
import com.svenruppert.functional.tuple.Sept;
import com.svenruppert.functional.tuple.Sext;
import com.svenruppert.functional.tuple.Single;
import com.svenruppert.functional.tuple.Triple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TupleRecordsTest {

  @Test
  void singleAccessorAndEquality() {
    Single<String> a = new Single<>("x");
    assertEquals("x", a.t1());
    assertEquals(a, new Single<>("x"));
    assertNotEquals(a, new Single<>("y"));
  }

  @Test
  void pairAccessorAndEquality() {
    Pair<String, Integer> a = new Pair<>("x", 1);
    assertEquals("x", a.t1());
    assertEquals(1, a.t2());
    assertEquals(a, new Pair<>("x", 1));
    assertNotEquals(a, new Pair<>("x", 2));
  }

  @Test
  void tripleAccessorAndEquality() {
    Triple<Integer, Integer, Integer> a = new Triple<>(1, 2, 3);
    assertEquals(1, a.t1());
    assertEquals(2, a.t2());
    assertEquals(3, a.t3());
    assertEquals(a, new Triple<>(1, 2, 3));
  }

  @Test
  void quadAccessorAndEquality() {
    Quad<Integer, Integer, Integer, Integer> a = new Quad<>(1, 2, 3, 4);
    assertEquals(4, a.t4());
    assertEquals(a, new Quad<>(1, 2, 3, 4));
  }

  @Test
  void quintAccessorAndEquality() {
    Quint<Integer, Integer, Integer, Integer, Integer> a = new Quint<>(1, 2, 3, 4, 5);
    assertEquals(5, a.t5());
    assertEquals(a, new Quint<>(1, 2, 3, 4, 5));
  }

  @Test
  void sextAccessorAndEquality() {
    Sext<Integer, Integer, Integer, Integer, Integer, Integer> a =
        new Sext<>(1, 2, 3, 4, 5, 6);
    assertEquals(6, a.t6());
    assertEquals(a, new Sext<>(1, 2, 3, 4, 5, 6));
  }

  @Test
  void septAccessorAndEquality() {
    Sept<Integer, Integer, Integer, Integer, Integer, Integer, Integer> a =
        new Sept<>(1, 2, 3, 4, 5, 6, 7);
    assertEquals(7, a.t7());
    assertEquals(a, new Sept<>(1, 2, 3, 4, 5, 6, 7));
  }

  @Test
  void recordPatternDeconstructionWorks() {
    Pair<String, Integer> p = new Pair<>("answer", 42);
    String formatted = switch (p) {
      case Pair<String, Integer>(String k, Integer v) -> k + "=" + v;
    };
    assertEquals("answer=42", formatted);
  }

  @Test
  void nullValuesAreAllowedInTuples() {
    Pair<String, String> p = new Pair<>(null, "x");
    assertEquals(null, p.t1());
    assertEquals("x", p.t2());
  }
}
