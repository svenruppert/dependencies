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

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * <p>StreamFunctions interface.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public interface StreamFunctions {

  /**
   * <p>streamFilter.</p>
   *
   * @param <T> a T object.
   * @return a {@link Function} object.
   */
  static <T> Function<Predicate<T>, Function<Stream<T>, Stream<T>>> streamFilter() {
    return (filter) -> (Function<Stream<T>, Stream<T>>) inputStream -> inputStream.filter(filter);
  }
}