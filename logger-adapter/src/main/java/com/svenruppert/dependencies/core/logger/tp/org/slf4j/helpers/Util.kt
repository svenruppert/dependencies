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
package com.svenruppert.dependencies.core.logger.tp.org.slf4j.helpers

/**
 * Copyright (c) 2004-2011 QOS.ch All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

/**
 * An internal utility class.
 *
 * @author Alexander Dorokhine
 * @author Ceki Glc
 */
object Util {

  private var SECURITY_MANAGER: ClassContextSecurityManager? = null
  private var SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = false

  private val securityManager: ClassContextSecurityManager?
    get() {
      if (SECURITY_MANAGER != null)
        return SECURITY_MANAGER
      else if (SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED)
        return null
      else {
        SECURITY_MANAGER = safeCreateSecurityManager()
        SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = true
        return SECURITY_MANAGER
      }
    }

  /**
   * Returns the name of the class which called the invoking method.
   *
   * @return the name of the class which called the invoking method.
   */
  // Advance until Util is found
  // trace[i] = Util; trace[i+1] = caller; trace[i+2] = caller's caller
  val callingClass: Class<*>?
    get() {
      val securityManager = securityManager ?: return null
      val trace = securityManager.classContext
      val thisClassName = Util::class.java.name
      var i: Int
      i = 0
      while (i < trace.size) {
        if (thisClassName == trace[i].name)
          break
        i++
      }
      if (i >= trace.size || i + 2 >= trace.size) {
        throw IllegalStateException(
            "Failed to find org.slf4j.helpers.Util or its caller in the stack; " + "this should not happen")
      }

      return trace[i + 2]
    }

  fun safeGetSystemProperty(key: String?): String? {
    if (key == null)
      throw IllegalArgumentException("null input")

    var result: String? = null
    try {
      result = System.getProperty(key)
    } catch (sm: java.lang.SecurityException) {
    }// ignore

    return result
  }

  fun safeGetBooleanSystemProperty(key: String): Boolean {
    val value = safeGetSystemProperty(key)
    return value?.equals("true", ignoreCase = true) ?: false
  }

  /**
   * In order to call [SecurityManager.getClassContext], which is a protected method, we add
   * this wrapper which allows the method to be visible inside this package.
   */
  private class ClassContextSecurityManager : SecurityManager() {
    public override fun getClassContext(): Array<Class<*>> {
      return super.getClassContext()
    }
  }

  private fun safeCreateSecurityManager(): ClassContextSecurityManager? {
    try {
      return ClassContextSecurityManager()
    } catch (sm: java.lang.SecurityException) {
      return null
    }

  }

  fun report(msg: String, t: Throwable) {
    System.err.println(msg)
    System.err.println("Reported exception:")
    t.printStackTrace()
  }

  fun report(msg: String) {
    System.err.println("SLF4J: $msg")
  }


}

