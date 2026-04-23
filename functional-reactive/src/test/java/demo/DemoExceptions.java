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

import com.svenruppert.functional.functions.CheckedFunction;
import com.svenruppert.functional.model.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Stream;

public class DemoExceptions {

  @Test
  void demo01() {
    try {
      int ups = Integer.parseInt("ups");
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }

    Stream.of("1", "2", "3", "4", "ups")
        .map(Integer::parseInt)
        .count();

    Stream.of("1", "2", "3", "4", "ups")
        .map(s -> {
          try {
            return Integer.parseInt(s);
          } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1; //oh no
          }
        })
        .count();

    Stream.of("1", "2", "3", "4", "ups")
        .map(s -> {
          try {
            return Optional.of(Integer.parseInt(s));
          } catch (NumberFormatException e) {
            e.printStackTrace();
            return Optional.<Integer>empty(); //oh no
          }
        })
        .filter(Optional::isPresent)
        //          .flatMap(Optional::stream)
        .count();


    Stream.of("1", "2", "3", "4", "ups")
        .map(new CheckedFunction<String, Integer>() {
          @Override
          public Integer applyWithException(String s)
              throws Exception {
            return Integer.parseInt(s);
          }
        })
        .count();

    Stream.of("1", "2", "3", "4", "ups")
        .map((CheckedFunction<String, Integer>) Integer::parseInt)
        .flatMap(Result::stream)
        .count();


  }
}