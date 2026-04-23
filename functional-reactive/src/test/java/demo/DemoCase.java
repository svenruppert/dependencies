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

import com.svenruppert.functional.matcher.Case;
import com.svenruppert.functional.model.Result;
import org.junit.jupiter.api.Test;

import static com.svenruppert.functional.matcher.Case.matchCase;

public class DemoCase {


  @Test
  void test001() {

    String value = "A";
    Result<String> result
        = Case.match(
            matchCase(() -> Result.failure("nothing fit")),
            matchCase(() -> value.contains("A"),
                      () -> Result.success("Got A")),
            matchCase(() -> value.contains("B"),
                      () -> Result.success("Got B"))
                    );


  }
}