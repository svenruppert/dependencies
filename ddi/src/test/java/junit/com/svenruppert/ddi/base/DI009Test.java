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
package junit.com.svenruppert.ddi.base;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DDIModelException;
import com.svenruppert.ddi.DI;

import javax.inject.Inject;

public class DI009Test
    extends DDIBaseTest {

  @Test()
  public void test001() {
    Service service = new Service();
    Assertions.assertThrows(DDIModelException.class, ()-> DI.activateDI(service));

  }


  public interface SubService {
  }

  public class Service {
    @Inject SubService suService;
  }

  public class FaultyServiceImpl implements SubService{
    public FaultyServiceImpl() {
      throw new RuntimeException("OOOPS");
    }
  }
}
