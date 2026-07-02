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
package junit.com.svenruppert.ddi.producer.v004;

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
import com.svenruppert.ddi.Produces;
import com.svenruppert.ddi.producer.Producer;
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

public class Producer004Test
    extends DDIBaseTest {


  private static boolean producerUssed;
  @Inject
  Service service;

  @Test
  public void test001() {
    DI.activateDI(this);
    Assertions.assertEquals(service.getClass(), ServiceImpl.class);
    Assertions.assertTrue(producerUssed);
  }

  public interface Service { }

  public static class ServiceImpl
      implements Service { }

  @Produces(Service.class)
  public static class ServiceProducer
      implements Producer<Service> {
    @Override
    public Service create() {
      producerUssed = true;
      return new ServiceImpl();
    }
  }

  @Produces(ServiceImpl.class)
  public static class ServiceImplProducer
      implements Producer<Service> {
    @Override
    public Service create() {
      throw new RuntimeException("wrong producer activated");
    }
  }


}
