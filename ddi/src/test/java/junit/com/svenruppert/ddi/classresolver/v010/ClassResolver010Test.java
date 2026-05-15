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
package junit.com.svenruppert.ddi.classresolver.v010;

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
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Set;

public class ClassResolver010Test
    extends DDIBaseTest {

  @Inject
  Service service;

  @Test
  public void test001() {
    final Set<Class<? extends Service>> subTypesOf = DI.getSubTypesOf(Service.class);
    for (Class<? extends Service> aClass : subTypesOf) {
      System.out.println("aClass = " + aClass);
      System.out.println("aClass.isInterface() = " + aClass.isInterface());
    }
    //DI.activateDI(this);
    Assertions.assertNotNull(service);

  }

  public interface Service {
    String doWork(String str);
  }

  public interface ServiceA
      extends Service {
    String doWork(String str);
  }

  public interface ServiceB
      extends Service {
    String doWork(String str);
  }

  public interface ServiceAA
      extends ServiceA {
    String doWork(String str);
  }

  public static class ServiceImpl
      implements ServiceAA {

    @Override
    public String doWork(final String str) {
      return "ServiceImpl " + str;
    }
  }


}
