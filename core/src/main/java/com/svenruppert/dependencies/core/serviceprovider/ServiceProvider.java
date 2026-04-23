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
package com.svenruppert.dependencies.core.serviceprovider;

/*-
 * #%L
 * SRU - Core
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

import com.svenruppert.dependencies.core.logger.HasLogger;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

public interface ServiceProvider<T>
    extends HasLogger {

  //  default boolean failWithException() {
  //    return false;
  //  }

  default Function<Class<T>, Optional<T>> loadServiceFkt() {
    return (service) -> {
      Iterator<T> iterator = ServiceLoader.load(service)
          .iterator();
      Iterable<T> iterable = () -> iterator;
      final Set<T> set = stream(iterable.spliterator(), false).collect(toSet());
      if (set.isEmpty()) {
        final String msg = "no implementation found for interface " + service.getName();
        logger(service).warn(msg);
        //        if (failWithException()) throw new RuntimeException("no implementation found for interface " + service.getName());
        logger().warn("no implementation found for interface " + service.getName());
        return Optional.empty();
      }

      if (set.size() > 1) {
        final String msg = "to many implementations found for interface " + service.getName();
        logger(service).warn(msg);
        //        if (failWithException()) throw new RuntimeException("to many implementations found for interface " + service.getName());
        logger().warn("to many implementations found for interface " + service.getName());
        return Optional.empty();
      }
      return Optional.of(set.iterator()
                             .next());
    };
  }

  Class<T> serviceInterface();

  //  default Result<? extends T> load() {
  //    return loadServiceFkt().apply(serviceInterface());
  //  }

  default Optional<T> load() {
    return loadServiceFkt().apply(serviceInterface());
  }
}