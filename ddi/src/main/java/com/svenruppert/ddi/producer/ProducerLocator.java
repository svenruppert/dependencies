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
package com.svenruppert.ddi.producer;

import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.Produces;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ProducerLocator {

  //class 2 producer-set
  private static final Map<Class, Set<Class<?>>> RESOLVER_CACHE_FOR_CLASS_2_PRODUCER_SET = new ConcurrentHashMap<>();

  private ProducerLocator() {
  }

  public static void clearCache() {
    RESOLVER_CACHE_FOR_CLASS_2_PRODUCER_SET.clear();
  }

  public static Set<Class<?>> findProducersFor(final Class clazzOrInterf) {
    if (RESOLVER_CACHE_FOR_CLASS_2_PRODUCER_SET.containsKey(clazzOrInterf)) return RESOLVER_CACHE_FOR_CLASS_2_PRODUCER_SET.get(clazzOrInterf);

    final Set<Class<?>> typesAnnotatedWith = DI.getTypesAnnotatedWith(Produces.class)
        .stream()
        .filter(producerClass -> {
          final Produces annotation = producerClass.getAnnotation(Produces.class);
          final Class value = annotation.value();
          return value.equals(clazzOrInterf);
        })
        .collect(Collectors.toSet());

    final Set<Class<?>> unmodifiableSet = Collections.unmodifiableSet(typesAnnotatedWith);
    RESOLVER_CACHE_FOR_CLASS_2_PRODUCER_SET.put(clazzOrInterf, unmodifiableSet);
    return unmodifiableSet;
  }
}
