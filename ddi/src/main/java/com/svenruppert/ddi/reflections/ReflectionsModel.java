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
 * #L%
 */



import com.svenruppert.functional.model.DataRecords;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableSet;


public class ReflectionsModel {

  public static final String DEFAULT_SCAN_PREFIX = "com.svenruppert";

  // Read–write lock guarding the underlying `reflections` store.
  // - rescannImpl mutates the store (merge) and clears the derived caches → write lock.
  // - getSubTypesOf / getTypesAnnotatedWith / getClassesForPkg read the store → read lock.
  // The per-cache `ConcurrentHashMap`s remain in place for cache-level concurrency on hot
  // (already-populated) reads, but the lock ensures readers never observe a half-merged store.
  private final ReadWriteLock storeLock = new ReentrantReadWriteLock();

  private final Map<String, LocalDateTime> activatedPackagesMap = new ConcurrentHashMap<>();
  private final Map<DataRecords.Pair<String, String>, Set<Method>> methodsAnnotatedWithCache = new ConcurrentHashMap<>();
  private final Map<String, Set> subTypeOfCache = new ConcurrentHashMap<>();
  private final Map<String, Set> subTypeOfCacheWithoutInterfacesnadGenerated = new ConcurrentHashMap<>();
  private final Map<Class<? extends Annotation>, Set> typesAnnotatedWithCache = new ConcurrentHashMap<>();
  private final ThreadLocal<Boolean> parallelExecutors = ThreadLocal.withInitial(() -> false);
  private final Reflections reflections;

  public ReflectionsModel() {
    this(DEFAULT_SCAN_PREFIX);
  }

  public ReflectionsModel(final String scanPrefix) {
    this.reflections = new Reflections(
        createConfigurationBuilder()
            .filterInputsBy(new FilterBuilder().includePackage(scanPrefix))
            .setScanners(createScanners())
    );
  }

  public void setParallelExecutors(final boolean parallelExecutors) {
    this.parallelExecutors.set(parallelExecutors);
  }

  public void rescan(final String pkgPrefix) {
    rescannImpl(createConfigurationBuilder()
                    .filterInputsBy(new FilterBuilder().includePackage(pkgPrefix))
                    .setScanners(createScanners()));
    activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
  }

  private void rescannImpl(final ConfigurationBuilder configuration) {
    storeLock.writeLock().lock();
    try {
      final LocalDateTime now = LocalDateTime.now();
      final Reflections reflections = new Reflections(configuration);
      this.reflections.merge(reflections);
      refreshActivatedPkgMap(now, reflections);
      clearCaches();
    } finally {
      storeLock.writeLock().unlock();
    }
  }

  private ConfigurationBuilder createConfigurationBuilder() {
    final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.setUrls(ClasspathHelper.forJavaClassPath());
    configurationBuilder.setParallel(parallelExecutors.get());
    // org.reflections 0.10.x expands super-types into the store by default, which causes
    // classes outside the filter to surface (e.g. via supertype links from scanned subclasses).
    // The previous reflections8 behaviour did not do this — keep the contract identical.
    configurationBuilder.setExpandSuperTypes(false);
    return configurationBuilder;
  }

  private Scanner[] createScanners() {
    final Scanner[] sccannerArray = new Scanner[4];
    sccannerArray[0] = new SubTypesScanner();
    sccannerArray[1] = new TypeAnnotationsScanner();
    sccannerArray[2] = new MethodAnnotationsScanner();
    sccannerArray[3] = new PkgTypesScanner();
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
                    .filterInputsBy(new FilterBuilder().includePackage(pkgPrefix))
                    .setUrls(urls)
                    .setScanners(createScanners()));
    activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
  }

  public void rescan(String pkgPrefix, Collection<URL> urls) {
    rescannImpl(createConfigurationBuilder()
                    .filterInputsBy(new FilterBuilder().includePackage(pkgPrefix))
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

  //delegated methods

  public Collection<String> getClassesForPkg(final String pkgName) {
    return underReadLock(() -> Collections.unmodifiableCollection(
        reflections.getStore().get(index(PkgTypesScanner.class)).get(pkgName)));
  }

  public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
    return (Set<Class<? extends T>>) subTypeOfCache.computeIfAbsent(type.getName(),
        k -> underReadLock(() -> reflections.getSubTypesOf(type)));
  }


  public <T> Set<Class<? extends T>> getSubTypesWithoutInterfacesAndGeneratedOf(final Class<T> type) {
    return (Set<Class<? extends T>>) subTypeOfCacheWithoutInterfacesnadGenerated.computeIfAbsent(type.getName(), k -> {
      final Set<Class<? extends T>> subTypesOf = underReadLock(() -> reflections.getSubTypesOf(type));
      return new DDIReflectionUtils().removeInterfacesAndGeneratedFromSubTypes(subTypesOf);
    });
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
    return (Set<Class<?>>) typesAnnotatedWithCache.computeIfAbsent(annotation,
        k -> underReadLock(() -> unmodifiableSet(reflections.getTypesAnnotatedWith(annotation))));
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation, final boolean honorInherited) {
    return underReadLock(() -> reflections.getTypesAnnotatedWith(annotation, honorInherited));
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation) {
    return underReadLock(() -> reflections.getTypesAnnotatedWith(annotation));
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation, final boolean honorInherited) {
    return underReadLock(() -> reflections.getTypesAnnotatedWith(annotation, honorInherited));
  }

  private <T> T underReadLock(final Supplier<T> read) {
    storeLock.readLock().lock();
    try {
      return read.get();
    } finally {
      storeLock.readLock().unlock();
    }
  }


  public Set<Method> getMethodsAnnotatedWith(Class clazz, final Annotation annotation) {
    return getMethodsAnnotatedWith(clazz, annotation.annotationType());
  }

  public Set<Method> getMethodsAnnotatedWith(Class clazz, final Class<? extends Annotation> annotationType) {
    final DataRecords.Pair<String, String> key = new DataRecords.Pair<>(clazz.getName(), annotationType.getName());

    return methodsAnnotatedWithCache.computeIfAbsent(key, k -> {
      final Set<Method> allMethods = DDIReflectionUtils.getAllMethods(clazz,
                                                                      (Predicate<Method>) input -> input != null && input.isAnnotationPresent(annotationType));
      return unmodifiableSet(allMethods);
    });
  }

}
