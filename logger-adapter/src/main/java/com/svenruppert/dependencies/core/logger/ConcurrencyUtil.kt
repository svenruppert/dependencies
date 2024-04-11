/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
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
package com.svenruppert.dependencies.core.logger

import java.util.concurrent.ConcurrentMap

/**
 *
 * ConcurrencyUtil class.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
object ConcurrencyUtil {

  /**
   *
   * getOrPutIfAbsent.
   *
   * @param map a [ConcurrentMap] object.
   * @param key a K object.
   * @param func a [org.rapidpm.dependencies.core.logger.ConstructorFunction] object.
   * @param <K> a K object.
   * @param <V> a V object.
   * @return a V object.
  </V></K> */
  fun <K, V> getOrPutIfAbsent(map: ConcurrentMap<K, V>,
                              key: K,
                              func: ConstructorFunction<K, V>): V {
    return if (map[key] == null) {
      val value = func.createNew(key)
      val current = (map as MutableMap<K, V>).putIfAbsent(key, value)
      return current ?: value
    } else map[key]!!
  }
}
