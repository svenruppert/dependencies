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

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

import com.svenruppert.dependencies.core.logger.factory.LoggerFactory
import com.svenruppert.dependencies.core.logger.factory.NoLogFactory
import com.svenruppert.dependencies.core.logger.factory.StandardLoggerFactory

/**
 *
 * Logger class.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
object Logger {

  /** Constant `RAPIDPM_LOGGING_TYPE="rapidpm.logging.type"`  */
  val RAPIDPM_LOGGING_TYPE = "rapidpm.logging.type"
  /** Constant `RAPIDPM_LOGGING_CLASS="rapidpm.logging.class"`  */
  val RAPIDPM_LOGGING_CLASS = "rapidpm.logging.class"

  @Volatile
  private var loggerFactory: LoggerFactory? = null
  private val FACTORY_LOCK = Any()

  /**
   *
   * getLogger.
   *
   * @param clazz a [Class] object.
   * @return a [com.svenruppert.dependencies.core.logger.LoggingService] object.
   */
  @JvmStatic
  fun getLogger(clazz: Class<*>): LoggingService {
    return getLogger(clazz.name)
  }

  /**
   *
   * getLogger.
   *
   * @param name a [String] object.
   * @return a [com.svenruppert.dependencies.core.logger.LoggingService] object.
   */
  @JvmStatic
  fun getLogger(name: String): LoggingService {

    when (loggerFactory) {
      null -> synchronized(FACTORY_LOCK) {
        if (loggerFactory == null) {
          val loggerType = System.getProperty(RAPIDPM_LOGGING_TYPE)
          loggerFactory = newLoggerFactory(loggerType)
        }
      }
    }
    return loggerFactory!!.getLogger(name)
  }

  /**
   *
   * newLoggerFactory.
   *
   * @param loggerType a [String] object.
   * @return a [com.svenruppert.dependencies.core.logger.factory.LoggerFactory] object.
   */
  fun newLoggerFactory(loggerType: String?): LoggerFactory {
    var loggerFactory: LoggerFactory? = null
    val loggerClass = System.getProperty(RAPIDPM_LOGGING_CLASS)
    if (loggerClass != null) {
      loggerFactory = loadLoggerFactory(loggerClass)
    }

    if (loggerFactory == null && loggerType != null) {
      when (loggerType) {
        "log4j" -> loggerFactory = loadLoggerFactory("com.svenruppert.dependencies.core.logger.factory.Log4jFactory")
        "log4j2" -> loggerFactory = loadLoggerFactory("com.svenruppert.dependencies.core.logger.factory.Log4j2Factory")
        "slf4j" -> loggerFactory = loadLoggerFactory("com.svenruppert.dependencies.core.logger.factory.Slf4jFactory")
        "jdk" -> loggerFactory = StandardLoggerFactory()
        "none" -> loggerFactory = NoLogFactory()
      }
    }

    if (loggerFactory == null) {
      loggerFactory = StandardLoggerFactory()
    }
    return loggerFactory
  }

  private fun loadLoggerFactory(className: String): LoggerFactory? {
    try {
      val forName = Class.forName(className)
      val declaredConstructor = forName.getDeclaredConstructor()
      return declaredConstructor.newInstance() as LoggerFactory
    } catch (e: ClassNotFoundException) {
      e.printStackTrace()
      return null
    } catch (e: InstantiationException) {
      e.printStackTrace()
      return null
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
      return null
    } catch (e: InvocationTargetException) {
      e.printStackTrace()
      return null
    } catch (e: NoSuchMethodException) {
      e.printStackTrace()
      return null
    }

  }
}
