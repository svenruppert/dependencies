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

public interface HasLogger {
  // Caching Map: calling class -> Logger
  ConcurrentMap<Class<?>, LoggingService> LOGGER_CACHE = new ConcurrentHashMap<>();

  static LoggingService staticLogger() {
    Class<?> callerClass = StackWalker
        .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
        .getCallerClass();
    return LOGGER_CACHE.computeIfAbsent(callerClass, Logger::getLogger);
  }

  default LoggingService logger() {
    return Logger.getLogger(getClass());
  }

}