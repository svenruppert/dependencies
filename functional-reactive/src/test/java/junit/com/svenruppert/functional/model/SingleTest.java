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
 * #L%
 */

import com.svenruppert.functional.model.DataRecords;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SingleTest {

  @Test
  public void testEquals() {
    final DataRecords.Single single = new DataRecords.Single(0);

    assertFalse(single.equals(null));
    assertTrue(single.equals(single));
    assertTrue(single.equals(new DataRecords.Single(0)));
  }

  @Test
  public void testHashCode() {
    final DataRecords.Single single = new DataRecords.Single(0);

    assertEquals(31, single.hashCode());
  }

  @Test
  public void testNext() {
    final DataRecords.Single single = DataRecords.Single.next(0);

    assertNotNull(single);
    assertEquals(0, single.getT1());
  }

  @Test
  public void testToString() {
    final DataRecords.Single single = new DataRecords.Single(0);

    assertEquals("Single{t1=0}", single.toString());
  }
}
