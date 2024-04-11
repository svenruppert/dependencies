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

import com.svenruppert.dependencies.core.logger.tp.org.slf4j.helpers.MessageFormatter
import java.util.function.Supplier
import java.util.logging.Level


/**
 *
 * Abstract AbstractLogger class.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
abstract class AbstractLogger : LoggingService {

  /** {@inheritDoc}  */
  override val isFinestEnabled: Boolean
    get() = isLoggable(Level.FINEST)

  /** {@inheritDoc}  */
  override val isFineEnabled: Boolean
    get() = isLoggable(Level.FINE)

  override val isInfoEnabled: Boolean
    get() = isLoggable(Level.INFO)

  override val isSevereEnabled: Boolean
    get() = isLoggable(Level.SEVERE)

  override val isWarningEnabled: Boolean
    get() = isLoggable(Level.WARNING)

  /** {@inheritDoc}  */
  override fun finest(message: String) {
    log(Level.FINEST, message)
  }

  /** {@inheritDoc}  */
  override fun finest(message: String, thrown: Throwable) {
    log(Level.FINEST, message, thrown)
  }

  /** {@inheritDoc}  */
  override fun finest(thrown: Throwable) {
    log(Level.FINEST, thrown.message ?: "no message ", thrown)
  }

  override fun finest(message: Supplier<String>) {
    if (isFinestEnabled) {
      finest(message.get())
    }
  }

  override fun finest(message: Supplier<String>, thrown: Throwable) {
    if (isFinestEnabled) {
      finest(message.get(), thrown)
    }
  }

  override fun finest(format: String, arg0: Any) {
    finest(format, arg0, "")
  }

  override fun finest(format: String, arg1: Any, arg2: Any) {
    if (isFinestEnabled) {
      val tp = MessageFormatter.format(format, arg1, arg2)
      finest(tp.message!!, tp.throwable!!)
    }
  }

  override fun finest(format: String, vararg arguments: Any) {
    if (isFinestEnabled) {
      val tp = MessageFormatter.arrayFormat(format, arguments)
      finest(tp.message!!, tp.throwable!!)
    }
  }

  /** {@inheritDoc}  */
  override fun fine(message: String) {
    log(Level.FINE, message)
  }

  /** {@inheritDoc}  */
  override fun info(message: String) {
    log(Level.INFO, message)
  }

  override fun info(message: String, thrown: Throwable) {
    log(Level.INFO, message, thrown)
  }

  override fun info(message: Supplier<String>) {
    if (isInfoEnabled) {
      info(message.get())
    }
  }

  override fun info(message: Supplier<String>, thrown: Throwable) {
    if (isInfoEnabled) {
      info(message.get(), thrown)
    }
  }

  override fun info(format: String, arg0: Any) {
    info(format, arg0, "")
  }

  override fun info(format: String, arg1: Any, arg2: Any) {
    if (isInfoEnabled) {
      val tp = MessageFormatter.format(format, arg1, arg2)
      info(tp.message!!, tp.throwable!!)
    }
  }

  override fun info(format: String, vararg arguments: Any) {
    if (isInfoEnabled) {
      val tp = MessageFormatter.arrayFormat(format, arguments)
      info(tp.message!!, tp.throwable!!)
    }
  }

  /** {@inheritDoc}  */
  override fun severe(message: String) {
    log(Level.SEVERE, message)
  }

  /** {@inheritDoc}  */
  override fun severe(thrown: Throwable) {
    log(Level.SEVERE, thrown.message ?: "no message ", thrown)
  }

  /** {@inheritDoc}  */
  override fun severe(message: String, thrown: Throwable) {
    log(Level.SEVERE, message, thrown)
  }

  override fun severe(message: Supplier<String>) {
    if (isSevereEnabled) {
      severe(message.get())
    }
  }

  override fun severe(message: Supplier<String>, thrown: Throwable) {
    if (isSevereEnabled) {
      severe(message.get(), thrown)
    }
  }

  override fun severe(format: String, arg0: Any) {
    severe(format, arg0, "")
  }

  override fun severe(format: String, arg1: Any, arg2: Any) {
    if (isSevereEnabled) {
      val tp = MessageFormatter.format(format, arg1, arg2)
      severe(tp.message!!, tp.throwable!!)
    }
  }

  override fun severe(format: String, vararg arguments: Any) {
    if (isSevereEnabled) {
      val tp = MessageFormatter.arrayFormat(format, arguments)
      severe(tp.message!!, tp.throwable!!)
    }
  }

  /** {@inheritDoc}  */
  override fun warning(message: String) {
    log(Level.WARNING, message)
  }

  /** {@inheritDoc}  */
  override fun warning(thrown: Throwable) {
    log(Level.WARNING, thrown.message ?: "no message ", thrown)
  }

  /** {@inheritDoc}  */
  override fun warning(message: String, thrown: Throwable) {
    log(Level.WARNING, message, thrown)
  }

  override fun warning(format: String, arg0: Any) {
    warning(format, arg0, "")
  }

  override fun warning(message: Supplier<String>) {
    if (isWarningEnabled) {
      warning(message.get())
    }
  }

  override fun warning(message: Supplier<String>, thrown: Throwable) {
    if (isWarningEnabled) {
      warning(message.get(), thrown)
    }
  }

  override fun warning(format: String, arg1: Any, arg2: Any) {
    if (!isWarningEnabled) {
      return
    }
    val tp = MessageFormatter.format(format, arg1, arg2)
    warning(tp.message!!, tp.throwable!!)
  }

  override fun warning(format: String, vararg arguments: Any) {
    if (!isWarningEnabled) {
      return
    }
    val tp = MessageFormatter.arrayFormat(format, arguments)
    warning(tp.message!!, tp.throwable!!)
  }
}
