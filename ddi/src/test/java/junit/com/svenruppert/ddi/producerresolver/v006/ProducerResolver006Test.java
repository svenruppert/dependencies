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
package junit.com.svenruppert.ddi.producerresolver.v006;

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
import com.svenruppert.ddi.ResponsibleFor;
import com.svenruppert.ddi.implresolver.ClassResolver;
import com.svenruppert.ddi.producer.Producer;
import com.svenruppert.ddi.producerresolver.ProducerResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProducerResolver006Test {


  public static Boolean toggle = true;
  //build a Toggle ProducerResolver
  public static Boolean toggleProducer = true;

  @Test
  public void test001() {

    DI.clearReflectionModel();
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(ProducerResolver006Test.class);

    Assertions.assertEquals(ServiceB.class, DI.activateDI(Service.class).getClass());
    Assertions.assertEquals(false, toggle);
    Assertions.assertEquals(true, toggleProducer);
    Assertions.assertEquals(ServiceA.class, DI.activateDI(Service.class).getClass());
    Assertions.assertEquals(true, toggle);
    Assertions.assertEquals(false, toggleProducer);

    Assertions.assertEquals(ServiceB.class, DI.activateDI(Service.class).getClass());
    Assertions.assertEquals(false, toggle);
    Assertions.assertEquals(false, toggleProducer);

    Assertions.assertEquals(ServiceA.class, DI.activateDI(Service.class).getClass());
    Assertions.assertEquals(true, toggle);
    Assertions.assertEquals(true, toggleProducer);

    DI.clearReflectionModel();

  }

  public interface Service {
    String doWork(String txt);
  }

  public static class ServiceA
      implements Service {
    private String postVAlue;

    public String doWork(String txt) {
      return txt + "A - " + postVAlue;
    }
  }

  public static class ServiceB
      implements Service {
    public String doWork(String txt) {
      return txt + "B";
    }
  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolver
      implements ClassResolver<Service> {

    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      toggle = !toggle;
      System.out.println("toggle = " + toggle);
      return (toggle) ? ServiceA.class : ServiceB.class;
    }
  }

  @ResponsibleFor(ServiceA.class)
  public static class ServiceAProducerResolver
      implements ProducerResolver<Service, Producer<Service>> {
    @Override
    public Class<? extends Producer<Service>> resolve(final Class<? extends Service> interf) {
      toggleProducer = !toggleProducer;
      System.out.println("toggleProducer = " + toggleProducer);
      return (toggleProducer) ? ServiceAProducerA.class : ServiceAProducerB.class;

    }
  }

  @Produces(ServiceA.class)
  public static class ServiceAProducerA
      implements Producer<Service> {

    @Override
    public Service create() {
      final ServiceA serviceA = new ServiceA();
      serviceA.postVAlue = "Producer A";
      return serviceA;
    }
  }

  @Produces(ServiceA.class)
  public static class ServiceAProducerB
      implements Producer<Service> {

    @Override
    public Service create() {
      final ServiceA serviceA = new ServiceA();
      serviceA.postVAlue = "Producer B";
      return serviceA;
    }
  }


}