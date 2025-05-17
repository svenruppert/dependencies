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

public class PairTest {

  @Test
  public void testEquals() {
    final DataRecords.Pair pair = new DataRecords.Pair(0, 1);

    assertFalse(pair.equals(null));
    assertFalse(pair.equals(new DataRecords.Pair(null, 1)));
    assertFalse(pair.equals(new DataRecords.Pair(0, null)));
    assertTrue(pair.equals(pair));
    assertTrue(pair.equals(new DataRecords.Pair(0, 1)));
  }

  @Test
  public void testHashCode() {
    final DataRecords.Pair pair = new DataRecords.Pair(0, 1);

    assertEquals(962, pair.hashCode());
  }

  @Test
  public void testNext() {
    final DataRecords.Pair pair = DataRecords.Pair.next(0, 1);

    assertNotNull(pair);
    assertEquals(0, pair.getT1());
    assertEquals(1, pair.getT2());
  }

  @Test
  public void testToString() {
    final DataRecords.Pair pair = new DataRecords.Pair(0, 1);

    assertEquals("Pair{t1=0, t2=1}", pair.toString());
  }
}