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
package junit.com.svenruppert.ddi.classresolver.v007;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DDIModelException;
import com.svenruppert.ddi.DI;
import junit.com.svenruppert.ddi.DDIBaseTest;

public class ClassResolver007Test
    extends DDIBaseTest {


  /**
   * Anonymous Class inside the create() Method.. will be removed
   * <p>
   * 1 Interface , 1 Impl -> Impl ServiceA will be used
   *
   */
  @Test()
  public void testProxy001() {
    final BusinessModulVirtual instance = new BusinessModulVirtual();
    Assertions.assertNotNull(instance);
    Assertions.assertNull(instance.service);
    Assertions.assertThrows(DDIModelException.class, ()->DI.activateDI(instance));
  }


  public interface Service {
    String doWork(String str);
  }

  public static class BusinessModulVirtual {
    @Inject Service service;

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class BusinessModul {
    private final Service service = new Service() { //this is a implementation of the Interface...
      @Override
      public String doWork(final String str) {
        return "created by Anonymous";
      }
    };

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class ServiceA implements Service {

    public ServiceA() {
      System.out.println(" ServiceA = constructed...");
    }

    @Override
    public String doWork(final String str) {
      return "ServiceA_" + str;
    }
  }

}
