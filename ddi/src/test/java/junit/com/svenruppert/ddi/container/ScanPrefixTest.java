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

import com.svenruppert.ddi.DIContainer;
import com.svenruppert.ddi.reflections.ReflectionsModel;
import com.svenruppert.ddi.scopes.InjectionScope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 2.4 — verifies that a {@link DIContainer} built with a non-default
 * scan prefix only sees classes under that prefix, and that
 * {@link DIContainer#clearReflectionModel()} preserves the configured prefix.
 */
public class ScanPrefixTest {

  @Test
  public void defaultContainerScansComSvenruppert() {
    final DIContainer container = new DIContainer();
    assertEquals(ReflectionsModel.DEFAULT_SCAN_PREFIX, container.scanPrefix());
    assertFalse(container.getSubTypesOf(InjectionScope.class).isEmpty(),
                "default-prefix container must discover InjectionScope subtypes from com.svenruppert");
  }

  @Test
  public void builderScanPrefixIsHonoured() {
    final DIContainer container = DIContainer.builder()
        .withScanPrefix("junit.com.svenruppert.ddi.container")
        .build();
    assertEquals("junit.com.svenruppert.ddi.container", container.scanPrefix());
  }

  @Test
  public void customPrefixContainerDoesNotSeeOutOfPrefixSubtypes() {
    // A prefix that doesn't include com.svenruppert.* and doesn't include any
    // production package: the InjectionScope sub-type set must be empty.
    final DIContainer container = DIContainer.builder()
        .withScanPrefix("does.not.exist.anywhere")
        .build();
    assertNotNull(container.getSubTypesOf(InjectionScope.class));
    assertTrue(container.getSubTypesOf(InjectionScope.class).isEmpty(),
               "scan prefix not matching any package must yield empty sub-type sets");
  }

  @Test
  public void clearReflectionModelPreservesScanPrefix() {
    final DIContainer container = new DIContainer("junit.com.svenruppert.ddi.container");
    assertEquals("junit.com.svenruppert.ddi.container", container.scanPrefix());
    container.clearReflectionModel();
    assertEquals("junit.com.svenruppert.ddi.container", container.scanPrefix(),
                 "scanPrefix must survive clearReflectionModel()");
  }
}
