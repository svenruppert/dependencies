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
package com.svenruppert.ddi.reflections;

import com.svenruppert.dependencies.core.stream.ImmutableSetCollector;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Set;

public class DDIReflectionUtils
    extends org.reflections8.ReflectionUtils {

  public boolean checkInterface(final Type aClass , Class targetInterface) {
    if (aClass.equals(targetInterface)) return true;

    final Type[] genericInterfaces = ((Class) aClass).getGenericInterfaces();
    for (final Type genericInterface : genericInterfaces) {
      if (genericInterface.equals(targetInterface)) return true;
      final Type[] nextLevBackArray = ((Class) genericInterface).getGenericInterfaces();
      for (final Type type : nextLevBackArray) {
        if (checkInterface(type , targetInterface)) return true;
      }
    }
    final Type genericSuperclass = ((Class) aClass).getGenericSuperclass();
    if (genericSuperclass != null) {
      return checkInterface(genericSuperclass, targetInterface);
    }
    return false;
  }

  public <T> Set<Class<? extends T>> removeInterfacesAndGeneratedFromSubTypes(final Set<Class<? extends T>> subTypesOf) {
    return subTypesOf
        .stream()
        .filter((c) -> ! (c.isInterface()))
        .filter((c) -> ! (Modifier.isAbstract(c.getModifiers())))
        .collect(ImmutableSetCollector.toImmutableSet());
  }

}