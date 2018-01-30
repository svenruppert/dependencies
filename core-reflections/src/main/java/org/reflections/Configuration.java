package org.reflections;

import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;

import org.reflections.adapters.MetadataAdapter;
import org.reflections.scanners.Scanner;
import org.reflections.serializers.Serializer;
import repacked.com.google.common.base.Predicate;


public interface Configuration {

  Set<Scanner> getScanners();


  Set<URL> getUrls();


  @SuppressWarnings({"RawUseOfParameterizedType"})
  MetadataAdapter getMetadataAdapter();


  @Nullable
  Predicate<String> getInputsFilter();


  ExecutorService getExecutorService();


  Serializer getSerializer();


  @Nullable
  ClassLoader[] getClassLoaders();


  boolean shouldExpandSuperTypes();
}
