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
package com.svenruppert.dependencies.core.stream;

/*-
 * #%L
 * SRU - Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2026 Sven Ruppert
 * %%
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
 * #L%
 */



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * <p>ImmutableListCollector class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class ImmutableListCollector {
  private ImmutableListCollector() {
  }

  /**
   * <p>toImmutableList.</p>
   *
   * @param collectionFactory a {@link java.util.function.Supplier} object.
   * @param <T>               a T object.
   * @param <A>               a A object.
   * @return a {@link java.util.stream.Collector} object.
   */
  public static <T, A extends List<T>> Collector<T, A, List<T>> toImmutableList(Supplier<A> collectionFactory) {
    return Collector.of(collectionFactory, List::add, (left, right) -> {
      left.addAll(right);
      return left;
    }, Collections::unmodifiableList);
  }

  /**
   * <p>toImmutableList.</p>
   *
   * @param <T> a T object.
   * @return a {@link java.util.stream.Collector} object.
   */
  public static <T> Collector<T, List<T>, List<T>> toImmutableList() {
    return toImmutableList(ArrayList::new);
  }
}
