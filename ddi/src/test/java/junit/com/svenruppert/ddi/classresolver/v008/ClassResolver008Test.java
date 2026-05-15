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
package junit.com.svenruppert.ddi.classresolver.v008;

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
import com.svenruppert.ddi.Produces;
import com.svenruppert.ddi.producer.Producer;
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class ClassResolver008Test
    extends DDIBaseTest {

  /**
   * no Anonymous Class inside the create() Method.. compare to ClassResolverTest007
   */
  @Test
  public void testProxy001() {
    final BusinessModulVirtual instance = new BusinessModulVirtual();
    Assertions.assertNotNull(instance);
    Assertions.assertNull(instance.service);
    DI.activateDI(instance);

    Assertions.assertNotNull(instance.service);
    //    Assertions.assertTrue(java.lang.reflect.Proxy.isProxyClass(instance.service.getClass()));

    final String hello = instance.service.doWork("Hello");
    Assertions.assertNotNull(hello);
    Assertions.assertEquals("created by Producer", hello);


  }


  public interface Service {
    String doWork(String str);
  }

  public static class BusinessModulVirtual {
    @Inject
    Service service;

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class ServiceA
      implements Service {

    public ServiceA() {
      System.out.println(" ServiceA = constructed...");
    }

    @Override
    public String doWork(final String str) {
      return "ServiceA_" + str;
    }
  }


  @Produces(Service.class)
  public static class ServiceProducer
      implements Producer<Service> {
    @Override
    public Service create() {
      return str -> "created by Producer";
    }
  }
}
