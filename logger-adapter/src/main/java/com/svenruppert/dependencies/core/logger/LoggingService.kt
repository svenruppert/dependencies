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

import java.util.function.Supplier
import java.util.logging.Level
import java.util.logging.LogRecord

/**
 *
 * LoggingService interface.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
interface LoggingService {

  val isFinestEnabled: Boolean

  val isFineEnabled: Boolean

  val level: Level

  val isWarningEnabled: Boolean

  val isSevereEnabled: Boolean

  val isInfoEnabled: Boolean


  fun finest(message: String)

  fun finest(thrown: Throwable)

  fun finest(message: String, thrown: Throwable)

  fun finest(message: Supplier<String>)

  fun finest(message: Supplier<String>, thrown: Throwable)

  fun finest(format: String, arg0: Any)

  fun finest(format: String, arg1: Any, arg2: Any)

  fun finest(format: String, vararg arguments: Any)

  fun fine(message: String)

  fun info(message: String)

  fun info(message: Supplier<String>)

  fun info(message: Supplier<String>, thrown: Throwable)

  fun info(message: String, thrown: Throwable)

  fun info(format: String, arg0: Any)

  fun info(format: String, arg1: Any, arg2: Any)

  fun info(format: String, vararg arguments: Any)

  fun warning(message: String)

  fun warning(thrown: Throwable)

  fun warning(message: String, thrown: Throwable)

  fun warning(message: Supplier<String>)

  fun warning(message: Supplier<String>, thrown: Throwable)

  fun warning(format: String, arg0: Any)

  fun warning(format: String, arg1: Any, arg2: Any)

  fun warning(format: String, vararg arguments: Any)

  fun severe(message: String)

  fun severe(thrown: Throwable)

  fun severe(message: Supplier<String>)

  fun severe(message: Supplier<String>, thrown: Throwable)

  fun severe(message: String, thrown: Throwable)

  fun severe(format: String, arg0: Any)

  fun severe(format: String, arg1: Any, arg2: Any)

  fun severe(format: String, vararg arguments: Any)

  fun log(level: Level, message: String)

  fun log(level: Level, message: String, thrown: Throwable?)

  fun log(logEvent: LogEvent<*>) {
    val logRecord = logEvent.logRecord
    val level = logEvent.logRecord.level
    val message = logRecord.message
    val thrown = logRecord.thrown
    log(level, message, thrown)
  }

  fun isLoggable(level: Level): Boolean

}
