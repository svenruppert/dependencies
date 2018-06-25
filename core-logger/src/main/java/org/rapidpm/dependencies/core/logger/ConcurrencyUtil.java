/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rapidpm.dependencies.core.logger;

import java.util.concurrent.ConcurrentMap;

/**
 *
 */
public class ConcurrencyUtil {
  private ConcurrencyUtil() {
  }

  public static <K, V> V getOrPutIfAbsent(ConcurrentMap<K, V> map , K key , ConstructorFunction<K, V> func) {
    V value = map.get(key);
    if (value == null) {
      value = func.createNew(key);
      V current = map.putIfAbsent(key , value);
      value = current == null ? value : current;
    }
    return value;
  }
}