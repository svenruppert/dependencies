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



import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the EUPL, Version 1.2 (the "Licence");
 * you may not use this file except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * Created by RapidPM - Team on 29.12.16.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class ImmutableSetCollector {
  private ImmutableSetCollector() {
  }

  /**
   * <p>toImmutableSet.</p>
   *
   * @param collectionFactory a {@link java.util.function.Supplier} object.
   * @param <T>               a T object.
   * @param <A>               a A object.
   * @return a {@link java.util.stream.Collector} object.
   */
  public static <T, A extends Set<T>> Collector<T, A, Set<T>> toImmutableSet(Supplier<A> collectionFactory) {
    return Collector.of(collectionFactory, Set::add, (left, right) -> {
      left.addAll(right);
      return left;
    }, Collections::unmodifiableSet);
  }

  /**
   * <p>toImmutableSet.</p>
   *
   * @param <T> a T object.
   * @return a {@link java.util.stream.Collector} object.
   */
  public static <T> Collector<T, Set<T>, Set<T>> toImmutableSet() {
    return toImmutableSet(HashSet::new);
  }
}
