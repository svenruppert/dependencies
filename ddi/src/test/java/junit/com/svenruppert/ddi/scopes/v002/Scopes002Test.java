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
package junit.com.svenruppert.ddi.scopes.v002;

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
import com.svenruppert.ddi.scopes.InjectionScope;
import com.svenruppert.ddi.scopes.provided.JVMSingletonInjectionScope;
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Scopes002Test
    extends DDIBaseTest {

  @Test
  public void test001() {

    DI.registerClassForScope(SingletonTestClass.class, JVMSingletonInjectionScope.class.getSimpleName());
    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);
    Assertions.assertTrue(serviceA instanceof SingletonTestClass);
    Assertions.assertTrue(serviceB instanceof SingletonTestClass);

    Assertions.assertEquals(serviceA, serviceB); // Singleton at impl level

  }

  @Test
  public void test002() {

    DI.registerClassForScope(Service.class, JVMSingletonInjectionScope.class.getSimpleName());
    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);
    Assertions.assertTrue(serviceA instanceof SingletonTestClass);
    Assertions.assertTrue(serviceB instanceof SingletonTestClass);

    Assertions.assertEquals(serviceA, serviceB); // Singleton at impl level

  }


  public interface Service {

  }


  public static class SingletonTestClass
      implements Service {
  }


  public static class TestScope
      extends InjectionScope {
    @Override
    public <T> T getInstance(final String clazz) {
      return null;
    }

    @Override
    public <T> void storeInstance(final Class<T> targetClassOrInterface, final T instance) {

    }

    @Override
    public void clear() {

    }

    @Override
    public String getScopeName() {
      return TestScope.class.getSimpleName();
    }
  }


}
