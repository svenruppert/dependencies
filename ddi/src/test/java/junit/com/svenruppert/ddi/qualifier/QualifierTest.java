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
package junit.com.svenruppert.ddi.qualifier;

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

import com.svenruppert.ddi.DDIModelException;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.ResponsibleFor;
import com.svenruppert.ddi.implresolver.ClassResolver;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Qualifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 2.5 — verifies the jakarta.inject {@code @Named} / {@code @Qualifier}
 * narrowing path. When an {@code @Inject} field carries qualifier annotations
 * the candidate set is filtered before the {@code ClassResolver} mechanism runs.
 */
public class QualifierTest {

  @BeforeEach
  void setUp() {
    DI.clearReflectionModel();
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(QualifierTest.class);
  }

  @AfterEach
  void tearDown() {
    DI.clearReflectionModel();
  }

  // ===== fixtures =====================================================================

  public interface Service {
    String tag();
  }

  @Named("primary")
  public static class PrimaryService
      implements Service {
    @Override
    public String tag() {
      return "primary";
    }
  }

  @Named("secondary")
  public static class SecondaryService
      implements Service {
    @Override
    public String tag() {
      return "secondary";
    }
  }

  public static class UnnamedService
      implements Service {
    @Override
    public String tag() {
      return "unnamed";
    }
  }

  public static class PrimaryHolder {
    @Inject
    @Named("primary")
    Service service;
  }

  public static class SecondaryHolder {
    @Inject
    @Named("secondary")
    Service service;
  }

  public static class UnknownNameHolder {
    @Inject
    @Named("does-not-exist")
    Service service;
  }

  // ===== plain @Named ==================================================================

  @Test
  public void namedQualifierSelectsTheMatchingImpl() {
    final PrimaryHolder holder = DI.activateDI(new PrimaryHolder());
    assertNotNull(holder.service);
    assertEquals("primary", holder.service.tag());
    assertTrue(holder.service instanceof PrimaryService);
  }

  @Test
  public void differentNamedValueSelectsDifferentImpl() {
    final SecondaryHolder holder = DI.activateDI(new SecondaryHolder());
    assertNotNull(holder.service);
    assertEquals("secondary", holder.service.tag());
    assertTrue(holder.service instanceof SecondaryService);
  }

  @Test
  public void unknownNameThrowsWithExplanatoryMessage() {
    final DDIModelException ex = assertThrows(DDIModelException.class,
                                              () -> DI.activateDI(new UnknownNameHolder()));
    assertTrue(ex.getMessage().contains("does-not-exist"),
               () -> "exception must mention the unmatched qualifier: " + ex.getMessage());
    assertTrue(ex.getMessage().contains(Service.class.getName()),
               () -> "exception must mention the requested interface: " + ex.getMessage());
  }

  // ===== custom @Qualifier-meta-annotation ============================================

  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.TYPE})
  public @interface Audited {
  }

  @Audited
  public static class AuditedThing
      implements Thing {
    @Override
    public String label() {
      return "audited";
    }
  }

  public static class PlainThing
      implements Thing {
    @Override
    public String label() {
      return "plain";
    }
  }

  public interface Thing {
    String label();
  }

  public static class AuditedHolder {
    @Inject
    @Audited
    Thing thing;
  }

  @Test
  public void customQualifierMetaAnnotationNarrowsCandidates() {
    final AuditedHolder holder = DI.activateDI(new AuditedHolder());
    assertNotNull(holder.thing);
    assertEquals("audited", holder.thing.label());
  }

  // ===== @Inject without qualifier — backwards-compat with ClassResolver =============

  public interface PickedService {
    String pick();
  }

  public static class PickedImplA
      implements PickedService {
    @Override
    public String pick() {
      return "A";
    }
  }

  public static class PickedImplB
      implements PickedService {
    @Override
    public String pick() {
      return "B";
    }
  }

  @ResponsibleFor(PickedService.class)
  public static class PickedServiceResolver
      implements ClassResolver<PickedService> {
    @Override
    public Class<? extends PickedService> resolve(Class<PickedService> interf) {
      return PickedImplB.class;
    }
  }

  public static class PickedHolder {
    @Inject
    PickedService service;
  }

  @Test
  public void injectionWithoutQualifierFallsThroughToClassResolver() {
    final PickedHolder holder = DI.activateDI(new PickedHolder());
    assertNotNull(holder.service);
    assertEquals("B", holder.service.pick(),
                 "ClassResolver must still drive resolution when no qualifier is present");
  }
}
