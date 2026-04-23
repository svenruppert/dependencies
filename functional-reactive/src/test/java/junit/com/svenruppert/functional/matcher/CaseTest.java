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
package junit.com.svenruppert.functional.matcher;

import com.svenruppert.functional.model.Result;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static com.svenruppert.functional.matcher.Case.match;
import static com.svenruppert.functional.matcher.Case.matchCase;
import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 */
public class CaseTest {


  @Test
  void test000() {
    String value = "OK";

    match(
        matchCase(() -> Result.success(value))
    )
        .ifPresentOrElse(
            s -> assertEquals(value , s) ,
            s -> fail("not good")
        );
  }

  @Test
  void test001() {
    final String error_message = "error message";
    String value = null;

    match(
        matchCase(() -> Result.success(value)) ,
        matchCase(() -> isNull(value) , () -> Result.failure(error_message))
    )
        .ifPresentOrElse(
            s -> fail("not good") ,
            s -> assertEquals(error_message , s)
        );
  }

  @Test
  void test002() {
    final String error_message = "error message";
    String value = "OK";

    match(
        matchCase(() -> Result.success(value)) ,
        matchCase(() -> isNull(value) , () -> Result.failure(error_message))
    )
        .ifPresentOrElse(
            s -> assertEquals(value , s) ,
            s -> fail("not good")
        );
  }

  @Test
  void test003() {
    final String error_message = "error message";


    IntStream
        .range(0 , 10)
        .boxed()
        .forEach(value -> match(
            matchCase(() -> Result.success(value)) ,
            matchCase(() -> value == null , () -> Result.failure(error_message)) ,
            matchCase(() -> value == 1 , () -> Result.success(100)) ,
            matchCase(() -> value == 2 , () -> Result.success(200))
        )
            .ifPresentOrElse(
                s -> {
                  if (value == 1) assertEquals(Long.valueOf(100) , Long.valueOf(s));
                  else if (value == 2) assertEquals(Long.valueOf(200) , Long.valueOf(s));
                  else assertEquals(Long.valueOf(value) , Long.valueOf(s));
                } ,
                s -> fail("not good")
            ));


  }


}