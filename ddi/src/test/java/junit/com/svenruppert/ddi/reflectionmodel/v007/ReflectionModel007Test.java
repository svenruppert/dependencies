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
package junit.com.svenruppert.ddi.reflectionmodel.v007;

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



import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.DIContainer;
import junit.com.svenruppert.ddi.reflectionmodel.v007.pkg.PkgServiceA;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

public class ReflectionModel007Test {


  @BeforeEach
  public void setUp() {
    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
  }


  @AfterEach
  public void tearDown() {
    DI.clearReflectionModel();
  }


  @Test
  public void test001()
      throws Exception {
    final DIContainer container = DIContainer.global();
    final Collection<String> classesForPkg = container.getClassesForPkg(PkgServiceA.class.getPackage().getName());
    Assertions.assertFalse(classesForPkg.isEmpty());
    Assertions.assertEquals(2, classesForPkg.size());


    final Set<String> activatedPkgs = container.getActivatedPkgs();
    Assertions.assertFalse(activatedPkgs.isEmpty());
    Assertions.assertEquals(2, activatedPkgs.size());

  }
}
