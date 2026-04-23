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

public class SextTest {

  @Test
  public void testEquals() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, 2, 3, 4, 5);

    assertTrue(sext.equals(sext));
    assertFalse(sext.equals(null));
    assertFalse(sext.equals(new DataRecords.Sext(null, 1, 2, 3, 4, 5)));
    assertFalse(sext.equals(new DataRecords.Sext(0, null, 2, 3, 4, 5)));
    assertFalse(sext.equals(new DataRecords.Sext(0, 1, null, 3, 4, 5)));
    assertFalse(sext.equals(new DataRecords.Sext(0, 1, 2, null, 4, 5)));
    assertFalse(sext.equals(new DataRecords.Sext(0, 1, 2, 3, null, 5)));
  }

  @Test
  public void testEqualsT1() {
    final DataRecords.Sext sext = new DataRecords.Sext(null, 1, 2, 3, 4, 5);

    assertTrue(sext.equals(new DataRecords.Sext(null, 1, 2, 3, 4, 5)));
    assertFalse(sext.equals(new DataRecords.Sext(0, 1, 2, 3, 4, 5)));
  }

  @Test
  public void testEqualsT2() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, null, 2, 3, 4, 5);

    assertTrue(sext.equals(new DataRecords.Sext(0, null, 2, 3, 4, 5)));
    assertFalse(sext.equals(new DataRecords.Sext(0, 1, 2, 3, 4, 5)));
  }

  @Test
  public void testEqualsT3() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, null, 3, 4, 5);

    assertTrue(sext.equals(new DataRecords.Sext(0, 1, null, 3, 4, 5)));
    assertFalse(sext.equals(new DataRecords.Sext(0, 1, 2, 3, 4, 5)));
  }

  @Test
  public void testEqualsT4() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, 2, null, 4, 5);

    assertTrue(sext.equals(new DataRecords.Sext(0, 1, 2, null, 4, 5)));
    assertFalse(sext.equals(new DataRecords.Sext(0, 1, 2, 3, 4, 5)));
  }

  @Test
  public void testEqualsT5() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, 2, 3, null, 5);

    assertTrue(sext.equals(new DataRecords.Sext(0, 1, 2, 3, null, 5)));
    assertFalse(sext.equals(new DataRecords.Sext(0, 1, 2, 3, 4, 5)));
  }

  @Test
  public void testEqualsT6() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, 2, 3, 4, null);

    assertTrue(sext.equals(new DataRecords.Sext(0, 1, 2, 3, 4, null)));
    assertFalse(sext.equals(new DataRecords.Sext(0, 1, 2, 3, 4, 5)));
  }

  @Test
  public void testHashCode() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, 2, 3, 4, 5);

    assertEquals(986115, sext.hashCode());
  }

  @Test
  public void testHashCodeT1() {
    final DataRecords.Sext sext = new DataRecords.Sext(null, 1, 2, 3, 4, 5);

    assertEquals(986115, sext.hashCode());
  }

  @Test
  public void testHashCodeT2() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, null, 2, 3, 4, 5);

    assertEquals(62594, sext.hashCode());
  }

  @Test
  public void testHashCodeT3() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, null, 3, 4, 5);

    assertEquals(926533, sext.hashCode());
  }

  @Test
  public void testHashCodeT4() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, 2, null, 4, 5);

    assertEquals(983232, sext.hashCode());
  }

  @Test
  public void testHashCodeT5() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, 2, 3, null, 5);

    assertEquals(985991, sext.hashCode());
  }

  @Test
  public void testHashCodeT6() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, 2, 3, 4, null);

    assertEquals(986110, sext.hashCode());
  }

  @Test
  public void testNext() {
    final DataRecords.Sext sext = DataRecords.Sext.next(0, 1, 2, 3, 4, 5);

    assertNotNull(sext);
    assertEquals(0, sext.getT1());
    assertEquals(1, sext.getT2());
    assertEquals(2, sext.getT3());
    assertEquals(3, sext.getT4());
    assertEquals(4, sext.getT5());
    assertEquals(5, sext.getT6());
  }

  @Test
  public void testToString() {
    final DataRecords.Sext sext = new DataRecords.Sext(0, 1, 2, 3, 4, 5);

    assertEquals("Sext{t1=0, t2=1, t3=2, t4=3, t5=4, t6=5}", sext.toString());
  }
}