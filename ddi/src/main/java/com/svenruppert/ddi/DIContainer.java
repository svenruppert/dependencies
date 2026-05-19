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
package com.svenruppert.ddi;

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

import com.svenruppert.ddi.bootstrap.ClassResolverCheck001;
import com.svenruppert.ddi.implresolver.ClassResolver;
import com.svenruppert.ddi.producer.InstanceCreator;
import com.svenruppert.ddi.reflections.ReflectionsModel;
import com.svenruppert.ddi.scopes.InjectionScope;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Instance-based dependency-injection container. Holds the {@link ReflectionsModel},
 * the resolver / producer caches and the scope maps that were previously held in
 * static fields across {@link DI}, {@code ImplementingClassResolver},
 * {@code ProducerLocator} and {@code InjectionScopeManager}.
 *
 * <p>The {@link #global() global} singleton powers the legacy static {@link DI}
 * facade so existing callers see no behavioural change. New code can instantiate
 * a {@code DIContainer} directly to obtain hermetic state (own reflection model,
 * own scopes, own caches) — useful for parallel test setups.
 */
public final class DIContainer {

  public static final String PACKAGES_FILE_PROPERTY = "com.svenruppert.ddi.packagesfile";
  private static final Logger LOGGER = LoggerFactory.getLogger(DIContainer.class);
  private static final DIContainer GLOBAL = new DIContainer();

  private final String scanPrefix;
  private volatile ReflectionsModel reflectionsModel;
  private volatile boolean bootstrapedNeeded = true;

  // ImplementingClassResolver cache: interface → cached ClassResolver class
  private final Map<Class<?>, Class<? extends ClassResolver>> implResolverCache = new ConcurrentHashMap<>();

  // ProducerLocator cache: clazzOrInterf → set of @Produces-annotated producer classes
  private final Map<Class<?>, Set<Class<?>>> producerCache = new ConcurrentHashMap<>();

  // InjectionScopeManager state
  private final Map<String, String> classNameToScopeName = new ConcurrentHashMap<>();
  private final Map<String, InjectionScope> injectionScopeMap = new ConcurrentHashMap<>();

  /**
   * Default container — scans the {@value ReflectionsModel#DEFAULT_SCAN_PREFIX} package
   * prefix. For a different prefix use {@link #DIContainer(String)} or
   * {@link #builder()}.
   */
  public DIContainer() {
    this(ReflectionsModel.DEFAULT_SCAN_PREFIX);
  }

  /**
   * Builds a container that scans the given {@code scanPrefix} instead of the default.
   * The prefix is remembered: subsequent {@link #clearReflectionModel()} calls recreate
   * the {@link ReflectionsModel} with the same prefix.
   */
  public DIContainer(final String scanPrefix) {
    this.scanPrefix = scanPrefix;
    this.reflectionsModel = new ReflectionsModel(scanPrefix);
    reInitAllScopes();
  }

  public static DIContainer global() {
    return GLOBAL;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Scan prefix in use by this container. Mirrors {@code FilterBuilder#includePackage}
   * input used to build the underlying {@link ReflectionsModel}.
   */
  public String scanPrefix() {
    return scanPrefix;
  }

  /**
   * Fluent builder for a {@link DIContainer}. Today only {@link #withScanPrefix(String)}
   * is exposed; further config options (custom scanners, initial packages, scope set)
   * are planned for later phases.
   */
  public static final class Builder {
    private String scanPrefix = ReflectionsModel.DEFAULT_SCAN_PREFIX;

    private Builder() {
    }

    public Builder withScanPrefix(final String scanPrefix) {
      this.scanPrefix = scanPrefix;
      return this;
    }

    public DIContainer build() {
      return new DIContainer(scanPrefix);
    }
  }

  // ===== bootstrap / lifecycle ===========================================================

  public synchronized void bootstrap() {
    clearImplResolverCache();
    if (bootstrapedNeeded) {
      final String packageFilePath = System.getProperty(PACKAGES_FILE_PROPERTY);
      if (packageFilePath != null && !packageFilePath.isEmpty()) {
        bootstrapFromResource(packageFilePath);
      } else {
        reflectionsModel.rescan("");
      }
    }
    bootstrapedNeeded = false;
  }

  public synchronized void clearReflectionModel() {
    reflectionsModel = new ReflectionsModel(scanPrefix);
    clearCaches();
    reInitAllScopes();
    bootstrapedNeeded = true;
  }

  public synchronized void activatePackages(Class<?> clazz) {
    reflectionsModel.rescan(clazz.getPackage().getName());
    clearCaches();
    bootstrapedNeeded = false;
  }

  public synchronized void activatePackages(String pkg) {
    reflectionsModel.rescan(pkg);
    clearCaches();
    bootstrapedNeeded = false;
  }

  public synchronized void activatePackages(String pkg, URL... urls) {
    reflectionsModel.rescan(pkg, urls);
    clearCaches();
    bootstrapedNeeded = false;
  }

  public synchronized void activatePackages(String pkg, Collection<URL> urls) {
    reflectionsModel.rescan(pkg, urls);
    clearCaches();
    bootstrapedNeeded = false;
  }

  public void checkActiveModel() {
    new ClassResolverCheck001(this).execute();
  }

  private void bootstrapFromResource(String path) {
    try (InputStream is = ClassLoader.getSystemResourceAsStream(path)) {
      if (is != null) {
        bootstrapFromResource(is);
      } else {
        throw new IOException();
      }
    } catch (IOException e) {
      loadFilesystemResource(path, e);
    }
  }

  private void loadFilesystemResource(String path, IOException e) {
    try (InputStream is = new FileInputStream(path)) {
      bootstrapFromResource(is);
    } catch (IOException e1) {
      LOGGER.warn(String.format("Error loading file <%s> <%s>", path, e.getMessage()));
      throw new DDIModelException("Unable to load packages from file", e1);
    }
  }

  private void bootstrapFromResource(InputStream inputStream) {
    String line;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
      while ((line = reader.readLine()) != null) {
        reflectionsModel.rescan(line);
      }
    } catch (IOException e) {
      LOGGER.warn("Error loading packages");
      throw new DDIModelException("Unable to load packages from file", e);
    }
  }

  private void clearCaches() {
    clearImplResolverCache();
    clearProducerCache();
    cleanUpScopes();
    reflectionsModel.clearCaches();
  }

  // ===== activateDI / injection ==========================================================

  public synchronized <T> T activateDI(T instance) {
    if (bootstrapedNeeded) bootstrap();
    injectAttributes(instance);
    initialize(instance);
    return instance;
  }

  public synchronized <T> T activateDI(Class<T> clazz2Instanciate) {
    if (bootstrapedNeeded) bootstrap();
    final T instance = new InstanceCreator(this).instantiate(clazz2Instanciate);
    injectAttributes(instance);
    initialize(instance);
    return instance;
  }

  private <T> void injectAttributes(final T rootInstance) {
    injectAttributesForClass(rootInstance.getClass(), rootInstance);
  }

  private <T> void injectAttributesForClass(Class<?> targetClass, T rootInstance) {
    Class<?> superclass = targetClass.getSuperclass();
    if (superclass != null) {
      injectAttributesForClass(superclass, rootInstance);
    }
    final Field[] fields = targetClass.getDeclaredFields();
    for (final Field field : fields) {
      if (field.isAnnotationPresent(Inject.class)) {
        LOGGER.info("found field: {}", field.getName());
        final Class<?> targetType = field.getType();
        LOGGER.info("field type: {}", targetType.getName());
        final Set<Annotation> qualifiers = extractQualifiers(field);
        Object value = new InstanceCreator(this).instantiate(targetType, qualifiers);
        activateDI(value);
        injectIntoField(field, rootInstance, value);
      }
    }
  }

  /**
   * Returns the set of jakarta.inject qualifier annotations present on the given
   * field — i.e. annotations whose type is {@link Named} or itself meta-annotated
   * with {@link Qualifier}. Used by the interface-resolution path to narrow the
   * implementation candidates before the {@code ClassResolver} step.
   */
  static Set<Annotation> extractQualifiers(final Field field) {
    final Set<Annotation> qualifiers = new java.util.LinkedHashSet<>();
    for (final Annotation a : field.getAnnotations()) {
      final Class<? extends Annotation> at = a.annotationType();
      if (at.equals(Named.class) || at.isAnnotationPresent(Qualifier.class)) {
        qualifiers.add(a);
      }
    }
    return qualifiers;
  }

  private void injectIntoField(final Field field, final Object instance, final Object target) {
    try {
      field.setAccessible(true);
      field.set(instance, target);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      LOGGER.warn("Cannot set field: ", ex);
      throw new IllegalStateException("Cannot set field: " + field, ex);
    }
  }

  private void initialize(Object instance) {
    final Set<Method> methodsAnnotatedWith
        = reflectionsModel.getMethodsAnnotatedWith(instance.getClass(), PostConstruct.class);
    methodsAnnotatedWith.forEach(m -> {
      try {
        m.setAccessible(true);
        m.invoke(instance);
      } catch (IllegalAccessException | InvocationTargetException e) {
        LOGGER.warn("method could not invoked ", e);
      }
    });
  }

  // ===== implementing-class resolver =====================================================

  public <T> Class<? extends T> resolveImplementingClass(final Class<T> interf) {
    return resolveImplementingClass(interf, java.util.Set.of());
  }

  /**
   * Resolves {@code interf} to a concrete implementation, taking the supplied
   * jakarta.inject qualifier annotations into account. The candidate set is
   * narrowed by qualifier match before the {@code ClassResolver} mechanism runs.
   * <ul>
   *   <li>If qualifiers narrow the candidates to exactly one match, that
   *       implementation is used regardless of any {@code ClassResolver}.</li>
   *   <li>If qualifiers narrow the candidates to zero, a {@link DDIModelException}
   *       is thrown — the inject point asked for an implementation that does not
   *       exist.</li>
   *   <li>If qualifiers narrow to multiple matches (or no qualifiers were given),
   *       the existing {@code ClassResolver}-based resolution applies.</li>
   * </ul>
   */
  public <T> Class<? extends T> resolveImplementingClass(final Class<T> interf, final Set<Annotation> qualifiers) {
    if (!interf.isInterface()) return interf;
    final Set<Class<? extends T>> all = getSubTypesWithoutInterfacesAndGeneratedOf(interf);
    if (all.isEmpty()) return interf;

    final Set<Class<? extends T>> candidates;
    if (qualifiers.isEmpty()) {
      candidates = all;
    } else {
      candidates = narrowByQualifiers(all, qualifiers);
      if (candidates.isEmpty()) {
        throw new DDIModelException("no implementation of " + interf
                                    + " matches qualifiers " + qualifiers
                                    + " — known candidates: " + all);
      }
    }

    if (candidates.size() == 1) return handleOneSubType(interf, candidates.iterator().next());
    return handleManySubTypes(interf, candidates);
  }

  private <T> Set<Class<? extends T>> narrowByQualifiers(final Set<Class<? extends T>> candidates,
                                                          final Set<Annotation> required) {
    return candidates.stream()
        .filter(c -> matchesAllQualifiers(c, required))
        .collect(Collectors.toSet());
  }

  private boolean matchesAllQualifiers(final Class<?> candidate, final Set<Annotation> required) {
    for (final Annotation req : required) {
      final Annotation present = candidate.getAnnotation(req.annotationType());
      if (present == null) return false;
      if (req instanceof Named reqNamed) {
        if (!reqNamed.value().equals(((Named) present).value())) return false;
      } else if (!req.equals(present)) {
        return false;
      }
    }
    return true;
  }

  private <T> Class<? extends T> handleOneSubType(final Class<T> interf, final Object o) {
    final Class<? extends T> implClass = (Class<? extends T>) o;
    final Set<Class<?>> producersForInterface = findProducersFor(interf);
    final Set<Class<?>> producersForImpl = findProducersFor(implClass);
    //@formatter:off
    if (!producersForInterface.isEmpty() && !producersForImpl.isEmpty()) return interf;
    if (producersForInterface.isEmpty()  && producersForImpl.isEmpty())  return implClass;
    if (producersForImpl.isEmpty())                                      return interf;
    if (producersForInterface.isEmpty())                                 return implClass;
    //@formatter:on
    throw new IllegalStateException("unreachable: producer-set classification for " + interf);
  }

  private <T> Class<? extends T> handleManySubTypes(final Class<T> interf, final Set<Class<? extends T>> subTypesOf) {
    final Class<? extends ClassResolver> cached = implResolverCache.get(interf);
    if (cached != null) {
      return handleOneResolver(interf, cached);
    }

    final List<Class<? extends ClassResolver>> resolvers = getSubTypesWithoutInterfacesAndGeneratedOf(ClassResolver.class)
        .stream()
        .filter(c -> c.isAnnotationPresent(ResponsibleFor.class))
        .filter(c -> interf.equals(c.getAnnotation(ResponsibleFor.class).value()))
        .collect(Collectors.toList());

    if (resolvers.size() == 1) {
      final Class<? extends ClassResolver> resolverClass = resolvers.get(0);
      implResolverCache.put(interf, resolverClass);
      return handleOneResolver(interf, resolverClass);
    }
    if (resolvers.isEmpty()) {
      return handleNoResolvers(interf, subTypesOf);
    }
    throw new DDIModelException("interface with multiple implementations and more as 1 ClassResolver = "
                                + interf + " ClassResolver: " + resolvers);
  }

  private <T> Class<? extends T> handleOneResolver(final Class<T> interf,
                                                   final Class<? extends ClassResolver> resolverClass) {
    try {
      final ClassResolver<T> resolver = resolverClass.getDeclaredConstructor().newInstance();
      return resolver.resolve(interf);
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      LOGGER.warn("could not create instance ", e);
      throw new DDIModelException(interf + " -- " + e);
    }
  }

  private <T> Class<? extends T> handleNoResolvers(final Class<T> interf, final Set<Class<? extends T>> subTypesOf) {
    final Set<Class<?>> producersForInterface = findProducersFor(interf);
    if (producersForInterface.isEmpty()) {
      final StringBuilder msg = new StringBuilder("interface with multiple implementations and no ClassResolver= " + interf);
      msg.append(subTypesOf.stream().map(c -> "impl. : " + c.getName()).collect(Collectors.toList()));
      throw new DDIModelException(msg.toString());
    }
    if (producersForInterface.size() == 1) return interf;

    final StringBuilder msg = new StringBuilder("interface with multiple implementations and no ClassResolver and n Producers f the interface = " + interf);
    msg.append(subTypesOf.stream().map(c -> "impl. : " + c.getName()).collect(Collectors.toList()));
    msg.append(producersForInterface.stream().map(c -> "producer. : " + c.getName()).collect(Collectors.toList()));
    throw new DDIModelException(msg.toString());
  }

  public void clearImplResolverCache() {
    implResolverCache.clear();
  }

  // ===== producer locator ================================================================

  public Set<Class<?>> findProducersFor(final Class<?> clazzOrInterf) {
    return producerCache.computeIfAbsent(clazzOrInterf, key -> {
      final Set<Class<?>> producers = getTypesAnnotatedWith(Produces.class)
          .stream()
          .filter(producerClass -> {
            final Produces annotation = producerClass.getAnnotation(Produces.class);
            return annotation.value().equals(key);
          })
          .collect(Collectors.toSet());
      return Collections.unmodifiableSet(producers);
    });
  }

  public void clearProducerCache() {
    producerCache.clear();
  }

  // ===== scope manager ===================================================================

  public <T> T getScopedInstance(final Class<T> target) {
    if (target == null) return null;
    final String targetName = target.getName();
    final String scopeName = classNameToScopeName.get(targetName);
    if (scopeName == null) return null;
    final InjectionScope scope = injectionScopeMap.get(scopeName);
    return scope == null ? null : scope.getInstance(targetName);
  }

  public <T> void storeScopedInstance(final Class<T> targetClass, final T instance) {
    final String targetName = targetClass.getName();
    final String scopeName = classNameToScopeName.get(targetName);
    if (scopeName == null) return;
    final InjectionScope scope = injectionScopeMap.get(scopeName);
    if (scope != null) scope.storeInstance(targetClass, instance);
  }

  public boolean isManagedScope(Class<?> clazz) {
    if (clazz == null) return false;
    return classNameToScopeName.containsKey(clazz.getName());
  }

  public synchronized void cleanUpScopes() {
    final Set<Class<? extends InjectionScope>> scopesFromModel = getSubTypesOf(InjectionScope.class);
    registerNewScopes(scopesFromModel);
    removeOldScopes(scopesFromModel);
  }

  private void registerNewScopes(Set<Class<? extends InjectionScope>> scopeClasses) {
    scopeClasses
        .stream()
        .map(c -> {
          try {
            LOGGER.info("registerNewScopes - create instance of class {}", c.getName());
            Constructor<? extends InjectionScope> declaredConstructor = c.getDeclaredConstructor();
            return declaredConstructor.newInstance();
          } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.warn("could not create an instance ", e);
          }
          return null;
        })
        .filter(Objects::nonNull)
        .filter(scope -> !injectionScopeMap.containsKey(scope.getScopeName()))
        .forEach(scope -> injectionScopeMap.put(scope.getScopeName(), scope));
  }

  private void removeOldScopes(Set<Class<? extends InjectionScope>> scopeClasses) {
    final Set<String> namesFromModel = getNamesFromScopes(scopeClasses);
    injectionScopeMap.keySet().stream()
        .filter(scope -> !namesFromModel.contains(scope))
        .toList()
        .forEach(this::removeScope);
  }

  private Set<String> getNamesFromScopes(Set<Class<? extends InjectionScope>> scopes) {
    LOGGER.info(scopes.toString());
    return scopes.stream()
        .map(c -> {
          try {
            return c.getDeclaredConstructor().newInstance();
          } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.warn("could not create new instance ", e);
          }
          return null;
        })
        .filter(Objects::nonNull)
        .map(InjectionScope::getScopeName)
        .collect(Collectors.toSet());
  }

  public void registerClassForScope(final Class<?> clazz, final String scopeName) {
    if (injectionScopeMap.containsKey(scopeName)) {
      classNameToScopeName.putIfAbsent(clazz.getName(), scopeName);
    }
  }

  public void deRegisterClassForScope(final Class<?> clazz) {
    classNameToScopeName.remove(clazz.getName());
  }

  public String scopeForClass(final Class<?> clazz) {
    return classNameToScopeName.getOrDefault(clazz.getName(), "PER INJECT");
  }

  public Set<String> listAllActiveScopeNames() {
    return Collections.unmodifiableSet(injectionScopeMap.keySet());
  }

  public void clearScope(final String scopeName) {
    injectionScopeMap.computeIfPresent(scopeName, (s, scope) -> {
      scope.clear();
      return scope;
    });
  }

  private void removeScope(final String scopeName) {
    injectionScopeMap.computeIfPresent(scopeName, (s, scope) -> {
      scope.clear();
      classNameToScopeName.keySet().forEach(className ->
          classNameToScopeName.computeIfPresent(className,
                                                (cn, mapped) -> mapped.equals(scopeName) ? null : mapped));
      return null;
    });
  }

  public synchronized void reInitAllScopes() {
    classNameToScopeName.clear();
    injectionScopeMap.values().forEach(InjectionScope::clear);
    injectionScopeMap.clear();
    final Set<Class<? extends InjectionScope>> subTypesOf = getSubTypesOf(InjectionScope.class);
    for (Class<? extends InjectionScope> c : subTypesOf) {
      try {
        final InjectionScope scope = c.getDeclaredConstructor().newInstance();
        injectionScopeMap.put(scope.getScopeName(), scope);
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        LOGGER.warn("could not create an instance ", e);
      }
    }
  }

  // ===== reflection delegation ===========================================================

  public boolean isPkgPrefixActivated(final String pkgPrefix) {
    return reflectionsModel.isPkgPrefixActivated(pkgPrefix);
  }

  public boolean isPkgPrefixActivated(final Class<?> clazz) {
    return reflectionsModel.isPkgPrefixActivated(clazz.getPackage().getName());
  }

  public LocalDateTime getPkgPrefixActivatedTimestamp(final String pkgPrefix) {
    return reflectionsModel.getPkgPrefixActivatedTimestamp(pkgPrefix);
  }

  public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
    return reflectionsModel.getSubTypesOf(type);
  }

  public <T> Set<Class<? extends T>> getSubTypesWithoutInterfacesAndGeneratedOf(final Class<T> type) {
    return reflectionsModel.getSubTypesWithoutInterfacesAndGeneratedOf(type);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
    return reflectionsModel.getTypesAnnotatedWith(annotation);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation, final boolean honorInherited) {
    return reflectionsModel.getTypesAnnotatedWith(annotation, honorInherited);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation) {
    return reflectionsModel.getTypesAnnotatedWith(annotation);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation, final boolean honorInherited) {
    return reflectionsModel.getTypesAnnotatedWith(annotation, honorInherited);
  }

  /**
   * Class names (FQN) discovered by the {@code PkgTypesScanner} under the given
   * package. Used for diagnostic queries against the underlying
   * {@link ReflectionsModel} without exposing the model itself.
   */
  public Collection<String> getClassesForPkg(final String pkgName) {
    return reflectionsModel.getClassesForPkg(pkgName);
  }

  /**
   * Names of all package prefixes that have been activated on this container
   * via {@code activatePackages(...)}. The returned set is a defensive copy.
   */
  public Set<String> getActivatedPkgs() {
    return reflectionsModel.getActivatedPkgs();
  }
}
