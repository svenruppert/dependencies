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
package junit.com.svenruppert.ddi.instancecreator.v004;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.Produces;
import com.svenruppert.ddi.ResponsibleFor;
import com.svenruppert.ddi.producer.InstanceCreator;
import com.svenruppert.ddi.producer.Producer;
import com.svenruppert.ddi.producerresolver.ProducerResolver;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the EUPL, Version 1.2 (the "Licence");
 * you may not use this file except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Created by RapidPM - Team on 02.08.16.
 */
public class InstanceCreator004Test
    extends DDIBaseTest {


  @Test
  public void test001() {
    DI.activateDI("com.svenruppert");
    DI.activateDI("junit.com.svenruppert");
      final ServiceImpl instantiate = new InstanceCreator().instantiate(ServiceImpl.class);
    Assertions.assertNotNull(instantiate);
  }

  public static class ServiceImpl {
  }


  @Produces(ServiceImpl.class)
  public static class ServiceProducer implements Producer<ServiceImpl> {
    @Override
    public ServiceImpl create() {
      return new ServiceImpl() {
      };
    }
  }

  @Produces(ServiceImpl.class)
  public static class ServiceImplProducer implements Producer<ServiceImpl> {
    @Override
    public ServiceImpl create() {
      return new ServiceImpl();
    }
  }

  @ResponsibleFor(ServiceImpl.class)
  public static class ServiceProducerResolver implements ProducerResolver<ServiceImpl,Producer<ServiceImpl>> {

    @Override
    public Class<? extends Producer<ServiceImpl>> resolve(final Class interf) {
      return ServiceProducer.class;
    }
  }

}