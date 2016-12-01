package org.rapidpm.dependencies.core.reflections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by RapidPM - Team on 08.09.16.
 */
public class NewInstances {

  private NewInstances() { }

  private static final Logger LOGGER = LoggerFactory.getLogger(NewInstances.class);

  public static <T> List<T> createInstances(final Set<Class<? extends T>> classes) {

    if (classes == null) return Collections.emptyList();

    return classes
        .stream()
        .map(c -> {
          try {
//            return Optional.of(c.newInstance());
            return Optional.of(c.getDeclaredConstructor().newInstance());
          } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("failed to create new instance ", e);
          } catch (NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
          }
          return Optional.<T>empty();
        })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.<T>toList());
  }


  public static <T> Optional<T> createInstance(final Class<? extends T> clazz) {
    if (clazz == null) return Optional.empty();

    try {
      return Optional.of(clazz.getDeclaredConstructor().newInstance());
    } catch (InstantiationException | IllegalAccessException e) {
      LOGGER.error("failed to create new instance ", e);
    } catch (NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }


}
