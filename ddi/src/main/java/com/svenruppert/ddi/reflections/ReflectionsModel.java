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

/*-
 * #%L
 * SRU - DDI
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2026 Sven Ruppert
 * %%
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * #L%
 */



import com.svenruppert.functional.model.DataRecords;
import org.reflections8.Reflections;
import org.reflections8.scanners.MethodAnnotationsScanner;
import org.reflections8.scanners.Scanner;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ClasspathHelper;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.unmodifiableSet;


public class ReflectionsModel {


  //TODO refactoring to pessimistic write / concurrent read

  private final Map<String, LocalDateTime> activatedPackagesMap = new ConcurrentHashMap<>();
  private final Object obj = new Object();
  private final Map<DataRecords.Pair<String, String>, Set<Method>> methodsAnnotatedWithCache = new ConcurrentHashMap<>();
  private final Map<String, Set> subTypeOfCache = new ConcurrentHashMap<>();
  private final Map<String, Set> subTypeOfCacheWithoutInterfacesnadGenerated = new ConcurrentHashMap<>();
  private final Map<Class<? extends Annotation>, Set> typesAnnotatedWithCache = new ConcurrentHashMap<>();
  private final ThreadLocal<Boolean> parallelExecutors = ThreadLocal.withInitial(() -> false);
  private final Reflections reflections = new Reflections(
      createConfigurationBuilder()
          .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("com.svenruppert")))
          .setScanners(createScanners())
  );
  private final Function<Set, Set> newSet = (Function<Set, Set>) input -> {
    final HashSet<Set<Class<?>>> hashSet = new HashSet<>(input);
    return hashSet;
  };

  public ReflectionsModel() {
  }

  public void setParallelExecutors(final boolean parallelExecutors) {
    this.parallelExecutors.set(parallelExecutors);
  }

  public void rescan(final String pkgPrefix) {
    var configurationBuilder = createConfigurationBuilder();
    var include = new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix));
    var scanners = createScanners();
    rescannImpl(configurationBuilder
                    .filterInputsBy(include)
                    .setScanners(scanners));
    activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
  }

  private void rescannImpl(final ConfigurationBuilder configuration) {
    synchronized (obj) {
      final LocalDateTime now = LocalDateTime.now();
      final Reflections reflections = new Reflections(configuration);
      this.reflections.merge(reflections);
      refreshActivatedPkgMap(now, reflections);
      clearCaches();
    }
  }

  private ConfigurationBuilder createConfigurationBuilder() {
    final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.setUrls(ClasspathHelper.forJavaClassPath());
    if (parallelExecutors.get()) return configurationBuilder.useParallelExecutor();
    else return configurationBuilder;
  }

  private Scanner[] createScanners() {
    final Scanner[] sccannerArray = new Scanner[4];
    sccannerArray[0] = new SubTypesScanner();
    sccannerArray[1] = new TypeAnnotationsScanner();
    sccannerArray[2] = new MethodAnnotationsScanner();
    sccannerArray[3] = new PkgTypesScanner();
    //    sccannerArray[4] = new StaticMetricsProxyScanner();
    //    sccannerArray[5] = new StaticLoggingProxyScanner();
    return sccannerArray;
  }

  private void refreshActivatedPkgMap(final LocalDateTime now, final Reflections reflections) {
    reflections
        .getStore()
        .get(index(PkgTypesScanner.class))
        .keySet()
        .forEach(pkgName -> activatedPackagesMap.put(pkgName, now));
  }

  public void clearCaches() {
    methodsAnnotatedWithCache.clear();
    subTypeOfCache.clear();
    subTypeOfCacheWithoutInterfacesnadGenerated.clear();
    typesAnnotatedWithCache.clear();
  }

  private String index(Class clazz) {
    return clazz.getSimpleName();
  }

  public void rescan(String pkgPrefix, URL... urls) {
    rescannImpl(createConfigurationBuilder()
                    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
                    .setUrls(urls)
                    .setScanners(createScanners()));
    activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
  }

  public void rescan(String pkgPrefix, Collection<URL> urls) {
    rescannImpl(createConfigurationBuilder()
                    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
                    .setUrls(urls)
                    .setScanners(createScanners()));
    activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
  }

  public Set<String> getActivatedPkgs() {
    return new HashSet<>(activatedPackagesMap.keySet());
  }

  public boolean isPkgPrefixActivated(final String pkgPrefix) {
    return activatedPackagesMap.containsKey(pkgPrefix);
  }

  public LocalDateTime getPkgPrefixActivatedTimestamp(final String pkgPrefix) {
    return activatedPackagesMap.getOrDefault(pkgPrefix, LocalDateTime.MIN);
  }

  //  //TODO to complex for performance
  //  public <T> Set<Class<? extends T>> getStaticMetricProxiesFor(final Class<T> type) {
  //
  //    final ClassLoader[] classLoaders = reflections.getConfiguration().getClassLoaders();
  //
  //    final Collection<String> metricProxyClassNames = reflections
  //        .getStore()
  //        .get(index(StaticMetricsProxyScanner.class))
  //        .get(type.getName());
  //
  //    final List<Class<? extends T>> classes = ReflectionUtils.forNames(metricProxyClassNames, classLoaders);
  //    return unmodifiableSet(new HashSet<>(classes));
  //
  //  }
  //
  //  public <T> Set<Class<? extends T>> getStaticLoggingProxiesFor(final Class<T> type) {
  //    final ClassLoader[] classLoaders = reflections.getConfiguration().getClassLoaders();
  //    final Collection<String> loggingProxyClassNames = reflections.getStore()
  //        .get(index(StaticLoggingProxyScanner.class))
  //        .get(type.getName());
  //
  //    final List<Class<? extends T>> classes = ReflectionUtils.forNames(loggingProxyClassNames, classLoaders);
  //    return unmodifiableSet(new HashSet<>(classes));
  //  }

  //delegated methods

  public Collection<String> getClassesForPkg(final String pkgName) {
    final Collection<String> clsNames = reflections
        .getStore()
        .get(index(PkgTypesScanner.class))
        .get(pkgName);
    return Collections.unmodifiableCollection(clsNames);
  }

  public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
    if (subTypeOfCache.containsKey(type.getName())) {
      return (Set<Class<? extends T>>) subTypeOfCache.get(type.getName());
    }
    final Set<Class<? extends T>> subTypesOf = reflections.getSubTypesOf(type);
    //    final Set<Class<? extends T>> unmodifiableSet = Collections.unmodifiableSet(subTypesOf);
    subTypeOfCache.put(type.getName(), subTypesOf);
    return subTypesOf;
    //    return reflections.getSubTypesOf(type);
  }


  public <T> Set<Class<? extends T>> getSubTypesWithoutInterfacesAndGeneratedOf(final Class<T> type) {


    if (subTypeOfCacheWithoutInterfacesnadGenerated.containsKey(type.getName())) {
      return (Set<Class<? extends T>>) subTypeOfCacheWithoutInterfacesnadGenerated.get(type.getName());
    }
    final Set<Class<? extends T>> subTypesOf = reflections.getSubTypesOf(type);
    final Set<Class<? extends T>> unmodifiableSet = new DDIReflectionUtils().removeInterfacesAndGeneratedFromSubTypes(subTypesOf);
    subTypeOfCacheWithoutInterfacesnadGenerated.put(type.getName(), unmodifiableSet);
    return unmodifiableSet;
    //    return reflections.getSubTypesOf(type);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
    if (typesAnnotatedWithCache.containsKey(annotation)) return (Set<Class<?>>) typesAnnotatedWithCache.get(annotation);

    final Set<Class<?>> typesAnnotatedWith = unmodifiableSet(reflections.getTypesAnnotatedWith(annotation));
    typesAnnotatedWithCache.put(annotation, typesAnnotatedWith);
    return typesAnnotatedWith;
    //    return reflections.getTypesAnnotatedWith(annotation);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation, final boolean honorInherited) {
    return reflections.getTypesAnnotatedWith(annotation, honorInherited);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation) {
    return reflections.getTypesAnnotatedWith(annotation);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation, final boolean honorInherited) {
    return reflections.getTypesAnnotatedWith(annotation, honorInherited);
  }


  public Set<Method> getMethodsAnnotatedWith(Class clazz, final Annotation annotation) {

    final DataRecords.Pair<String, String> key = new DataRecords.Pair<>(clazz.getName(), annotation.annotationType().getName());

    if (methodsAnnotatedWithCache.containsKey(key)) return methodsAnnotatedWithCache.get(key);

    final Set<Method> allMethods = DDIReflectionUtils.getAllMethods(clazz,
                                                                    (Predicate<Method>) input -> input != null && input.isAnnotationPresent(annotation.annotationType()));

    final Set<Method> unmodifiableSet = unmodifiableSet(allMethods);
    methodsAnnotatedWithCache.put(key, unmodifiableSet);
    return unmodifiableSet;
  }

}
