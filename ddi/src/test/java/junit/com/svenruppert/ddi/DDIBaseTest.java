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
package junit.com.svenruppert.ddi;

import com.svenruppert.dependencies.core.logger.HasLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import com.svenruppert.ddi.DI;

public class DDIBaseTest implements HasLogger {


  @BeforeEach
  public void setUpDDI() {
    DI.clearReflectionModel();
    DI.activatePackages("com.svenruppert");
    final String name = this.getClass().getPackage().getName();
    logger().info("Activate packages: " + name);
    DI.activatePackages(name);
    DI.activateDI(this);
  }

  @AfterEach
  public void tearDownDDI() {
    DI.clearReflectionModel();
  }
}