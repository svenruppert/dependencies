package org.reflections.scanners;

import javax.annotation.Nullable;

import org.reflections.Configuration;
import org.reflections.vfs.Vfs;
import repacked.com.google.common.base.Predicate;
import repacked.com.google.common.collect.Multimap;

/**
 *
 */
public interface Scanner {

  void setConfiguration(Configuration configuration);

  Multimap<String, String> getStore();

  void setStore(Multimap<String, String> store);

  Scanner filterResultsBy(Predicate<String> filter);

  boolean acceptsInput(String file);

  Object scan(Vfs.File file , @Nullable Object classObject);

  boolean acceptResult(String fqn);
}
