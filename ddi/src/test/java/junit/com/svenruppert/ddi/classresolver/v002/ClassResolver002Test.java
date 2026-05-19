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
package junit.com.svenruppert.ddi.classresolver.v002;

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

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class ClassResolver002Test
    extends DDIBaseTest {

  @Test()
  public void testProducer001() {
    final BusinessModule businessModule = new BusinessModule();

    try {
      DI.activateDI(businessModule);
    } catch (Exception e) {
      final Class<? extends Exception> aClass = e.getClass();
      Assertions.assertEquals(DDIModelException.class, aClass);
      final String message = e.getMessage();
      Assertions.assertTrue(message.startsWith("interface with multiple implementations"));
      Assertions.assertTrue(message.contains("more as 1 ClassResolver"));
      Assertions.assertTrue(message.contains("ClassResolver002Test$Service"));
    }
  }

  public interface Service {
    String work(String txt);

    SubService getSubService();

    boolean isPostconstructed();
  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolverA
      implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return ServiceImplA.class;
    }
  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolverB
      implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return ServiceImplB.class;
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
    @Inject
    SubService subService;
    boolean postconstructed;

    public String work(String txt) {
      return subService.work(txt);
    }

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }

    @Override
    public SubService getSubService() {
      return subService;
    }


    public boolean isPostconstructed() {
      return postconstructed;
    }


  }

  public static class ServiceImplB
      implements Service {
    @Inject
    SubService subService;
    boolean postconstructed;

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }

    public String work(String txt) {
      return subService.work(txt);
    }


    @Override
    public SubService getSubService() {
      return subService;
    }


    public boolean isPostconstructed() {
      return postconstructed;
    }


  }

  public static class SubService {
    @Inject
    SubSubService subSubService;
    boolean postconstructed;

    public String work(final String txt) {
      return subSubService.work(txt);
    }

    public boolean isPostconstructed() {
      return postconstructed;
    }

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }
  }

  public static class SubSubService {
    public String work(final String txt) {
      return "SubSubService " + txt;
    }
  }


}
