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

import com.svenruppert.functional.model.Result;

import java.util.function.Function;

public interface Converting {


  static <T>
  Function<T, Result<String>> convertToString(Function<T, String> func) {
    return Transformations.asCheckedFunc(func);
  }

  static <T>
  Function<T, Result<String>> convertToString() {
    return convertToString(String::valueOf);
  }

  static <T>
  Function<T, Result<Boolean>> convertToBoolean(Function<T, Boolean> func) {
    return Transformations.asCheckedFunc(func);
  }

  static <T>
  Function<T, Result<Boolean>> convertToBoolean() {
    return Converting
        .<T>convertToString()
        .andThen(s -> s.map(Boolean::parseBoolean));
  }

  static <T>
  Function<T, Result<Integer>> convertToInteger(Function<T, Integer> func) {
    return Transformations.asCheckedFunc(func);
  }

  static <T>
  Function<T, Result<Integer>> convertToInteger() {
    return Converting
        .<T>convertToString()
        .andThen(s -> s.map(Integer::parseInt));
  }

  static <T>
  Function<T, Result<Double>> convertToDouble(Function<T, Double> func) {
    return Transformations.asCheckedFunc(func);
  }

  static <T>
  Function<T, Result<Double>> convertToDouble() {
    return Converting
        .<T>convertToString()
        .andThen(s -> s.map(Double::parseDouble));
  }


}