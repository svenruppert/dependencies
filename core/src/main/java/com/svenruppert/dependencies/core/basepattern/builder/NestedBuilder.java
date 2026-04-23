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
package com.svenruppert.dependencies.core.basepattern.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>Abstract NestedBuilder class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public abstract class NestedBuilder<T, V> {
  protected T parent;

  /**
   * To get the parent builder
   *
   * @return T the instance of the parent builder
   */
  public T done() {
    Class<?> parentClass = parent.getClass();
    try {
      V build = this.build();
      String methodname = "with" + build.getClass().getSimpleName();
      Method method = parentClass.getDeclaredMethod(methodname, build.getClass());
      final boolean accessible = method.canAccess(parent);
      method.setAccessible(true);
      method.invoke(parent, build);
      method.setAccessible(accessible);
    } catch (NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException e) {
      e.printStackTrace();
    }
    return parent;
  }

  /**
   * <p>build.</p>
   *
   * @return a V object.
   */
  public abstract V build();

  /**
   * <p>withParentBuilder.</p>
   *
   * @param parent a T object.
   * @param <P> a P object.
   * @return a P object.
   */
  public <P extends NestedBuilder<T, V>> P withParentBuilder(T parent) {
    this.parent = parent;
    return (P) this;
  }
}
