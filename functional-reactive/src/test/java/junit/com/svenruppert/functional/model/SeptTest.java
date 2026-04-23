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

public class SeptTest {

  @Test
  public void testEquals() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, 3, 4, 5, 6);

    assertTrue(sept.equals(sept));
    assertFalse(sept.equals(null));
    assertFalse(sept.equals(new DataRecords.Sept(null, 1, 2, 3, 4, 5, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, null, 2, 3, 4, 5, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, null, 3, 4, 5, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, 2, null, 4, 5, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, 2, 3, null, 5, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, 2, 3, 4, null, 6)));
  }

  @Test
  public void testEqualsT1() {
    final DataRecords.Sept sept = new DataRecords.Sept(null, 1, 2, 3, 4, 5, 6);

    assertTrue(sept.equals(new DataRecords.Sept(null, 1, 2, 3, 4, 5, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, 2, 3, 4, 5, 6)));
  }

  @Test
  public void testEqualsT2() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, null, 2, 3, 4, 5, 6);

    assertTrue(sept.equals(new DataRecords.Sept(0, null, 2, 3, 4, 5, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, 2, 3, 4, 5, 6)));
  }

  @Test
  public void testEqualsT3() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, null, 3, 4, 5, 6);

    assertTrue(sept.equals(new DataRecords.Sept(0, 1, null, 3, 4, 5, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, 2, 3, 4, 5, 6)));
  }

  @Test
  public void testEqualsT4() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, null, 4, 5, 6);

    assertTrue(sept.equals(new DataRecords.Sept(0, 1, 2, null, 4, 5, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, 2, 3, 4, 5, 6)));
  }

  @Test
  public void testEqualsT5() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, 3, null, 5, 6);

    assertTrue(sept.equals(new DataRecords.Sept(0, 1, 2, 3, null, 5, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, 2, 3, 4, 5, 6)));
  }

  @Test
  public void testEqualsT6() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, 3, 4, null, 6);

    assertTrue(sept.equals(new DataRecords.Sept(0, 1, 2, 3, 4, null, 6)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, 2, 3, 4, 5, 6)));
  }

  @Test
  public void testEqualsT7() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, 3, 4, 5, null);

    assertTrue(sept.equals(new DataRecords.Sept(0, 1, 2, 3, 4, 5, null)));
    assertFalse(sept.equals(new DataRecords.Sept(0, 1, 2, 3, 4, 5, 6)));
  }

  @Test
  public void testHashCode() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, 3, 4, 5, 6);

    assertEquals(30569571, sept.hashCode());
  }

  @Test
  public void testHashCodeT1() {
    final DataRecords.Sept sept = new DataRecords.Sept(null, 1, 2, 3, 4, 5, 6);

    assertEquals(30569571, sept.hashCode());
  }

  @Test
  public void testHashCodeT2() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, null, 2, 3, 4, 5, 6);

    assertEquals(1940420, sept.hashCode());
  }

  @Test
  public void testHashCodeT3() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, null, 3, 4, 5, 6);

    assertEquals(28722529, sept.hashCode());
  }

  @Test
  public void testHashCodeT4() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, null, 4, 5, 6);

    assertEquals(30480198, sept.hashCode());
  }

  @Test
  public void testHashCodeT5() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, 3, null, 5, 6);

    assertEquals(30565727, sept.hashCode());
  }

  @Test
  public void testHashCodeT6() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, 3, 4, null, 6);

    assertEquals(30569416, sept.hashCode());
  }

  @Test
  public void testHashCodeT7() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, 3, 4, 5, null);

    assertEquals(30569565, sept.hashCode());
  }

  @Test
  public void testNext() {
    final DataRecords.Sept sept = DataRecords.Sept.next(0, 1, 2, 3, 4, 5, 6);

    assertNotNull(sept);
    assertEquals(0, sept.getT1());
    assertEquals(1, sept.getT2());
    assertEquals(2, sept.getT3());
    assertEquals(3, sept.getT4());
    assertEquals(4, sept.getT5());
    assertEquals(5, sept.getT6());
    assertEquals(6, sept.getT7());
  }

  @Test
  public void testToString() {
    final DataRecords.Sept sept = new DataRecords.Sept(0, 1, 2, 3, 4, 5, 6);

    assertEquals("Sept{t1=0, t2=1, t3=2, t4=3, t5=4, t6=5, t7=6}", sept.toString());
  }
}