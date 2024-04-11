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
import java.util.logging.Logger
import com.svenruppert.dependencies.core.logger.AbstractLogger
import com.svenruppert.dependencies.core.logger.LogEvent
import com.svenruppert.dependencies.core.logger.LoggerFactorySupport
import com.svenruppert.dependencies.core.logger.LoggingService

/**
 *
 * StandardLoggerFactory class.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
class StandardLoggerFactory : LoggerFactorySupport(), LoggerFactory {

  /** {@inheritDoc}  */
  override fun createLogger(name: String): LoggingService {
    val l = Logger.getLogger(name)
    return StandardLogger(l)
  }

  internal class StandardLogger(private val logger: Logger) : AbstractLogger() {
    override fun log(level: Level, message: String) {
      log(level, message, null)
    }

    override val level: Level
      get() = logger.level

    override fun log(level: Level, message: String, thrown: Throwable?) {
      val logRecord = LogRecord(level, message)
      logRecord.loggerName = logger.name
      logRecord.thrown = thrown
      logRecord.sourceClassName = logger.name
      logger.log(logRecord)
    }

    override fun log(logEvent: LogEvent<*>) {
      logger.log(logEvent.logRecord)
    }

    override fun isLoggable(level: Level): Boolean {
      return logger.isLoggable(level)
    }
  }
}
