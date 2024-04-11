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
package com.svenruppert.dependencies.core.logger.factory


import java.util.logging.Level
import com.svenruppert.dependencies.core.logger.AbstractLogger
import com.svenruppert.dependencies.core.logger.LogEvent
import com.svenruppert.dependencies.core.logger.LoggerFactorySupport
import com.svenruppert.dependencies.core.logger.LoggingService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * Slf4jFactory class.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
class Slf4jFactory : LoggerFactorySupport() {

  /** {@inheritDoc}  */
  override fun createLogger(name: String): LoggingService {
    val logger = LoggerFactory.getLogger(name)
    return Slf4jLogger(logger)
  }

  internal class Slf4jLogger(private val logger: Logger) : AbstractLogger() {

    override val level: Level
      get() = when {
        logger.isTraceEnabled -> Level.FINEST
        logger.isDebugEnabled -> Level.FINE
        logger.isInfoEnabled -> Level.INFO
        logger.isWarnEnabled -> Level.WARNING
        logger.isErrorEnabled -> Level.SEVERE
        else -> Level.OFF
      }

    override fun log(level: Level, message: String) {
      when {
        level === Level.FINEST -> logger.trace(message)
        level === Level.FINER || level === Level.FINE -> logger.debug(message)
        level === Level.CONFIG || level === Level.INFO -> logger.info(message)
        level === Level.WARNING -> logger.warn(message)
        level === Level.SEVERE -> logger.error(message)
        level !== Level.OFF -> logger.info(message)
      }
    }

    override fun log(level: Level, message: String, thrown: Throwable?) {
      when {
        level === Level.FINEST -> logger.trace(message, thrown)
        level === Level.FINER || level === Level.FINE -> logger.debug(message, thrown)
        level === Level.CONFIG || level === Level.INFO -> logger.info(message, thrown)
        level === Level.WARNING -> logger.warn(message, thrown)
        level === Level.SEVERE -> logger.error(message, thrown)
        level !== Level.OFF -> logger.info(message, thrown)
      }
    }

    override fun isLoggable(level: Level): Boolean {
      return when {
        level === Level.FINEST -> logger.isTraceEnabled
        level === Level.FINER -> logger.isDebugEnabled
        level === Level.FINE -> logger.isDebugEnabled
        level === Level.CONFIG -> logger.isInfoEnabled
        level === Level.INFO -> logger.isInfoEnabled
        level === Level.WARNING -> logger.isWarnEnabled
        level === Level.SEVERE -> logger.isErrorEnabled
        else -> level !== Level.OFF && logger.isInfoEnabled
      }
    }


    override fun log(logEvent: LogEvent<*>) {
    }
  }
}
