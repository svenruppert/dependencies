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


import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import com.svenruppert.dependencies.core.logger.AbstractLogger
import com.svenruppert.dependencies.core.logger.LogEvent
import com.svenruppert.dependencies.core.logger.LoggerFactorySupport
import com.svenruppert.dependencies.core.logger.LoggingService
import java.util.logging.Level

/**
 *
 * Log4jFactory class.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
class Log4jFactory : LoggerFactorySupport(), LoggerFactory {

  /** {@inheritDoc}  */
  override fun createLogger(name: String): LoggingService {
    val l = LogManager.getLogger(name)
    return Log4jLogger(l)
  }

  internal class Log4jLogger(private val logger: Logger) : AbstractLogger() {
    override val level: Level

    init {
      val log4jLevel = logger.level
      this.level = toStandardLevel(log4jLevel)
    }

    override fun log(level: Level, message: String) {
      logger.log(toLog4jLevel(level), message)
    }

    override fun log(level: Level, message: String, thrown: Throwable?) {
      logger.log(toLog4jLevel(level), message, thrown)
    }

    override fun isLoggable(level: Level): Boolean {
      return level !== Level.OFF && logger.isEnabled(toLog4jLevel(level))
    }

    override fun log(logEvent: LogEvent<*>) {
      val logRecord = logEvent.logRecord
      if (logRecord.level === Level.OFF) {
        return
      }
      val name = logEvent.logRecord.loggerName
      val logger = LogManager.getLogger(name)
      val level = toLog4jLevel(logRecord.level)
      val message = logRecord.message
      val throwable = logRecord.thrown
      logger.log(level,message, throwable )
    }

    private fun toLog4jLevel(level: Level): org.apache.logging.log4j.Level {
      return when {
        level === Level.FINEST -> org.apache.logging.log4j.Level.TRACE
        level === Level.FINE -> org.apache.logging.log4j.Level.DEBUG
        level === Level.INFO -> org.apache.logging.log4j.Level.INFO
        level === Level.WARNING -> org.apache.logging.log4j.Level.WARN
        level === Level.SEVERE -> org.apache.logging.log4j.Level.ERROR
        level === Level.CONFIG -> org.apache.logging.log4j.Level.INFO
        level === Level.FINER -> org.apache.logging.log4j.Level.DEBUG
        level === Level.OFF -> org.apache.logging.log4j.Level.OFF
        else -> org.apache.logging.log4j.Level.INFO
      }
    }

    private fun toStandardLevel(log4jLevel: org.apache.logging.log4j.Level): Level {
      return when {
        log4jLevel === org.apache.logging.log4j.Level.TRACE -> Level.FINEST
        log4jLevel === org.apache.logging.log4j.Level.DEBUG -> Level.FINE
        log4jLevel === org.apache.logging.log4j.Level.INFO -> Level.INFO
        log4jLevel === org.apache.logging.log4j.Level.WARN -> Level.WARNING
        log4jLevel === org.apache.logging.log4j.Level.ERROR -> Level.SEVERE
        log4jLevel === org.apache.logging.log4j.Level.FATAL -> Level.SEVERE
        log4jLevel === org.apache.logging.log4j.Level.OFF -> Level.OFF
        else -> Level.INFO
      }
    }
  }
}
