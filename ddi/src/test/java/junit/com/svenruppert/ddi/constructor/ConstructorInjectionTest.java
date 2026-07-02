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
package junit.com.svenruppert.ddi.constructor;

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
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 3.2 — verifies that DDI prefers an {@code @Inject}-annotated
 * constructor over the default constructor when instantiating a concrete
 * class, and that constructor parameters are resolved recursively (including
 * {@code @Named} / {@code @Qualifier} narrowing).
 */
public class ConstructorInjectionTest {

  @BeforeEach
  void setUp() {
    DI.clearReflectionModel();
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(ConstructorInjectionTest.class);
  }

  @AfterEach
  void tearDown() {
    DI.clearReflectionModel();
  }

  // ===== fixtures: simple constructor injection =====================================

  public static class Dependency {
    public String tag() {
      return "dep";
    }
  }

  public static class CtorInjected {
    private final Dependency dependency;

    @Inject
    public CtorInjected(Dependency dependency) {
      this.dependency = dependency;
    }

    public Dependency dependency() {
      return dependency;
    }
  }

  // ===== fixtures: qualifier-aware constructor injection ============================

  public interface Renderer {
    String render();
  }

  @Named("primary")
  public static class PrimaryRenderer
      implements Renderer {
    @Override
    public String render() {
      return "primary";
    }
  }

  @Named("secondary")
  public static class SecondaryRenderer
      implements Renderer {
    @Override
    public String render() {
      return "secondary";
    }
  }

  public static class QualifierCtorHolder {
    private final Renderer renderer;

    @Inject
    public QualifierCtorHolder(@Named("primary") Renderer renderer) {
      this.renderer = renderer;
    }

    public Renderer renderer() {
      return renderer;
    }
  }

  // ===== fixtures: multiple @Inject constructors → exception =======================

  public static class TwoInjectCtors {
    @Inject
    public TwoInjectCtors() {
    }

    @Inject
    public TwoInjectCtors(Dependency d) {
    }
  }

  // ===== fixtures: package-private @Inject ctor — exercises setAccessible(true) =====

  public static class PackagePrivateCtor {
    private final Dependency dependency;

    @Inject
    PackagePrivateCtor(Dependency dependency) {
      this.dependency = dependency;
    }

    public Dependency dependency() {
      return dependency;
    }
  }

  // ===== fixtures: backwards-compat — no @Inject ctor uses default constructor =====

  public static class PlainDefault {
    private final boolean built;

    public PlainDefault() {
      this.built = true;
    }

    public boolean built() {
      return built;
    }
  }

  // ===== tests ======================================================================

  @Test
  public void injectAnnotatedConstructorIsUsedAndParameterResolved() {
    final CtorInjected instance = DI.activateDI(CtorInjected.class);
    assertNotNull(instance);
    assertNotNull(instance.dependency(),
                  "constructor parameter must be resolved by the container");
    assertEquals("dep", instance.dependency().tag());
  }

  @Test
  public void namedQualifierOnConstructorParameterNarrowsCandidates() {
    final QualifierCtorHolder holder = DI.activateDI(QualifierCtorHolder.class);
    assertNotNull(holder.renderer());
    assertTrue(holder.renderer() instanceof PrimaryRenderer,
               "the @Named(\"primary\") parameter must produce a PrimaryRenderer");
    assertEquals("primary", holder.renderer().render());
  }

  @Test
  public void multipleInjectConstructorsThrowDDIModelException() {
    final DDIModelException ex = assertThrows(DDIModelException.class,
                                              () -> DI.activateDI(TwoInjectCtors.class));
    assertTrue(ex.getMessage().contains("multiple @Inject constructors"),
               () -> "exception must explain the ambiguity: " + ex.getMessage());
    assertTrue(ex.getMessage().contains(TwoInjectCtors.class.getName()),
               () -> "exception must mention the offending class: " + ex.getMessage());
  }

  @Test
  public void packagePrivateInjectConstructorIsMadeAccessible() {
    // Exercises setAccessible(true) — without it, reflective newInstance on a
    // non-public constructor fails with IllegalAccessException from outside the
    // declaring package.
    final PackagePrivateCtor instance = DI.activateDI(PackagePrivateCtor.class);
    assertNotNull(instance);
    assertNotNull(instance.dependency());
    assertEquals("dep", instance.dependency().tag());
  }

  @Test
  public void classWithoutInjectConstructorStillUsesDefaultConstructor() {
    final PlainDefault instance = DI.activateDI(PlainDefault.class);
    assertNotNull(instance);
    assertTrue(instance.built(),
               "default constructor must still run when no @Inject ctor is present");
  }
}
