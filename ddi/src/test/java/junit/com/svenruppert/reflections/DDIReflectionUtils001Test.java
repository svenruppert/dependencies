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
package junit.com.svenruppert.reflections;

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

import com.svenruppert.ddi.reflections.DDIReflectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DDIReflectionUtils001Test {


  @Test
  public void test001() {

    DDIReflectionUtils utils = new DDIReflectionUtils();

    Assertions.assertTrue(Service.class.isInterface());
    Assertions.assertTrue(ServiceA.class.isInterface());

    Assertions.assertNull(ServiceA.class.getSuperclass());

    Assertions.assertTrue(utils.checkInterface(Service.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(ServiceA.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(ServiceB.class, Service.class));
    //
    Assertions.assertTrue(utils.checkInterface(ServiceImplA.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(ServiceImplB.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(ServiceImplAB.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(ServiceImplBB.class, Service.class));

    Assertions.assertFalse(utils.checkInterface(A.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(B.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(C.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(D.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(E.class, Service.class));

    Assertions.assertTrue(utils.checkInterface(F.class, Service.class));
  }


  public interface Service {
  }

  public interface ServiceA
      extends Service {
  }

  public interface ServiceB
      extends ServiceA {
  }

  public static class ServiceImplA
      implements Service {
  }

  public static class ServiceImplB
      implements ServiceA {
  }

  public static class ServiceImplAB
      extends ServiceImplA {
  }

  public static class ServiceImplBB
      extends ServiceImplB {
  }

  public static class A {
  }

  public static class B
      extends A
      implements Service {
  }

  public static class C
      extends B {
  }

  public static class D
      extends A
      implements ServiceA {
  }

  public static class E
      extends A
      implements ServiceB {
  }

  public static class F
      extends ServiceImplAB {
  }


}