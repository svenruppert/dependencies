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