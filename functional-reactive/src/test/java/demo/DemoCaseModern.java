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

import com.svenruppert.functional.result.Result;
import com.svenruppert.functional.result.matcher.Case;
import org.junit.jupiter.api.Test;

import static com.svenruppert.functional.result.matcher.Case.match;
import static com.svenruppert.functional.result.matcher.Case.matchCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Modern counterpart of {@link DemoCase}, exercising
 * {@link Case Case&lt;T, E&gt;} on top of the modern Result.
 */
public class DemoCaseModern {

  @Test
  void firstMatchingCaseWins() {
    String value = "A";
    Result<String, String> result =
        match(
            matchCase(() -> Result.<String, String>failure("nothing fit")),
            matchCase(() -> value.contains("A"),
                () -> Result.<String, String>success("Got A")),
            matchCase(() -> value.contains("B"),
                () -> Result.<String, String>success("Got B"))
        );
    assertTrue(result.isSuccess());
    assertEquals("Got A", result.getOrThrow());
  }

  @Test
  void defaultCaseRunsWhenNothingMatches() {
    String value = "C";
    Result<String, String> result =
        match(
            matchCase(() -> Result.<String, String>failure("nothing fit")),
            matchCase(() -> value.contains("A"),
                () -> Result.<String, String>success("Got A")),
            matchCase(() -> value.contains("B"),
                () -> Result.<String, String>success("Got B"))
        );
    assertTrue(result.isFailure());
    assertEquals("nothing fit", ((Result.Failure<String, String>) result).error());
  }

  @Test
  void typedErrorEnum() {
    enum Code { TOO_SHORT, TOO_LONG }
    String value = "hi";
    Result<String, Code> result =
        match(
            matchCase(() -> Result.<String, Code>success(value)),
            matchCase(() -> value.length() < 3,
                () -> Result.<String, Code>failure(Code.TOO_SHORT)),
            matchCase(() -> value.length() > 10,
                () -> Result.<String, Code>failure(Code.TOO_LONG))
        );
    assertTrue(result.isFailure());
    assertEquals(Code.TOO_SHORT, ((Result.Failure<String, Code>) result).error());
  }
}
