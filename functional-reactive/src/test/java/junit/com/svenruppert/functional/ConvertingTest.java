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
package junit.com.svenruppert.functional;

import com.svenruppert.functional.Converting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConvertingTest {

  @Test
  @DisplayName("convertToString")
  void test001() {
    final String result = Converting
        .<Integer>convertToString()
        .apply(1)
        .get();
    Assertions.assertEquals("1" , result);
  }

  @Test
  @DisplayName("convertToString(f)")
  void test002() {

    final String result = Converting
        .<Integer>convertToString((i) -> "x" + 1)
        .apply(1)
        .get();
    Assertions.assertEquals("x1" , result);
  }

  @Test
  @DisplayName("convertToString(f) - failed")
  void test003() {
    Converting
        .<Integer>convertToString((i) -> {
          throw new RuntimeException("failed");
        })
        .apply(1)
    .ifPresentOrElse((s) -> Assertions.fail(),
                     (failed) -> Assertions.assertEquals("RuntimeException - failed", failed));
  }



}