/*
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.svenruppert.dependencies.core.logger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface HasLogger {
  // Cache für Klassen-Logger
  Map<Class<?>, Logger> LOGGER_CACHE = new ConcurrentHashMap<>();

  static Logger staticLogger() {
    Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
        .getCallerClass();
    return LOGGER_CACHE.computeIfAbsent(callerClass, LoggerFactory::getLogger);
  }

  default Logger logger() {
    return LoggerFactory.getLogger(getClass());
  }

  default Logger logger(Class<?> clazz) {
    return LoggerFactory.getLogger(clazz);
  }


}
