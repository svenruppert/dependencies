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
/*
  Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)

  Licensed under the EUPL, Version 1.2 (the "Licence");
   * you may not use this file except in compliance with the Licence.
   * You may obtain a copy of the Licence at:
   * https://joinup.ec.europa.eu/software/page/eupl
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the Licence is distributed on an "AS IS" basis,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the Licence for the specific language governing permissions and
   * limitations under the Licence.
 */
package junit.com.svenruppert.ddi.bootstrap.test001;

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
import junit.com.svenruppert.ddi.bootstrap.test001.api.Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Created by benjamin-bosch on 05.04.17.
 */
public class BootstrapTest {

  @BeforeEach
  public void setUp() {
    DI.clearReflectionModel();
    System.clearProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE);
  }


  @AfterEach
  public void tearDown() {
    DI.clearReflectionModel();
    System.clearProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE);
  }

  @Test()
  public void testInjection001() {

    DI.activatePackages(BootstrapTest.class);
    DI.bootstrap();
    Assertions.assertThrows(DDIModelException.class, () -> DI.activateDI(Service.class));

  }

  @Test
  public void test002() {
    String path = this.getClass().getResource("good.packages").getPath();
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE, path);
    DI.bootstrap();
    Service service = DI.activateDI(Service.class);
    Assertions.assertEquals("done", service.doWork());
  }

  @Test()
  public void test003() {
    String path = this.getClass().getResource("bad.packages").getPath();
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE, path);
    DI.bootstrap();
    final Service service = DI.activateDI(Service.class);
    Assertions.assertThrows(RuntimeException.class, service::doWork);
  }

  @Test()
  public void test004() {
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE, "");
    DI.bootstrap();
    Assertions.assertThrows(DDIModelException.class, () -> DI.activateDI(Service.class));

  }

  @Test()
  public void test005() {
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE, "NO_FILE");
    Assertions.assertThrows(DDIModelException.class, DI::bootstrap);
  }

  @Test
  public void test006() {
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE, BootstrapTest.class.getPackage().getName().replace(".", "/") + "/good.packages");
    DI.bootstrap();
    Service service = DI.activateDI(Service.class);
    Assertions.assertEquals("done", service.doWork());
  }
}