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
import java.util.logging.LogRecord

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.spi.ExtendedLogger
import com.svenruppert.dependencies.core.logger.AbstractLogger
import com.svenruppert.dependencies.core.logger.LogEvent
import com.svenruppert.dependencies.core.logger.LoggerFactorySupport
import com.svenruppert.dependencies.core.logger.LoggingService

/**
 * Logging to Log4j 2.x.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
class Log4j2Factory : LoggerFactorySupport() {

  /** {@inheritDoc}  */
  override fun createLogger(name: String): LoggingService {
    return Log4j2Logger(LogManager.getContext().getLogger(name))
  }

  internal class Log4j2Logger(private val logger: ExtendedLogger) : AbstractLogger() {

    override val level: Level
      get() = when {
        logger.isTraceEnabled -> Level.FINEST
        logger.isDebugEnabled -> Level.FINE
        logger.isInfoEnabled -> Level.INFO
        logger.isWarnEnabled -> Level.WARNING
        logger.isErrorEnabled -> Level.SEVERE
        logger.isFatalEnabled -> Level.SEVERE
        else -> Level.OFF
      }

    override fun log(logEvent: LogEvent<*>) {
      val logRecord = logEvent.logRecord
      val level = logEvent.logRecord.level
      val message = logRecord.message
      val thrown = logRecord.thrown
      log(level, message, thrown)
    }

    override fun log(level: Level, message: String) {
      logger.logIfEnabled(FQCN, toLog4j2Level(level), null, message)
    }

    override fun log(level: Level, message: String, thrown: Throwable?) {
      logger.logIfEnabled(FQCN, toLog4j2Level(level), null, message, thrown)
    }

    override fun isLoggable(level: Level): Boolean {
      return level !== Level.OFF && logger.isEnabled(toLog4j2Level(level), null)
    }

    private fun toLog4j2Level(level: Level): org.apache.logging.log4j.Level {
      return when {
        level === Level.FINEST -> org.apache.logging.log4j.Level.TRACE
        level === Level.FINE -> org.apache.logging.log4j.Level.DEBUG
        level === Level.INFO -> org.apache.logging.log4j.Level.INFO
        level === Level.WARNING -> org.apache.logging.log4j.Level.WARN
        level === Level.SEVERE -> org.apache.logging.log4j.Level.ERROR
        level === Level.FINER -> org.apache.logging.log4j.Level.DEBUG
        level === Level.CONFIG -> org.apache.logging.log4j.Level.INFO
        level === Level.OFF -> org.apache.logging.log4j.Level.OFF
        else -> org.apache.logging.log4j.Level.INFO
      }
    }
  }

  companion object {
    private val FQCN = Log4j2Logger::class.java.name
  }
}
