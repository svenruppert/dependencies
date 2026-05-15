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
package junit.com.svenruppert.ddi.classresolver.v004;

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
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class ClassResolver004Test
    extends DDIBaseTest {

  @Test()
  public void testProducer001() {

    final BusinessModule businessModule = new BusinessModule();
    try {
      DI.activateDI(businessModule);
      Assertions.fail("too bad..");
    } catch (DDIModelException e) {
      final String message = e.getMessage();
      Assertions.assertTrue(message.contains("interface with multiple implementations and more as 1 ClassResolver"));
    }
  }

  public interface Service {
    String work(String txt);
  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolverA
      implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return null;
    }
  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolverB
      implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return null;
    }
  }

  public static class BusinessModule {
    @Inject
    Service service;

    public String work(String txt) {
      return service.work(txt);
    }
  }

  public static class ServiceImplA
      implements Service {
    public String work(String txt) {
      return txt;
    }
  }

  public static class ServiceImplB
      implements Service {
    public String work(String txt) {
      return txt;
    }

  }
}
