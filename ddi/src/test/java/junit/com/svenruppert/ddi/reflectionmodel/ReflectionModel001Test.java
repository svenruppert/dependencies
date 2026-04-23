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
package junit.com.svenruppert.ddi.reflectionmodel;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class ReflectionModel001Test {

  @Test()
  public void test001() {
    DI.clearReflectionModel();
    DI.activatePackages("com.svenruppert");
    try {
      DI.activateDI(new BusinessModule());
      Assertions.fail("to bad....");
    } catch (DDIModelException e) {
      final String message = e.getMessage();
      Assertions.assertTrue(message.contains("only interfaces found for interface"));
    }
  }


  @Test
  public void test002() {
    DI.clearReflectionModel();
    DI.activatePackages("com.svenruppert");
    DI.activatePackages("junit.com.svenruppert.ddi.reflectionmodel");
    final BusinessModule businessModule = DI.activateDI(new BusinessModule());
    Assertions.assertNotNull(businessModule);
    Assertions.assertNotNull(businessModule.service);
  }


  interface Service {
  }

  public static class ServiceImpl
      implements Service {
  }

  public static class BusinessModule {
    @Inject
    Service service;
  }


}