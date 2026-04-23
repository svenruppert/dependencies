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

import com.svenruppert.functional.model.DataRecords.Quad;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuadTest {

  @Test
  public void testEquals() {
    final Quad<Integer, Integer, Integer, Integer> quad = new Quad<>(0, 1, 2, 3);

    assertEquals(quad, quad);
    assertNotEquals(null, quad);
    assertNotEquals(quad, new Quad<>(null, 1, 2, 3));
    assertNotEquals(quad, new Quad<>(0, null, 2, 3));
    assertNotEquals(quad, new Quad<>(0, 1, null, 3));
  }

  @Test
  public void testEqualsT1() {
    final Quad quad = new Quad(null, 1, 2, 3);

    assertTrue(quad.equals(new Quad(null, 1, 2, 3)));
    assertFalse(quad.equals(new Quad(0, 1, 2, 3)));
  }

  @Test
  public void testEqualsT2() {
    final Quad quad = new Quad(0, null, 2, 3);

    assertTrue(quad.equals(new Quad(0, null, 2, 3)));
    assertFalse(quad.equals(new Quad(0, 1, 2, 3)));
  }

  @Test
  public void testEqualsT3() {
    final Quad quad = new Quad(0, 1, null, 3);

    assertTrue(quad.equals(new Quad(0, 1, null, 3)));
    assertFalse(quad.equals(new Quad(0, 1, 2, 3)));
  }

  @Test
  public void testEqualsT4() {
    final Quad quad = new Quad(0, 1, 2, null);

    assertTrue(quad.equals(new Quad(0, 1, 2, null)));
    assertFalse(quad.equals(new Quad(0, 1, 2, 3)));
  }

  @Test
  public void testHashCode() {
    final Quad quad = new Quad(0, 1, 2, 3);

    assertEquals(1026, quad.hashCode());
  }

  @Test
  public void testHashCodeT1() {
    final Quad quad = new Quad(null, 1, 2, 3);

    assertEquals(1026, quad.hashCode());
  }

  @Test
  public void testHashCodeT2() {
    final Quad quad = new Quad(0, null, 2, 3);

    assertEquals(65, quad.hashCode());
  }

  @Test
  public void testHashCodeT3() {
    final Quad quad = new Quad(0, 1, null, 3);

    assertEquals(964, quad.hashCode());
  }

  @Test
  public void testHashCodeT4() {
    final Quad quad = new Quad(0, 1, 2, null);

    assertEquals(1023, quad.hashCode());
  }


  @Test
  public void testNext() {
    final Quad quad = Quad.next(0, 1, 2, 3);

    assertNotNull(quad);
    assertEquals(0, quad.getT1());
    assertEquals(1, quad.getT2());
    assertEquals(2, quad.getT3());
    assertEquals(3, quad.getT4());
  }

  @Test
  public void testToString() {
    final Quad quad = new Quad(0, 1, 2, 3);

    assertEquals("Quad{t1=0, t2=1, t3=2, t4=3}", quad.toString());
  }
}