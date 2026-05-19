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
package junit.com.svenruppert.ddi.container;

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

import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.DIContainer;
import com.svenruppert.ddi.scopes.provided.JVMSingletonInjectionScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that {@link DIContainer} instances are hermetic: state mutations on
 * one container do not leak into another container or into {@link DI#global()}.
 */
public class ContainerIsolationTest {

  @BeforeEach
  void resetGlobal() {
    DI.clearReflectionModel();
  }

  @AfterEach
  void tearDownGlobal() {
    DI.clearReflectionModel();
  }

  public interface Service {
    String tag();
  }

  public static class ServiceImpl
      implements Service {
    @Override
    public String tag() {
      return "impl";
    }
  }

  public static class Holder {
    @Inject
    Service service;
  }

  // Isolated activation: activating a package in one container must NOT activate it in the global one.
  @Test
  public void packageActivationDoesNotLeakBetweenContainers() {
    final DIContainer container = new DIContainer();
    container.activatePackages(ContainerIsolationTest.class);

    assertTrue(container.isPkgPrefixActivated(ContainerIsolationTest.class));
    assertFalse(DIContainer.global().isPkgPrefixActivated(ContainerIsolationTest.class),
                "package activated in a fresh container must not be visible in the global container");
  }

  // Isolated singleton scopes: registering a class for scope in one container leaves the other untouched.
  @Test
  public void scopeRegistrationDoesNotLeakBetweenContainers() {
    final DIContainer a = new DIContainer();
    final DIContainer b = new DIContainer();
    a.activatePackages(ContainerIsolationTest.class);
    b.activatePackages(ContainerIsolationTest.class);

    a.registerClassForScope(ServiceImpl.class, JVMSingletonInjectionScope.class.getSimpleName());

    assertTrue(a.isManagedScope(ServiceImpl.class));
    assertFalse(b.isManagedScope(ServiceImpl.class),
                "scope registration on container a must not leak into container b");
    assertFalse(DIContainer.global().isManagedScope(ServiceImpl.class),
                "scope registration on a separate container must not leak into the global container");
  }

  // The same scope-registered class produces the same instance within one container ...
  @Test
  public void scopedInstanceIsSharedWithinSameContainer() {
    final DIContainer container = new DIContainer();
    container.activatePackages(ContainerIsolationTest.class);
    container.registerClassForScope(ServiceImpl.class, JVMSingletonInjectionScope.class.getSimpleName());

    final Service first = container.activateDI(Service.class);
    final Service second = container.activateDI(Service.class);
    assertNotNull(first);
    assertSame(first, second, "scope cache must yield the same instance within one container");
  }

  // Note: a true cross-container non-shared-instance assertion would require a
  // per-container scope. The only provided scope today is JVMSingletonInjectionScope,
  // which is JVM-wide by design (`private static Map<String, Object> SINGLETONS`)
  // and therefore intentionally shared across containers. A non-shared scope
  // (ThreadScope, RequestScope, ...) is on the roadmap under Phase 3.1.

  // Field injection inside a container resolves via that container, not the global one.
  @Test
  public void fieldInjectionUsesOwningContainer() {
    final DIContainer container = new DIContainer();
    container.activatePackages(ContainerIsolationTest.class);

    final Holder holder = container.activateDI(new Holder());
    assertNotNull(holder.service, "field injection must populate the @Inject field");
    assertTrue(holder.service instanceof ServiceImpl,
               "injected impl must come from the container's own reflection model");
  }
}
