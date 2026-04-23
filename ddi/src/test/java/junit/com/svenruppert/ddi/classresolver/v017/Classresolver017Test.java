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
package junit.com.svenruppert.ddi.classresolver.v017;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;

/**
 *
 */
public class Classresolver017Test {


  public static interface Service {}

  public static abstract class AbstractService implements Service {}

  public static class ServiceImpl extends AbstractService {}


  @BeforeEach
  void setUp() {
    DI.clearReflectionModel();
    DI.activatePackages("com.svenruppert");
    DI.activatePackages(this.getClass());
  }

  @AfterEach
  void tearDown() {
    DI.clearReflectionModel();
  }

  @Test
  void test001() {
    final Service service = DI.activateDI(Service.class);
    Assertions.assertEquals(service.getClass() , ServiceImpl.class);
  }

  @Test
  void test002() {
    final Service service = DI.activateDI(ServiceImpl.class);
    Assertions.assertEquals(service.getClass() , ServiceImpl.class);
  }


}