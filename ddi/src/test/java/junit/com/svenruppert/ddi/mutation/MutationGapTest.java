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
package junit.com.svenruppert.ddi.mutation;

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

import com.svenruppert.ddi.DDIModelException;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.producer.ProducerLocator;
import com.svenruppert.ddi.reflections.ReflectionsModel;
import com.svenruppert.ddi.scopes.provided.JVMSingletonInjectionScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Targeted regression coverage for mutants that survived the first PIT run
 * after the Phase-1 bugfix wave. Each test method names the mutator and the
 * production source it pins down.
 *
 * The tests deliberately do not extend {@code DDIBaseTest} because that base
 * class calls {@link DI#activatePackages(String)} in {@code @BeforeEach}, which
 * flips {@code bootstrapedNeeded} to {@code false} and therefore hides the
 * {@code if (bootstrapedNeeded) bootstrap()} branches inside {@code activateDI}.
 */
public class MutationGapTest {

  @BeforeEach
  void resetDI() {
    DI.clearReflectionModel();
  }

  @AfterEach
  void tearDown() {
    DI.clearReflectionModel();
  }

  // --- DI.activateDI(Class) ----------------------------------------------------------------
  // Kills: DI.activateDI(Class):185 VoidMethodCall — removed call to DI::bootstrap
  // Kills: DI.activateDI(Class):189 VoidMethodCall — removed call to DI::initialize
  @Test
  public void activateDIByClassBootstrapsAndRunsPostConstruct() {
    final PostConstructProbe probe = DI.activateDI(PostConstructProbe.class);
    assertTrue(probe.initialised,
               "@PostConstruct must fire when an instance is created via activateDI(Class)");
    assertTrue(DI.isPkgPrefixActivated(""),
               "activateDI(Class) must trigger bootstrap when bootstrapedNeeded is true");
  }

  // --- DI.activateDI(Object) ---------------------------------------------------------------
  // Kills: DI.activateDI(Object):177 VoidMethodCall — removed call to DI::bootstrap
  @Test
  public void activateDIByInstanceBootstrapsWhenNeeded() {
    final PostConstructProbe probe = new PostConstructProbe();
    DI.activateDI(probe);
    assertTrue(probe.initialised);
    assertTrue(DI.isPkgPrefixActivated(""),
               "activateDI(Object) must trigger bootstrap when bootstrapedNeeded is true");
  }

  // --- DI.isPkgPrefixActivated(Class) ------------------------------------------------------
  // Kills: DI.isPkgPrefixActivated(Class):273 BooleanTrueReturnVals / BooleanFalseReturnVals
  @Test
  public void isPkgPrefixActivatedClassOverloadReflectsActivation() {
    assertFalse(DI.isPkgPrefixActivated(MutationGapTest.class),
                "before activate the class package must not be reported as activated");
    DI.activatePackages(MutationGapTest.class);
    assertTrue(DI.isPkgPrefixActivated(MutationGapTest.class),
               "after activatePackages(Class) the class' package must be reported as activated");
  }

  // --- ImplementingClassResolver.resolveNewForClass:97 ------------------------------------
  // Kills: NO_COVERAGE — replaced return value with null for resolveNewForClass
  @Test
  public void resolveImplementingClassOnConcreteClassReturnsSameClass() {
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(MutationGapTest.class);
    final Class<? extends Concrete> resolved = DI.resolveImplementingClass(Concrete.class);
    assertSame(Concrete.class, resolved,
               "resolveImplementingClass must return the class itself for non-interface input");
  }

  // --- ImplementingClassResolver.handleNoResolvers lambdas --------------------------------
  // Kills: handleNoResolvers$0:165 — replaced "impl. : " return with ""
  // Kills: handleNoResolvers$1:174 — replaced "impl. : " return with ""
  // Kills: handleNoResolvers$2:175 — replaced "producer. : " return with ""
  // (Achieved by asserting that the DDIModelException message lists all impl/producer class names.)
  @Test
  public void exceptionMessageForNImplsListsAllImplementations() {
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(MutationGapTest.class);
    final DDIModelException ex = assertThrows(DDIModelException.class,
                                              () -> DI.activateDI(MultiImpl.class));
    final String message = ex.getMessage();
    assertTrue(message.contains(MultiImplA.class.getName()),
               () -> "message must mention MultiImplA: " + message);
    assertTrue(message.contains(MultiImplB.class.getName()),
               () -> "message must mention MultiImplB: " + message);
  }

  // --- ImplementingClassResolver.handleNoResolvers — n-producers branch ------------------
  // Kills: handleNoResolvers$1:174 — replaced "impl. : " return with ""
  // Kills: handleNoResolvers$2:175 — replaced "producer. : " return with ""
  @Test
  public void exceptionMessageForNImplsAndNProducersListsBoth() {
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(MutationGapTest.class);
    final DDIModelException ex = assertThrows(DDIModelException.class,
                                              () -> DI.activateDI(NoisyInterface.class));
    final String message = ex.getMessage();
    assertTrue(message.contains(NoisyImplA.class.getName()),
               () -> "message must mention NoisyImplA: " + message);
    assertTrue(message.contains(NoisyImplB.class.getName()),
               () -> "message must mention NoisyImplB: " + message);
    assertTrue(message.contains(NoisyProducerOne.class.getName()),
               () -> "message must mention NoisyProducerOne: " + message);
    assertTrue(message.contains(NoisyProducerTwo.class.getName()),
               () -> "message must mention NoisyProducerTwo: " + message);
  }

  // --- ReflectionsModel parallel-path -----------------------------------------------------
  // Kills: setParallelExecutors:93 VoidMethodCall (NO_COVERAGE)
  // Kills: createConfigurationBuilder:119 NullReturnVals (NO_COVERAGE)
  @Test
  public void parallelScannerConfigurationProducesUsableModel() {
    final ReflectionsModel model = new ReflectionsModel();
    model.setParallelExecutors(true);
    model.rescan("com.svenruppert.ddi");
    assertTrue(model.isPkgPrefixActivated("com.svenruppert.ddi"),
               "parallel rescan must record the activated prefix");
    assertFalse(model.getSubTypesOf(com.svenruppert.ddi.scopes.InjectionScope.class).isEmpty(),
                "parallel rescan must populate the reflection model");
  }

  // --- ReflectionsModel.getMethodsAnnotatedWith(Class, Annotation) overload ---------------
  // Kills: getMethodsAnnotatedWith:221 EmptyObjectReturnVals (NO_COVERAGE)
  @Test
  public void getMethodsAnnotatedWithAnnotationOverloadDelegatesToClassOverload() {
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(MutationGapTest.class);
    final ReflectionsModel model = new ReflectionsModel();
    model.rescan(MutationGapTest.class.getPackage().getName());

    final PostConstruct annotation = new PostConstruct() {
      @Override
      public Class<? extends java.lang.annotation.Annotation> annotationType() {
        return PostConstruct.class;
      }
    };
    final Set<Method> viaInstance = model.getMethodsAnnotatedWith(PostConstructProbe.class, annotation);
    final Set<Method> viaClass = model.getMethodsAnnotatedWith(PostConstructProbe.class, PostConstruct.class);
    assertEquals(viaClass, viaInstance,
                 "Annotation-instance overload must produce the same result as the Class overload");
    assertFalse(viaInstance.isEmpty(),
                "PostConstructProbe declares a @PostConstruct method");
  }

  // --- DI.activatePackages(String, URL...) clears caches ----------------------------------
  // Kills: DI.activatePackages(String,URL...):166 — removed call to DI::clearCaches
  // Kills: ReflectionsModel.rescan(String,URL...):147 — removed call to rescannImpl
  @Test
  public void activatePackagesUrlArrayClearsProducerCacheAndScansNewPrefix() {
    DI.activatePackages("com.svenruppert");
    final Set<Class<?>> beforeActivation = ProducerLocator.findProducersFor(NoisyInterface.class);
    assertTrue(beforeActivation.isEmpty(),
               "no producers visible while only com.svenruppert is activated");

    final URL[] urls = ClasspathHelper.forJavaClassPath().toArray(new URL[0]);
    DI.activatePackages(MutationGapTest.class.getPackage().getName(), urls);

    final Set<Class<?>> afterActivation = ProducerLocator.findProducersFor(NoisyInterface.class);
    assertEquals(2, afterActivation.size(),
                 "after activatePackages(String, URL[]) the test producers must be visible "
                     + "— requires both the rescan and the cache invalidation: "
                     + afterActivation);
  }

  // --- DI.activatePackages(String, Collection<URL>) clears caches -------------------------
  // Kills: DI.activatePackages(String,Collection):172 — removed call to DI::clearCaches
  // Kills: ReflectionsModel.rescan(String,Collection):155 — removed call to rescannImpl
  @Test
  public void activatePackagesUrlCollectionClearsProducerCacheAndScansNewPrefix() {
    DI.activatePackages("com.svenruppert");
    final Set<Class<?>> beforeActivation = ProducerLocator.findProducersFor(NoisyInterface.class);
    assertTrue(beforeActivation.isEmpty());

    final Collection<URL> urls = ClasspathHelper.forJavaClassPath();
    DI.activatePackages(MutationGapTest.class.getPackage().getName(), urls);

    final Set<Class<?>> afterActivation = ProducerLocator.findProducersFor(NoisyInterface.class);
    assertEquals(2, afterActivation.size(),
                 "after activatePackages(String, Collection<URL>) the test producers must be visible: "
                     + afterActivation);
  }

  // --- InstanceCreator.createInstanceWithProducers scope storage --------------------------
  // Kills: InstanceCreator.createInstanceWithProducers:200 — removed call to putToScope
  @Test
  public void resolvedProducerResultIsCachedAcrossActivateCalls() {
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(MutationGapTest.class);
    DI.registerClassForScope(ResolvedScopedTarget.class,
                             JVMSingletonInjectionScope.class.getSimpleName());

    final ResolvedScopedTarget first = DI.activateDI(ResolvedScopedTarget.class);
    final ResolvedScopedTarget second = DI.activateDI(ResolvedScopedTarget.class);
    assertSame(first, second,
               "Scope-registered class resolved via ProducerResolver must yield the same instance — "
                   + "requires putToScope inside createInstanceWithProducers");
  }

  // --- InstanceCreator.createNewInstance scope storage ------------------------------------
  // Kills: InstanceCreator.createNewInstance:109 — removed call to putToScope
  @Test
  public void scopedProducerResultIsCachedAcrossActivateCalls() {
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(MutationGapTest.class);
    DI.registerClassForScope(ScopedTarget.class,
                             JVMSingletonInjectionScope.class.getSimpleName());

    final ScopedTarget first = DI.activateDI(ScopedTarget.class);
    final ScopedTarget second = DI.activateDI(ScopedTarget.class);
    assertSame(first, second,
               "Scope-registered class with single producer must yield the same instance — "
                   + "requires putToScope after producer invocation");
  }

  // ---- fixtures --------------------------------------------------------------------------

  public static class PostConstructProbe {
    boolean initialised = false;

    @PostConstruct
    public void init() {
      initialised = true;
    }
  }

  public static class Concrete {
    public int value() {
      return 42;
    }
  }

  public interface MultiImpl {
    String name();
  }

  public static class MultiImplA
      implements MultiImpl {
    @Override
    public String name() {
      return "A";
    }
  }

  public static class MultiImplB
      implements MultiImpl {
    @Override
    public String name() {
      return "B";
    }
  }

  public interface NoisyInterface {
    String tag();
  }

  public static class NoisyImplA
      implements NoisyInterface {
    @Override
    public String tag() {
      return "A";
    }
  }

  public static class NoisyImplB
      implements NoisyInterface {
    @Override
    public String tag() {
      return "B";
    }
  }

  @com.svenruppert.ddi.Produces(NoisyInterface.class)
  public static class NoisyProducerOne
      implements com.svenruppert.ddi.producer.Producer<NoisyInterface> {
    @Override
    public NoisyInterface create() {
      return new NoisyImplA();
    }
  }

  @com.svenruppert.ddi.Produces(NoisyInterface.class)
  public static class NoisyProducerTwo
      implements com.svenruppert.ddi.producer.Producer<NoisyInterface> {
    @Override
    public NoisyInterface create() {
      return new NoisyImplB();
    }
  }

  public static class ScopedTarget {
    public String label() {
      return "scoped";
    }
  }

  @com.svenruppert.ddi.Produces(ScopedTarget.class)
  public static class ScopedTargetProducer
      implements com.svenruppert.ddi.producer.Producer<ScopedTarget> {
    @Override
    public ScopedTarget create() {
      return new ScopedTarget();
    }
  }

  public static class ResolvedScopedTarget {
    public String label() {
      return "resolved-scoped";
    }
  }

  @com.svenruppert.ddi.Produces(ResolvedScopedTarget.class)
  public static class ResolvedScopedTargetProducerOne
      implements com.svenruppert.ddi.producer.Producer<ResolvedScopedTarget> {
    @Override
    public ResolvedScopedTarget create() {
      return new ResolvedScopedTarget();
    }
  }

  @com.svenruppert.ddi.Produces(ResolvedScopedTarget.class)
  public static class ResolvedScopedTargetProducerTwo
      implements com.svenruppert.ddi.producer.Producer<ResolvedScopedTarget> {
    @Override
    public ResolvedScopedTarget create() {
      return new ResolvedScopedTarget();
    }
  }

  @com.svenruppert.ddi.ResponsibleFor(ResolvedScopedTarget.class)
  public static class ResolvedScopedTargetProducerResolver
      implements com.svenruppert.ddi.producerresolver.ProducerResolver<ResolvedScopedTarget, com.svenruppert.ddi.producer.Producer<ResolvedScopedTarget>> {
    @Override
    public Class<? extends com.svenruppert.ddi.producer.Producer<ResolvedScopedTarget>> resolve(Class<? extends ResolvedScopedTarget> interf) {
      return ResolvedScopedTargetProducerOne.class;
    }
  }
}
