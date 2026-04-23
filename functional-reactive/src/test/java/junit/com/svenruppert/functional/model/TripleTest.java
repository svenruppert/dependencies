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

import com.svenruppert.functional.model.DataRecords;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TripleTest {

  @Test
  public void testEquals() {
    final DataRecords.Triple triple = new DataRecords.Triple(0, 1, 2);

    assertTrue(triple.equals(triple));
    assertFalse(triple.equals(null));
    assertFalse(triple.equals(new DataRecords.Triple(null, 1, 2)));
    assertFalse(triple.equals(new DataRecords.Triple(0, null, 2)));
  }

  @Test
  public void testEqualsT1() {
    final DataRecords.Triple triple = new DataRecords.Triple(null, 1, 2);

    assertTrue(triple.equals(new DataRecords.Triple(null, 1, 2)));
    assertFalse(triple.equals(new DataRecords.Triple(0, 1, 2)));
  }

  @Test
  public void testEqualsT2() {
    final DataRecords.Triple triple = new DataRecords.Triple(0, null, 2);

    assertTrue(triple.equals(new DataRecords.Triple(0, null, 2)));
    assertFalse(triple.equals(new DataRecords.Triple(0, 1, 2)));
  }

  @Test
  public void testEqualsT3() {
    final DataRecords.Triple triple = new DataRecords.Triple(0, 1, null);

    assertTrue(triple.equals(new DataRecords.Triple(0, 1, null)));
    assertFalse(triple.equals(new DataRecords.Triple(0, 1, 2)));
  }

  @Test
  public void testHashCode() {
    final DataRecords.Triple triple = new DataRecords.Triple(0, 1, 2);

    assertEquals(33, triple.hashCode());
  }

  @Test
  public void testHashCodeT1() {
    final DataRecords.Triple triple = new DataRecords.Triple(null, 1, 2);

    assertEquals(33, triple.hashCode());
  }

  @Test
  public void testHashCodeT2() {
    final DataRecords.Triple triple = new DataRecords.Triple(0, null, 2);

    assertEquals(2, triple.hashCode());
  }

  @Test
  public void testHashCodeT3() {
    final DataRecords.Triple triple = new DataRecords.Triple(0, 1, null);

    assertEquals(31, triple.hashCode());
  }

  @Test
  public void testNext() {
    final DataRecords.Triple triple = DataRecords.Triple.next(0, 1, 2);

    assertNotNull(triple);
    assertEquals(0, triple.getT1());
    assertEquals(1, triple.getT2());
    assertEquals(2, triple.getT3());
  }

  @Test
  public void testToString() {
    final DataRecords.Triple triple = new DataRecords.Triple(0, 1, 2);

    assertEquals("Triple{t1=0, t2=1, t3=2}", triple.toString());
  }
}