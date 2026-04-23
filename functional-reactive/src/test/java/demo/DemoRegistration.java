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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static java.lang.System.out;

public class DemoRegistration {

  interface Registration {
    void remove();
  }

  @Test
  void test001() {

    class Observer<KEY, VALUE> {
      private Set<Consumer<VALUE>> listener
          = ConcurrentHashMap.newKeySet();

      public Registration register(Consumer<VALUE> consumer) {
        listener.add(consumer);
        return () -> listener.remove(consumer);
      }

      public void senEvent(VALUE value) {
        listener.forEach(c -> c.accept(value));
      }
    }


    Observer<String, Integer> observer = new Observer<>();

    Registration reg = observer.register(out::println);
    observer.senEvent(2);
    reg.remove();
    observer.senEvent(2);


  }
}
