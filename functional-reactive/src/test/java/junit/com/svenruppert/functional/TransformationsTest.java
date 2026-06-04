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

import com.svenruppert.functional.Transformations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class TransformationsTest {


  @Test
  public void test001() {

    String helloWorld = Transformations
        .<String, String, String>curryBiFunction()
        .apply((s1, s2) -> s1 + " " + s2)
        .apply("Hello").apply("World");
    assertEquals("Hello World", helloWorld);
  }


  @Test
  public void test002() {
    String helloWorld = Transformations
        .<String, String, String>unCurryBiFunction()
        .apply(inputA -> inputB -> inputA + " " + inputB)
        .apply("Hello", "World");
    assertEquals("Hello World", helloWorld);
  }

  @Test
  public void test003() {

    String helloWorld = Transformations
        .<String, String, String, String>curryTriFunction()
        .apply((s1, s2, s3) -> s1 + " " + s2 + " " + s3)
        .apply("Hello").apply("World").apply("!");
    assertEquals("Hello World !", helloWorld);
  }


  @Test
  public void test004() {
    String helloWorld = Transformations
        .<String, String, String, String>unCurryTriFunction()
        .apply(inputA -> inputB -> inputC -> inputA + " " + inputB + " " + inputC)
        .apply("Hello", "World", "!");
    assertEquals("Hello World !", helloWorld);
  }

  @Test
  public void test005() {
    String helloWorld = Transformations
        .<String, String, String, String>unCurryCheckedTriFunction()
        .apply(inputA -> inputB -> inputC -> inputA + " " + inputB + " " + inputC)
        .apply("Hello", "World", "!")
        .getOrThrow();
    assertEquals("Hello World !", helloWorld);
  }

  @Test
  public void test006() {
    Transformations
        .<String, String, String, String>unCurryCheckedTriFunction()
        .apply(inputA -> inputB -> inputC -> {
          throw new RuntimeException("");
        })
        .apply("Hello", "World", "!")
        .peek(e -> Assertions.fail("should be false"));
  }


  @Test
  public void test007() {

    String helloWorld = Transformations
        .<String, String, String, String>curryCheckedTriFunction()
        .apply((s1, s2, s3) -> s1 + " " + s2 + " " + s3)
        .apply("Hello").apply("World").apply("!")
        .getOrThrow();
    assertEquals("Hello World !", helloWorld);
  }

  @Test
  public void test008() {
    Transformations
        .<String, String, String, String>curryCheckedTriFunction()
        .apply((s1, s2, s3) -> { throw new RuntimeException(""); })
        .apply("Hello").apply("World").apply("!")
        .peek(e -> Assertions.fail("should be false"));
  }

  @Test
  public void test009() {
    String helloWorld = Transformations
        .<String, String, String>curryCheckedBiFunction()
        .apply((s1, s2) -> s1 + " " + s2)
        .apply("Hello").apply("World")
        .getOrThrow();
    assertEquals("Hello World", helloWorld);
  }

  @Test
  public void test010() {
    Transformations
        .<String, String, String>curryCheckedBiFunction()
        .apply((s1, s2) -> { throw new RuntimeException(""); })
        .apply("Hello").apply("World")
        .peek(e -> Assertions.fail("should be false"));
  }

  @Test
  public void test011() {
    String helloWorld = Transformations
        .<String, String, String>unCurryCheckedBiFunction()
        .apply(inputA -> inputB -> inputA + " " + inputB)
        .apply("Hello", "World")
        .getOrThrow();
    assertEquals("Hello World", helloWorld);
  }

  @Test
  public void test012() {
    Transformations
        .<String, String, String>unCurryCheckedBiFunction()
        .apply(inputA -> inputB -> { throw new RuntimeException(""); })
        .apply("Hello", "World")
        .peek(e -> Assertions.fail("should be false"));
  }

}