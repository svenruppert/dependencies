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

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class DI006Test
    extends DDIBaseTest {
  static boolean postconstructA1;
  static boolean postconstructA2;
  static boolean postconstructB1;
  static boolean postconstructB2;

  //  @BeforeEach
  //  public void setUp() throws Exception {
  //    DI.clearReflectionModel();
  //    DI.activatePackages(DITest006.class.getPackage().getName());
  //  }

  @Test
  public void test001() {
    Assertions.assertFalse(postconstructA1);
    Assertions.assertFalse(postconstructA2);
    Assertions.assertFalse(postconstructB1);
    Assertions.assertFalse(postconstructB2);

    final BusinessModule businessModule = new BusinessModule();
    DI.activateDI(businessModule);

    Assertions.assertTrue(postconstructA1);
    Assertions.assertTrue(postconstructA2);
    Assertions.assertTrue(postconstructB1);
    Assertions.assertTrue(postconstructB2);


  }

  public interface Service {
    String doWork(String txt);
  }

  public static class ServiceImpl
      implements Service {
    @Override
    public String doWork(final String txt) {
      return this.getClass().getSimpleName() + " " + txt;
    }

    @PostConstruct
    public void post001() {
      if (postconstructA1 == true) Assertions.fail("too bad..");
      postconstructA1 = true;
    }

    @PostConstruct
    public void post002() {
      if (postconstructA2 == true) Assertions.fail("too bad..");
      postconstructA2 = true;
    }
  }

  public static class BusinessModule {
    @Inject
    Service service;

    @PostConstruct
    public void post001() {
      if (postconstructB1 == true) Assertions.fail("too bad..");
      postconstructB1 = true;
    }

    @PostConstruct
    public void post002() {
      if (postconstructB2 == true) Assertions.fail("too bad..");
      postconstructB2 = true;
    }

  }


}
