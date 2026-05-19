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
package junit.com.svenruppert.ddi.named;

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
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 * Multiple implementations of an Interface, no decission possible
 * Created by RapidPM - Team on 07.12.2014.
 */
public class Named002Test
    extends DDIBaseTest {


  @Test()
  public void testInjection001() {
    BusinessModule businessModule = new BusinessModule();
    Assertions.assertThrows(DDIModelException.class, () -> DI.activateDI(businessModule));
  }

  public interface Service {
    String work(String txt);

    SubService getSubService();

    boolean isPostconstructed();
  }

  public static class BusinessModule {
    @Inject
    Service service;

    public String work(String txt) {
      return service.work(txt);
    }
  }

  public static class ServiceImplA
      implements Service {


    @Override
    public String work(final String txt) {
      return null;
    }

    @PostConstruct
    public void postconstruct() {
    }

    @Override
    public SubService getSubService() {
      return null;
    }


    public boolean isPostconstructed() {
      return false;
    }


  }

  public static class ServiceImplB
      implements Service {

    boolean postconstructed;

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }

    public String work(String txt) {
      return null;
    }


    @Override
    public SubService getSubService() {
      return null;
    }


    public boolean isPostconstructed() {
      return postconstructed;
    }


  }

  public static class SubService {
  }


}
