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
package com.svenruppert.dependencies.core.logger;

/*-
 * #%L
 * SRU - Core
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




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface HasLogger {
  // Cache für Klassen-Logger mit ClassValue (verhindert Memory Leaks und ist optimiert)
  ClassValue<Logger> LOGGER_CACHE = new ClassValue<>() {
    @Override
    protected Logger computeValue(Class<?> type) {
      return LoggerFactory.getLogger(type);
    }
  };

  // Wiederverwendbare, thread-safe StackWalker-Instanz
  StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  static Logger staticLogger() {
    Class<?> callerClass = WALKER.getCallerClass();
    return LOGGER_CACHE.get(callerClass);
  }

  default Logger logger() {
    return LOGGER_CACHE.get(getClass());
  }

  default Logger logger(Class<?> clazz) {
    return LOGGER_CACHE.get(clazz);
  }
}
