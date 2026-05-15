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
package junit.com.svenruppert.ddi.scopes.v003;

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
import com.svenruppert.ddi.scopes.InjectionScope;
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class Scopes003Test
    extends DDIBaseTest {

  @Test
  public void test001() {
    final Set<String> scopes = DI.listAllActiveScopes();
    Assertions.assertNotNull(scopes);
    Assertions.assertFalse(scopes.isEmpty());
    Assertions.assertTrue(scopes.contains(TestScopeB.class.getSimpleName()));
    Assertions.assertFalse(scopes.contains(TestScopeA.class.getSimpleName()));
  }


  public interface Service {
  }


  public static class SingletonTestClass
      implements Service {
  }

  public static class TestScopeA
      extends InjectionScope {

    //no default constructor
    public TestScopeA(String txt) {
    }

    @Override
    public <T> T getInstance(final String clazz) {
      return null;
    }

    @Override
    public <T> void storeInstance(final Class<T> targetClassOrInterface, final T instance) {

    }

    @Override
    public void clear() {

    }

    @Override
    public String getScopeName() {
      return TestScopeA.class.getSimpleName();
    }
  }

  public static class TestScopeB
      extends InjectionScope {

    @Override
    public <T> T getInstance(final String clazz) {
      return null;
    }

    @Override
    public <T> void storeInstance(final Class<T> targetClassOrInterface, final T instance) {

    }

    @Override
    public void clear() {

    }

    @Override
    public String getScopeName() {
      return TestScopeB.class.getSimpleName();
    }
  }


}
