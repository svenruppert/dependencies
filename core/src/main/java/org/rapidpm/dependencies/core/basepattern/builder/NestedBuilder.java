/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.rapidpm.dependencies.core.basepattern.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
      final boolean accessible = method.isAccessible();
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

  public abstract V build();

  /**
   * @param parent
   *
   * @return
   */
  public <P extends NestedBuilder<T, V>> P withParentBuilder(T parent) {
    this.parent = parent;
    return (P) this;
  }
}
