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