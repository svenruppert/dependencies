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
package demo;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

public class DemoMigration {


  @Test
  void test001() {


    Function<Integer, Integer> plus2 = (i)-> i + 2;
    Function<Integer, Integer> plus5 = (i)-> i + 5;
    Function<Integer, Integer> plus10 = (i)-> i + 10;

    Function<Integer, Function<Integer, Integer>>
        adder = (con) -> (i) -> i+con;

    Function<Integer, Integer> plusTwo
        = adder.apply(2);



  }
}
