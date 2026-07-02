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

import java.lang.annotation.Annotation;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

/**
 * Static convenience facade. All operations delegate to
 * {@link DIContainer#global()}. For hermetic state (parallel test setups, own
 * Reflections model, own scopes) instantiate a {@link DIContainer} directly.
 */
public final class DI {

  public static final String ORG_RAPIDPM_DDI_PACKAGESFILE = DIContainer.PACKAGES_FILE_PROPERTY;

  private DI() {
  }

  public static void checkActiveModel() {
    DIContainer.global().checkActiveModel();
  }

  public static void bootstrap() {
    DIContainer.global().bootstrap();
  }

  public static void clearReflectionModel() {
    DIContainer.global().clearReflectionModel();
  }

  public static void activatePackages(Class clazz) {
    DIContainer.global().activatePackages(clazz);
  }

  public static void activatePackages(String pkg) {
    DIContainer.global().activatePackages(pkg);
  }

  public static void activatePackages(String pkg, URL... urls) {
    DIContainer.global().activatePackages(pkg, urls);
  }

  public static void activatePackages(String pkg, Collection<URL> urls) {
    DIContainer.global().activatePackages(pkg, urls);
  }

  public static <T> T activateDI(T instance) {
    return DIContainer.global().activateDI(instance);
  }

  public static <T> T activateDI(Class<T> clazz2Instanciate) {
    return DIContainer.global().activateDI(clazz2Instanciate);
  }

  public static Set<String> listAllActiveScopes() {
    return DIContainer.global().listAllActiveScopeNames();
  }

  public static void registerClassForScope(Class clazz, String scope) {
    DIContainer.global().registerClassForScope(clazz, scope);
  }

  public static void deRegisterClassForScope(Class clazz) {
    DIContainer.global().deRegisterClassForScope(clazz);
  }

  public static <T> Class<? extends T> resolveImplementingClass(final Class<T> interf) {
    return DIContainer.global().resolveImplementingClass(interf);
  }

  public static boolean isPkgPrefixActivated(final String pkgPrefix) {
    return DIContainer.global().isPkgPrefixActivated(pkgPrefix);
  }

  public static boolean isPkgPrefixActivated(final Class clazz) {
    return DIContainer.global().isPkgPrefixActivated(clazz);
  }

  public static LocalDateTime getPkgPrefixActivatedTimestamp(final String pkgPrefix) {
    return DIContainer.global().getPkgPrefixActivatedTimestamp(pkgPrefix);
  }

  public static <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
    return DIContainer.global().getSubTypesOf(type);
  }

  public static <T> Set<Class<? extends T>> getSubTypesWithoutInterfacesAndGeneratedOf(final Class<T> type) {
    return DIContainer.global().getSubTypesWithoutInterfacesAndGeneratedOf(type);
  }

  public static Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
    return DIContainer.global().getTypesAnnotatedWith(annotation);
  }

  public static Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation, final boolean honorInherited) {
    return DIContainer.global().getTypesAnnotatedWith(annotation, honorInherited);
  }

  public static Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation) {
    return DIContainer.global().getTypesAnnotatedWith(annotation);
  }

  public static Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation, final boolean honorInherited) {
    return DIContainer.global().getTypesAnnotatedWith(annotation, honorInherited);
  }
}
