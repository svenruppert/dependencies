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
package junit.com.svenruppert.ddi.reflectionmodel.v004;

import junit.com.svenruppert.ddi.reflectionmodel.v004.api.DemoAnnotation;
import junit.com.svenruppert.ddi.reflectionmodel.v004.api.Service;
import junit.com.svenruppert.ddi.reflectionmodel.v004.model001.ServiceImplA;
import junit.com.svenruppert.ddi.reflectionmodel.v004.model002.ServiceImplB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;

import java.lang.annotation.Annotation;
import java.util.Set;


public class ReflectionModel004Test {

  @Test
  public void test001_a() {
    preCheck();

    DI.activatePackages(ServiceImplA.class.getPackage().getName());

    Assertions.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
    Assertions.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class,false).isEmpty());
    Assertions.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class,true).isEmpty());
  }

  private void preCheck() {
    DI.clearReflectionModel();

    DI.activatePackages("com.svenruppert");
    DI.activatePackages(checkPkgIsNotActivated());

    Assertions.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
  }

  private String checkPkgIsNotActivated() {
    final Package aPackage = Service.class.getPackage();
    final String aPackageName = aPackage.getName();
    Assertions.assertFalse(DI.isPkgPrefixActivated(aPackageName));
    return aPackageName;
  }

  @Test
  public void test001_b() {
    preCheck();

    DI.activatePackages(ServiceImplA.class.getPackage().getName());

    Annotation annotation = () -> DemoAnnotation.class;
    Assertions.assertFalse(DI.getTypesAnnotatedWith(annotation).isEmpty());
    Assertions.assertFalse(DI.getTypesAnnotatedWith(annotation,false).isEmpty());
    Assertions.assertFalse(DI.getTypesAnnotatedWith(annotation,true).isEmpty());
  }

  @Test
  public void test002_a() {
    preCheck();

    DI.activatePackages(ServiceImplB.class.getPackage().getName());

    Assertions.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
    Assertions.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class,false).isEmpty());
    Assertions.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class,true).isEmpty());

    DI.activatePackages(ServiceImplA.class.getPackage().getName());
    Assertions.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
    final Set<Class<?>> withInherit = DI.getTypesAnnotatedWith(DemoAnnotation.class, false);
    Assertions.assertFalse(withInherit.isEmpty());
    Assertions.assertEquals(withInherit.size(), 2);

    final Set<Class<?>> withoutInherit = DI.getTypesAnnotatedWith(DemoAnnotation.class, true);
    Assertions.assertEquals(withoutInherit.size(), 1);
    Assertions.assertFalse(withoutInherit.isEmpty());
  }

  @Test
  public void test002_b() {
    preCheck();

    DI.activatePackages(ServiceImplB.class.getPackage().getName());

    Annotation annotation = () -> DemoAnnotation.class;

    Assertions.assertTrue(DI.getTypesAnnotatedWith(annotation).isEmpty());
    Assertions.assertTrue(DI.getTypesAnnotatedWith(annotation,false).isEmpty());
    Assertions.assertTrue(DI.getTypesAnnotatedWith(annotation,true).isEmpty());

    DI.activatePackages(ServiceImplA.class.getPackage().getName());
    Assertions.assertFalse(DI.getTypesAnnotatedWith(annotation).isEmpty());
    final Set<Class<?>> withInherit = DI.getTypesAnnotatedWith(annotation, false);
    Assertions.assertFalse(withInherit.isEmpty());
    Assertions.assertEquals(withInherit.size(), 2);

    final Set<Class<?>> withoutInherit = DI.getTypesAnnotatedWith(annotation, true);
    Assertions.assertEquals(withoutInherit.size(), 1);
    Assertions.assertFalse(withoutInherit.isEmpty());
  }
}