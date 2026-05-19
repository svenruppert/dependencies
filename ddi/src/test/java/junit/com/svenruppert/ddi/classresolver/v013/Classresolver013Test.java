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
package junit.com.svenruppert.ddi.classresolver.v013;

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
import com.svenruppert.ddi.ResponsibleFor;
import com.svenruppert.ddi.implresolver.ClassResolver;
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

public class Classresolver013Test
    extends DDIBaseTest {

  @Test()
  public void test001() {
    Service service = new Service();
    Assertions.assertThrows(DDIModelException.class, () -> DI.activateDI(service));
  }

  public interface SubService {
  }

  public static class Service {
    @Inject
    SubService subService;
  }

  @ResponsibleFor(SubService.class)
  public static class SubServiceResolver
      implements ClassResolver<SubService> {

    public SubServiceResolver(String txt) {
    }

    @Override
    public Class<? extends SubService> resolve(Class<SubService> interf) {
      return SubServiceImplA.class;
    }
  }

  public static class SubServiceImplA
      implements SubService {
    public SubServiceImplA() {
      throw new RuntimeException("should never be be..");
    }
  }

  public static class SubServiceImplB
      implements SubService {
    public SubServiceImplB() {
      throw new RuntimeException("should never be be..");
    }
  }

}
