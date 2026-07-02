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
package junit.com.svenruppert.ddi.reflections;

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



import com.svenruppert.ddi.reflections.DDIReflectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ReflectionUtils Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jan 25, 2016</pre>
 */
public class DDIReflectionUtilsTest {

  @BeforeEach
  public void before() {
  }

  @AfterEach
  public void after() {
  }

  /**
   * Method: checkInterface(final Type aClass, Class targetInterface)
   */
  @Test
  public void testCheckInterface() {

    final DDIReflectionUtils utils = new DDIReflectionUtils();

    Assertions.assertTrue(utils.checkInterface(ServiceImpl_A.class, Service.class));
    Assertions.assertFalse(utils.checkInterface(Service.class, ServiceImpl_A.class));

    Assertions.assertFalse(utils.checkInterface(ServiceImpl_A.class, NoService.class));
    Assertions.assertFalse(utils.checkInterface(NoService.class, ServiceImpl_A.class));


    Assertions.assertFalse(utils.checkInterface(NoService.class, Service.class));
    Assertions.assertFalse(utils.checkInterface(Service.class, NoService.class));

    Assertions.assertFalse(utils.checkInterface(Service.class, ServiceLev2.class));
    Assertions.assertTrue(utils.checkInterface(ServiceLev2.class, Service.class));

    Assertions.assertTrue(utils.checkInterface(ServiceImpl_B.class, Service.class));
    Assertions.assertFalse(utils.checkInterface(Service.class, ServiceImpl_B.class));

    Assertions.assertFalse(utils.checkInterface(ServiceImpl_B.class, NoService.class));
    Assertions.assertFalse(utils.checkInterface(NoService.class, ServiceImpl_B.class));

  }


  public interface Service {
  }

  public interface ServiceLev2
      extends Service {
  }


  public interface NoService {
  }

  public static class ServiceImpl_A
      implements Service {
  }

  public static class ServiceImpl_B
      implements ServiceLev2 {
  }


}
