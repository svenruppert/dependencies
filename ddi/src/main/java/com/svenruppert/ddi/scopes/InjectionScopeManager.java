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
package com.svenruppert.ddi.scopes;

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

import com.svenruppert.ddi.DIContainer;

import java.util.Set;

/**
 * Thin static facade over the global {@link DIContainer}'s scope state.
 * State (class → scope mapping, scope-name → scope instance) now lives
 * entirely in the container; this class keeps its public static API for
 * backwards compatibility with existing callers and tests.
 */
public final class InjectionScopeManager {

  private InjectionScopeManager() {
  }

  public static <T> T getInstance(final Class<T> target) {
    return DIContainer.global().getScopedInstance(target);
  }

  public static <T> void manageInstance(Class<T> targetClass, T instance) {
    DIContainer.global().storeScopedInstance(targetClass, instance);
  }

  public static boolean isManagedByMe(Class clazz) {
    return DIContainer.global().isManagedScope(clazz);
  }

  public static void cleanUp() {
    DIContainer.global().cleanUpScopes();
  }

  public static void registerClassForScope(final Class clazz, final String scopeName) {
    DIContainer.global().registerClassForScope(clazz, scopeName);
  }

  public static void deRegisterClassForScope(final Class clazz) {
    DIContainer.global().deRegisterClassForScope(clazz);
  }

  public static String scopeForClass(final Class clazz) {
    return DIContainer.global().scopeForClass(clazz);
  }

  public static Set<String> listAllActiveScopeNames() {
    return DIContainer.global().listAllActiveScopeNames();
  }

  public static void clearScope(final String scopeName) {
    DIContainer.global().clearScope(scopeName);
  }

  public static void reInitAllScopes() {
    DIContainer.global().reInitAllScopes();
  }
}
