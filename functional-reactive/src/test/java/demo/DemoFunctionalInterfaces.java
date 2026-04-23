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
package demo;

import org.junit.jupiter.api.Test;

import static java.lang.System.out;

public class DemoFunctionalInterfaces {
  private static String doWorkStatic() {return null;}

  @FunctionalInterface
  interface InterfaceA {
    String doWork();
  }

  @FunctionalInterface
  interface InterfaceB {
    String doWork();

    default String doMoreWork() {
      return "useless";
    }
  }

  @FunctionalInterface
  interface InterfaceC {
    String doWork();

    default String doMoreWork() {
      return "useless";
    }
//with JDK9++
//    private String doHiddenWork() {
//      return "useless";
//    }
  }


  interface A {
    static void doStaticWork() {out.println("interface A");}
    default void doWork() {out.println("default A");}
  }

  interface B extends A {
    default void doWork() {out.println("default B");}
  }

  interface C {
    default void doWork() {out.println("default C");}
  }

  public static class ClassAB implements A , B { }
  public static class ClassAC implements A , C {
    @Override
    public void doWork() {
      out.println("default ClassAC");
    }
  }


  @Test
  void demoA() {
    new ClassAB().doWork(); //"default B"
    //ClassAB.doStaticWork()

    A.doStaticWork();

    new ClassAC().doWork(); //"default ClassAC"

  }


  public static class DemoClass{
    public void consumeInterfaceA(InterfaceA a){ }
  }

  @Test
  void demoB() {
    DemoClass demoClass = new DemoClass();

    demoClass.consumeInterfaceA(new InterfaceA() {
      @Override
      public String doWork() {
        return null;
      }
    });

    demoClass.consumeInterfaceA(() -> { return null;});
    demoClass.consumeInterfaceA(() -> null);
    //be careful !!! inheritance ??
    demoClass.consumeInterfaceA(DemoFunctionalInterfaces::doWorkStatic);



  }
}
