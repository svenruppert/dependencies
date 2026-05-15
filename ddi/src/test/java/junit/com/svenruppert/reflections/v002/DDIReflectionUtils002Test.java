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
package junit.com.svenruppert.reflections.v002;

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

import java.util.HashSet;
import java.util.Set;

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
public class DDIReflectionUtils002Test {


  @Test
  public void test001() {
    final DDIReflectionUtils utils = new DDIReflectionUtils();

    final Set<Class<? extends Service>> subTypesOfService = new HashSet<>();
    subTypesOfService.add(Service.class);
    subTypesOfService.add(ServiceA.class);
    subTypesOfService.add(ServiceB.class);
    subTypesOfService.add(ServiceImplA.class);
    subTypesOfService.add(ServiceImplB.class);
    subTypesOfService.add(ServiceImplAB.class);
    subTypesOfService.add(ServiceImplBB.class);


    final Set<Class<? extends Service>> cleared = utils.removeInterfacesAndGeneratedFromSubTypes(subTypesOfService);

    Assertions.assertFalse(cleared.contains(Service.class));
    Assertions.assertFalse(cleared.contains(ServiceA.class));
    Assertions.assertFalse(cleared.contains(ServiceB.class));
    Assertions.assertTrue(cleared.contains(ServiceImplA.class));
    Assertions.assertTrue(cleared.contains(ServiceImplB.class));
    Assertions.assertTrue(cleared.contains(ServiceImplAB.class));
    Assertions.assertTrue(cleared.contains(ServiceImplBB.class));

    final Set<Class<? extends A>> subTypesOfA = new HashSet<>();
    subTypesOfA.add(A.class);
    subTypesOfA.add(B.class);
    subTypesOfA.add(C.class);
    subTypesOfA.add(D.class);
    subTypesOfA.add(E.class);
    final Set<Class<? extends A>> clearedB = utils.removeInterfacesAndGeneratedFromSubTypes(subTypesOfA);

    Assertions.assertTrue(clearedB.contains(A.class));
    Assertions.assertTrue(clearedB.contains(B.class));
    Assertions.assertTrue(clearedB.contains(C.class));
    Assertions.assertTrue(clearedB.contains(D.class));
    Assertions.assertTrue(clearedB.contains(E.class));

  }

  public interface Service {
  }

  public interface ServiceA
      extends Service {
  }

  public interface ServiceB
      extends ServiceA {
  }

  //  @IsGeneratedProxy
  public static class ServiceImplA
      implements Service {
  }

  //  @IsMetricsProxy
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

  //  @IsStaticObjectAdapter
  public static class C
      extends B {
  }

  //  @IsLoggingProxy
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
