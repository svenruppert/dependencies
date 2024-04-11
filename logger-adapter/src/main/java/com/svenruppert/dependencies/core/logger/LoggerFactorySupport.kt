/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
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
package com.svenruppert.dependencies.core.logger

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import com.svenruppert.dependencies.core.logger.factory.LoggerFactory

abstract class LoggerFactorySupport : LoggerFactory {

  internal val mapLoggers: ConcurrentMap<String, LoggingService> = ConcurrentHashMap(100)

  internal val loggerConstructor = object : ConstructorFunction<String, LoggingService> {
    override fun createNew(arg: String): LoggingService {
      return createLogger(arg)
    }

  }

  /** {@inheritDoc}  */
  override fun getLogger(name: String): LoggingService {
    return ConcurrencyUtil.getOrPutIfAbsent(mapLoggers, name, loggerConstructor)
  }

  protected abstract fun createLogger(name: String): LoggingService


}
