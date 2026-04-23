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
package com.svenruppert.ddi.scopes.provided;

import com.svenruppert.ddi.scopes.InjectionScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JVMSingletonInjectionScope extends InjectionScope {


  private static final Map<String, Object> SINGLETONS = new ConcurrentHashMap<>();

  @Override
  public <T> T getInstance(final String clazz) {
    if (SINGLETONS.containsKey(clazz)) {
      return (T) SINGLETONS.get(clazz);
    }
    return null;
  }

  @Override
  public <T> void storeInstance(final Class<T> targetClassOrInterface, final T instance) {
    final String name = targetClassOrInterface.getName();
    if (SINGLETONS.containsKey(name)) {
      //logging that Singleton was tried to set twice
      throw new RuntimeException("tried to set the Singleton twice .. " + targetClassOrInterface + " with instance " + instance);
    } else {
      SINGLETONS.put(name, instance);
    }

  }

  @Override
  public void clear() {
    SINGLETONS.clear();
  }

  @Override
  public String getScopeName() {
    return JVMSingletonInjectionScope.class.getSimpleName();
  }
}
