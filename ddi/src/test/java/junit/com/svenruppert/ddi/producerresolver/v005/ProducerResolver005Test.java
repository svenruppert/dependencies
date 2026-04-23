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
package junit.com.svenruppert.ddi.producerresolver.v005;

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
import com.svenruppert.ddi.Produces;
import com.svenruppert.ddi.ResponsibleFor;
import com.svenruppert.ddi.producer.Producer;
import com.svenruppert.ddi.producerresolver.ProducerResolver;
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProducerResolver005Test
    extends DDIBaseTest {


  @Test()
  public void test001() {
    Assertions.assertThrows(DDIModelException.class, () -> DI.activateDI(MyService.class));
  }

  public interface MyService {
    String doWork(String txt);
  }

  @Produces(MyService.class)
  public static class Producer_A1
      implements Producer<MyService> {


    @Override
    public MyService create() {
      return null;
    }
  }

  @Produces(MyService.class)
  public static class Producer_A2
      implements Producer<MyService> {

    @Override
    public MyService create() {
      return null;
    }
  }

  @ResponsibleFor(MyService.class)
  public static class MyProducerResolver_A
      implements ProducerResolver<MyService, Producer<MyService>> {

    //no default constructor
    public MyProducerResolver_A(String txt) {
    }

    @Override
    public Class resolve(final Class<? extends MyService> interf) {
      return null;
    }
  }
}