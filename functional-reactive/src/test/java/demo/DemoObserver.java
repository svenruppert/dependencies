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

/*-
 * #%L
 * SRU - Functional
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

import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static java.lang.System.out;

public class DemoObserver {


  @Test
  void test001() {

    class Observer<KEY, VALUE> {
      private ConcurrentHashMap<KEY, Consumer<VALUE>> listener
          = new ConcurrentHashMap<>();

      public void register(KEY key,
                           Consumer<VALUE> consumer) {
        listener.put(key, consumer);
      }

      public void remove(KEY key) {
        listener.remove(key);
      }

      public void senEvent(VALUE value) {
        listener.values()
            .forEach(c -> c.accept(value));
      }
    }


    Observer<String, Integer> observer = new Observer<>();

    observer.register("key1", out::println);
    observer.senEvent(2);
    observer.remove("key1");
    observer.senEvent(2);


  }
}