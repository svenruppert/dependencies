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
package junit.com.svenruppert.ddi.producer.v006;

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
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class Producer006Test
    extends DDIBaseTest {


  @Inject
  Service service;

  @Test
  public void test001() {
    DI.activateDI(this);
  }

  public interface Service { }

  public static class ServiceImpl_A
      implements Service { }

  public static class ServiceImpl_B
      implements Service { }

  @Produces(Service.class)
  public static class ServiceProducer_A
      implements Producer<Service> {
    @Override
    public Service create() {
      return new ServiceImpl_A();
    }
  }


  @Produces(ServiceImpl_A.class)
  public static class ServiceImpl_A_Producer
      implements Producer<Service> {
    @Override
    public Service create() {
      throw new RuntimeException("wrong producer activated");
    }
  }

  @Produces(ServiceImpl_B.class)
  public static class ServiceImpl_B_Producer
      implements Producer<Service> {
    @Override
    public Service create() {
      throw new RuntimeException("wrong producer activated");
    }
  }
}
