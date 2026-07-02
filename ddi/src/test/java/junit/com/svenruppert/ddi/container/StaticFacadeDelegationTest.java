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

import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.DIContainer;
import com.svenruppert.ddi.implresolver.ImplementingClassResolver;
import com.svenruppert.ddi.producer.ProducerLocator;
import com.svenruppert.ddi.scopes.InjectionScopeManager;
import com.svenruppert.ddi.scopes.provided.JVMSingletonInjectionScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that the static facade classes preserved for backwards compatibility
 * ({@link InjectionScopeManager}, {@link ImplementingClassResolver},
 * {@link ProducerLocator} and {@link DI}) still route correctly through
 * {@link DIContainer#global()}. Each test pins down one delegator.
 */
public class StaticFacadeDelegationTest {

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

  @BeforeEach
  void setup() {
    DI.clearReflectionModel();
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(StaticFacadeDelegationTest.class);
  }

  @AfterEach
  void tearDown() {
    DI.clearReflectionModel();
  }

  // ImplementingClassResolver static delegators
  @Test
  public void implementingClassResolverStaticsRouteThroughGlobalContainer() {
    final Class<? extends Service> resolved = ImplementingClassResolver.resolve(Service.class);
    assertSame(ServiceImpl.class, resolved,
               "static ImplementingClassResolver.resolve(...) must produce the same result as DI.resolveImplementingClass(...)");

    ImplementingClassResolver.clearCache();
    final Class<? extends Service> resolvedAgain = ImplementingClassResolver.resolve(Service.class);
    assertSame(ServiceImpl.class, resolvedAgain);
  }

  // ProducerLocator.clearCache static delegator
  @Test
  public void producerLocatorClearCacheActuallyClears() {
    final Set<Class<?>> first = ProducerLocator.findProducersFor(Service.class);
    assertNotNull(first);
    ProducerLocator.clearCache();
    final Set<Class<?>> second = ProducerLocator.findProducersFor(Service.class);
    assertNotNull(second);
    assertEquals(first, second, "results before and after clear should match");
  }

  // InjectionScopeManager — every backwards-compat static method
  @Test
  public void injectionScopeManagerStaticsRouteThroughGlobalContainer() {
    final String singletonScope = JVMSingletonInjectionScope.class.getSimpleName();

    assertFalse(InjectionScopeManager.isManagedByMe(ServiceImpl.class),
                "before register: not managed");
    assertEquals("PER INJECT", InjectionScopeManager.scopeForClass(ServiceImpl.class));
    assertNull(InjectionScopeManager.getInstance(ServiceImpl.class),
               "before any storeInstance: null");

    InjectionScopeManager.registerClassForScope(ServiceImpl.class, singletonScope);
    assertTrue(InjectionScopeManager.isManagedByMe(ServiceImpl.class));
    assertEquals(singletonScope, InjectionScopeManager.scopeForClass(ServiceImpl.class));

    final Set<String> scopeNames = InjectionScopeManager.listAllActiveScopeNames();
    assertTrue(scopeNames.contains(singletonScope));

    final ServiceImpl probe = new ServiceImpl();
    InjectionScopeManager.manageInstance(ServiceImpl.class, probe);
    assertSame(probe, InjectionScopeManager.getInstance(ServiceImpl.class));

    InjectionScopeManager.clearScope(singletonScope);
    assertNull(InjectionScopeManager.getInstance(ServiceImpl.class),
               "after clearScope: stored instance gone");

    InjectionScopeManager.deRegisterClassForScope(ServiceImpl.class);
    assertFalse(InjectionScopeManager.isManagedByMe(ServiceImpl.class));

    InjectionScopeManager.cleanUp();
    InjectionScopeManager.reInitAllScopes();
    assertTrue(InjectionScopeManager.listAllActiveScopeNames().contains(singletonScope),
               "reInitAllScopes must re-discover the built-in JVMSingletonInjectionScope");
  }

  // DIContainer null-class scope check (defensive contract)
  @Test
  public void isManagedScopeReturnsFalseForNullClass() {
    assertFalse(DIContainer.global().isManagedScope(null));
  }

  // DI.getSubTypesOf returns the full set, not just emptySet
  @Test
  public void diGetSubTypesOfReturnsNonEmptySetForKnownInterface() {
    final Set<?> subs = DI.getSubTypesOf(com.svenruppert.ddi.scopes.InjectionScope.class);
    assertNotNull(subs);
    assertFalse(subs.isEmpty(),
                "InjectionScope must have at least JVMSingletonInjectionScope as a subtype");
  }

  // DI.getSubTypesWithoutInterfacesAndGeneratedOf must not return emptySet for the same query
  @Test
  public void diGetSubTypesWithoutInterfacesAndGeneratedOfReturnsConcreteImpls() {
    final Set<?> subs = DI.getSubTypesWithoutInterfacesAndGeneratedOf(com.svenruppert.ddi.scopes.InjectionScope.class);
    assertNotNull(subs);
    assertFalse(subs.isEmpty(),
                "must surface the concrete JVMSingletonInjectionScope");
  }
}
