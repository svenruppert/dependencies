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

import com.svenruppert.dependencies.core.logger.LogEvent
import com.svenruppert.dependencies.core.logger.LoggingService
import java.util.function.Supplier
import java.util.logging.Level

/**
 *
 * NoLogFactory class.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
class NoLogFactory : LoggerFactory {
  internal val noLogger: LoggingService = NoLogger()

  /** {@inheritDoc}  */
  override fun getLogger(name: String): LoggingService {
    return noLogger
  }

  internal class NoLogger : LoggingService {

    override val isFinestEnabled: Boolean
      get() = false

    override val isFineEnabled: Boolean
      get() = false

    override val isWarningEnabled: Boolean
      get() = false

    override val isSevereEnabled: Boolean
      get() = false

    override val isInfoEnabled: Boolean
      get() = false

    override val level: Level
      get() = Level.OFF

    override fun finest(message: String) {}

    override fun finest(message: String, thrown: Throwable) {}

    override fun finest(thrown: Throwable) {}

    override fun finest(message: Supplier<String>) {}

    override fun finest(message: Supplier<String>, thrown: Throwable) {}

    override fun finest(format: String, arg0: Any) {}

    override fun finest(format: String, arg1: Any, arg2: Any) {}

    override fun finest(format: String, vararg arguments: Any) {}

    override fun fine(message: String) {}

    override fun info(message: String) {}

    override fun info(message: Supplier<String>) {}

    override fun info(message: Supplier<String>, thrown: Throwable) {}

    override fun info(message: String, thrown: Throwable) {}

    override fun info(format: String, arg0: Any) {}

    override fun info(format: String, arg1: Any, arg2: Any) {}

    override fun info(format: String, vararg arguments: Any) {}

    override fun severe(message: String) {}

    override fun severe(thrown: Throwable) {}

    override fun severe(message: String, thrown: Throwable) {}

    override fun severe(message: Supplier<String>) {}

    override fun severe(message: Supplier<String>, thrown: Throwable) {}

    override fun severe(format: String, arg0: Any) {}

    override fun severe(format: String, arg1: Any, arg2: Any) {}

    override fun severe(format: String, vararg arguments: Any) {}

    override fun warning(message: String) {}

    override fun warning(thrown: Throwable) {}

    override fun warning(message: String, thrown: Throwable) {}

    override fun warning(message: Supplier<String>) {}

    override fun warning(message: Supplier<String>, thrown: Throwable) {}

    override fun warning(format: String, arg0: Any) {}

    override fun warning(format: String, arg1: Any, arg2: Any) {}

    override fun warning(format: String, vararg arguments: Any) {}

    override fun log(level: Level, message: String) {}

    override fun log(level: Level, message: String, thrown: Throwable?) {}

    override fun log(logEvent: LogEvent<*>) {}

    override fun isLoggable(level: Level): Boolean {
      return false
    }
  }
}
