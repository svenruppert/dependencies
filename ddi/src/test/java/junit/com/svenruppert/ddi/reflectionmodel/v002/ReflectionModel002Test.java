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
package junit.com.svenruppert.ddi.reflectionmodel.v002;

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
import junit.com.svenruppert.ddi.reflectionmodel.v002.api.Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import java.time.LocalDateTime;

public class ReflectionModel002Test {


  //  private static final Collection<URL> urls = ClasspathHelper.forClassLoader();

  @BeforeEach
  public void setUp() {
    DI.clearReflectionModel();
    DI.activatePackages("com.svenruppert");
  }

  @AfterEach
  public void tearDown() {
    DI.clearReflectionModel();
  }

  @Test
  public void test001() {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(aPackageName, ClasspathHelper.forClassLoader());
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    DI.clearReflectionModel();
    Assertions.assertFalse(DI.isPkgPrefixActivated(aPackageName));

    DI.activatePackages(aPackageName, ClasspathHelper.forClass(Service.class));
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  private String checkPkgIsNotActivated() {
    final Package aPackage = Service.class.getPackage();
    final String aPackageName = aPackage.getName();
    Assertions.assertFalse(DI.isPkgPrefixActivated(aPackageName));
    return aPackageName;
  }

  private void checkTimestamp(final String aPackageName) {
    final LocalDateTime pkgPrefixActivatedTimestamp = DI.getPkgPrefixActivatedTimestamp(aPackageName);
    Assertions.assertTrue(pkgPrefixActivatedTimestamp.isBefore(LocalDateTime.now().plusSeconds(1)));
  }

  @Test
  public void test002_a() {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(aPackageName, ClasspathHelper.forClassLoader());
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    DI.clearReflectionModel();
    Assertions.assertFalse(DI.isPkgPrefixActivated(aPackageName));

    DI.activatePackages(aPackageName, ClasspathHelper.forClass(Service.class));
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  @Test
  public void test002_b() {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(aPackageName, ClasspathHelper.forClassLoader());
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  @Test
  public void test003_a() {
    final String aPackageName = checkPkgIsNotActivated();


    DI.activatePackages(aPackageName);
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  @Test
  public void test003_b() {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(aPackageName);
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);


  }
}
