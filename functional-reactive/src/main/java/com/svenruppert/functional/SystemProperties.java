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
package com.svenruppert.functional;

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

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.svenruppert.functional.Converting.convertToBooleanResult;
import static com.svenruppert.functional.Converting.convertToDoubleResult;
import static com.svenruppert.functional.Converting.convertToIntegerResult;
import static java.lang.System.getProperty;

/**
 * Read JVM system properties through a uniform API. Property keys are namespaced via
 * the qualifier class' fully-qualified name ({@code qualifier.getName() + "." + key}).
 *
 * <p>All accessor methods return a modern {@link Result Result&lt;T, String&gt;};
 * missing keys produce a failure whose message embeds the qualified key.
 */
public interface SystemProperties {

  // -------------------------------------------------------------------------
  // Key qualification + presence checks
  // -------------------------------------------------------------------------

  static BiFunction<Class<?>, String, String> qualifiedParameter() {
    return (clazz, unqualifiedName) -> clazz.getName() + "." + unqualifiedName;
  }

  static BiFunction<Class<?>, String, Boolean> hasSystemProperty() {
    return qualifiedParameter().andThen(key -> getProperty(key) != null);
  }

  static Function<String, Boolean> hasSystemProperty(Class<?> qualifier) {
    return key -> hasSystemProperty().apply(qualifier, key);
  }

  // -------------------------------------------------------------------------
  // Result-returning accessors
  // -------------------------------------------------------------------------

  static BiFunction<Class<?>, String, Result<String, String>> systemPropertyResult() {
    return (clazz, key) -> {
      String qualified = clazz.getName() + "." + key;
      return Result.ofNullable(getProperty(qualified),
          "system property '" + qualified + "' was null");
    };
  }

  static BiFunction<Class<?>, String, Result<String, String>> systemPropertyResult(String defaultValue) {
    return (clazz, key) -> {
      String qualified = clazz.getName() + "." + key;
      return Result.ofNullable(getProperty(qualified, defaultValue),
          "system property '" + qualified + "' was null");
    };
  }

  static Function<String, Result<String, String>> systemPropertyResult(Class<?> qualifier) {
    BiFunction<Class<?>, String, Result<String, String>> bi = systemPropertyResult();
    return key -> bi.apply(qualifier, key);
  }

  static Function<String, Result<String, String>> systemPropertyResult(Class<?> qualifier, String defaultValue) {
    BiFunction<Class<?>, String, Result<String, String>> bi = systemPropertyResult(defaultValue);
    return key -> bi.apply(qualifier, key);
  }

  static Function<String, Result<Boolean, String>> systemPropertyBooleanResult(Class<?> qualifier) {
    return key -> systemPropertyResult(qualifier).apply(key)
        .flatMap(convertToBooleanResult());
  }

  static Function<String, Result<Boolean, String>> systemPropertyBooleanResult(Class<?> qualifier, String defaultValue) {
    return key -> systemPropertyResult(qualifier, defaultValue).apply(key)
        .flatMap(convertToBooleanResult());
  }

  static Function<String, Result<Integer, String>> systemPropertyIntResult(Class<?> qualifier) {
    return key -> systemPropertyResult(qualifier).apply(key)
        .flatMap(convertToIntegerResult());
  }

  static Function<String, Result<Integer, String>> systemPropertyIntResult(Class<?> qualifier, String defaultValue) {
    return key -> systemPropertyResult(qualifier, defaultValue).apply(key)
        .flatMap(convertToIntegerResult());
  }

  static Function<String, Result<Double, String>> systemPropertyDoubleResult(Class<?> qualifier) {
    return key -> systemPropertyResult(qualifier).apply(key)
        .flatMap(convertToDoubleResult());
  }

  static Function<String, Result<Double, String>> systemPropertyDoubleResult(Class<?> qualifier, String defaultValue) {
    return key -> systemPropertyResult(qualifier, defaultValue).apply(key)
        .flatMap(convertToDoubleResult());
  }
}
