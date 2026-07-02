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
package junit.com.svenruppert.ddi.scopes.v004;

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
import com.svenruppert.ddi.scopes.InjectionScopeManager;
import com.svenruppert.ddi.scopes.provided.JVMSingletonInjectionScope;
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Scopes004Test
    extends DDIBaseTest {

  @Test
  public void test001() {
    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);

    Assertions.assertNotEquals(serviceA.value(), serviceB.value());
  }

  @Test
  public void test002() {
    DI.bootstrap();
    DI.registerClassForScope(SingleResource.class, JVMSingletonInjectionScope.class.getSimpleName());

    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);

    Assertions.assertEquals(serviceA.value(), serviceB.value());
  }

  @Test
  public void test003() {
    DI.registerClassForScope(SingleResource.class, JVMSingletonInjectionScope.class.getSimpleName());
    DI.registerClassForScope(ServiceImpl.class, JVMSingletonInjectionScope.class.getSimpleName());
    DI.registerClassForScope(Service.class, JVMSingletonInjectionScope.class.getSimpleName());

    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);

    Assertions.assertEquals(serviceA.value(), serviceB.value());
  }

  @Test
  public void test004() {
    final String scopeBefore = InjectionScopeManager.scopeForClass(SingleResource.class);
    DI.registerClassForScope(SingleResource.class, JVMSingletonInjectionScope.class.getSimpleName());
    final String scopeAfter = InjectionScopeManager.scopeForClass(SingleResource.class);

    Assertions.assertNotEquals(scopeBefore, scopeAfter);

    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);

    Assertions.assertEquals(serviceA.value(), serviceB.value());
  }

}
