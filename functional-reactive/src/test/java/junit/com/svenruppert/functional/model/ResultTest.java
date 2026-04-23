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

import com.svenruppert.functional.model.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class ResultTest {

  @Test
  public void test001() throws Exception {
    Result<String> hello = Result.success("Hello");
    Result<String> world = hello.thenCombine("World" , (s , s2) -> Result.success(s + " - " + s2));
    assertEquals("Hello - World" , world.get());
  }


  @Test
  public void test002() throws Exception {
    Result<String> hello = Result.success("Hello");
    CompletableFuture<Result<String>> world = hello
        .thenCombineAsync("World" , (s , s2) -> Result.success(s + " - " + s2));

    Result<String> result = world.join();
    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals("Hello - World" , result.get());
  }


  @Test
  public void test003() throws Exception {
    final Result<Integer> asFailure = Result.success("Hello").asFailure();
    assertTrue(asFailure.isAbsent());
    asFailure.ifPresent(v -> Assertions.fail("not good"));
  }

  @Test
  public void test004() throws Exception {
    final Result<Integer> asFailure = Result.failure("Hello").asFailure();
    assertTrue(asFailure.isAbsent());
    asFailure.ifPresent(v -> Assertions.fail("not good"));
  }
}