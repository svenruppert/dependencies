/*
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.com.svenruppert.functional.model;

import com.svenruppert.functional.model.DataRecords;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuintTest {

  @Test
  public void testEquals() {
    final DataRecords.Quint quint = new DataRecords.Quint(0, 1, 2, 3, 4);

//    assertTrue(quint.equals(quint));
    assertEquals(quint, quint);
    assertFalse(quint.equals(null));
    assertFalse(quint.equals(new DataRecords.Quint(null, 1, 2, 3, 4)));
    assertFalse(quint.equals(new DataRecords.Quint(0, null, 2, 3, 4)));
    assertFalse(quint.equals(new DataRecords.Quint(0, 1, null, 3, 4)));
    assertFalse(quint.equals(new DataRecords.Quint(0, 1, 2, null, 4)));
  }

  @Test
  public void testEqualsT1() {
    final DataRecords.Quint quint = new DataRecords.Quint(null, 1, 2, 3, 4);

    assertTrue(quint.equals(new DataRecords.Quint(null, 1, 2, 3, 4)));
    assertFalse(quint.equals(new DataRecords.Quint(0, 1, 2, 3, 4)));
  }

  @Test
  public void testEqualsT2() {
    final DataRecords.Quint quint = new DataRecords.Quint(0, null, 2, 3, 4);

    assertTrue(quint.equals(new DataRecords.Quint(0, null, 2, 3, 4)));
    assertFalse(quint.equals(new DataRecords.Quint(0, 1, 2, 3, 4)));
  }

  @Test
  public void testEqualsT3() {
    final DataRecords.Quint quint = new DataRecords.Quint(0, 1, null, 3, 4);

    assertTrue(quint.equals(new DataRecords.Quint(0, 1, null, 3, 4)));
    assertFalse(quint.equals(new DataRecords.Quint(0, 1, 2, 3, 4)));
  }

  @Test
  public void testEqualsT4() {
    final DataRecords.Quint quint = new DataRecords.Quint(0, 1, 2, null, 4);

    assertTrue(quint.equals(new DataRecords.Quint(0, 1, 2, null, 4)));
    assertFalse(quint.equals(new DataRecords.Quint(0, 1, 2, 3, 4)));
  }

  @Test
  public void testEqualsT5() {
    final DataRecords.Quint quint = new DataRecords.Quint(0, 1, 2, 3, null);

    assertTrue(quint.equals(new DataRecords.Quint(0, 1, 2, 3, null)));
    assertFalse(quint.equals(new DataRecords.Quint(0, 1, 2, 3, 4)));
  }

  @Test
  public void testHashCode() {
    final DataRecords.Quint quint = new DataRecords.Quint(0, 1, 2, 3, 4);

    assertEquals(31810, quint.hashCode());
  }

  @Test
  public void testHashCodeT1() {
    final DataRecords.Quint quint = new DataRecords.Quint(null, 1, 2, 3, 4);

    assertEquals(31810, quint.hashCode());
  }

  @Test
  public void testHashCodeT2() {
    final DataRecords.Quint quint = new DataRecords.Quint(0, null, 2, 3, 4);

    assertEquals(2019, quint.hashCode());
  }

  @Test
  public void testHashCodeT3() {
    final DataRecords.Quint quint = new DataRecords.Quint(0, 1, null, 3, 4);

    assertEquals(29888, quint.hashCode());
  }

  @Test
  public void testHashCodeT4() {
    final DataRecords.Quint quint = new DataRecords.Quint(0, 1, 2, null, 4);

    assertEquals(31717, quint.hashCode());
  }

  @Test
  public void testHashCodeT5() throws Exception {
    final DataRecords.Quint quint = new DataRecords.Quint(0, 1, 2, 3, null);

    assertEquals(31806, quint.hashCode());
  }

  @Test
  public void testNext() {
    final DataRecords.Quint quint = DataRecords.Quint.next(0, 1, 2, 3, 4);

    assertNotNull(quint);
    assertEquals(0, quint.getT1());
    assertEquals(1, quint.getT2());
    assertEquals(2, quint.getT3());
    assertEquals(3, quint.getT4());
    assertEquals(4, quint.getT5());
  }

  @Test
  public void testToString() {
    final DataRecords.Quint quint = new DataRecords.Quint(0, 1, 2, 3, 4);

    assertEquals("Quint{t1=0, t2=1, t3=2, t4=3, t5=4}", quint.toString());
  }
}