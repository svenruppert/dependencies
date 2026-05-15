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
package junit.com.svenruppert.ddi.instancecreator.v002;

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
import com.svenruppert.ddi.Produces;
import com.svenruppert.ddi.producer.InstanceCreator;
import com.svenruppert.ddi.producer.Producer;
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
 * Created by RapidPM - Team on 02.08.16.
 */
public class InstanceCreator002Test
    extends DDIBaseTest {
  //Demo Kommentare
  @Test
  public void test001() {
    try {
      final ServiceImpl instantiate = new InstanceCreator().instantiate(ServiceImpl.class);
      Assertions.fail("too bad..");
    } catch (DDIModelException e) {
      Assertions.assertTrue(e.getMessage().contains("to many Producer and no Produce"));
    }
  }

  public static class ServiceImpl {
  }


  @Produces(ServiceImpl.class)
  public static class ServiceProducer
      implements Producer<ServiceImpl> {
    @Override
    public ServiceImpl create() {
      return new ServiceImpl() {
      };
    }
  }

  @Produces(ServiceImpl.class)
  public static class ServiceImplProducer
      implements Producer<ServiceImpl> {
    @Override
    public ServiceImpl create() {
      return new ServiceImpl();
    }
  }

}
