/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
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
package org.rapidpm.dependencies.core.logger;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * <p>LoggingService interface.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public interface LoggingService {


  /**
   * <p>finest.</p>
   *
   * @param message a {@link java.lang.String} object.
   */
  void finest(String message);

  /**
   * <p>finest.</p>
   *
   * @param thrown a {@link java.lang.Throwable} object.
   */
  void finest(Throwable thrown);

  /**
   * <p>finest.</p>
   *
   * @param message a {@link java.lang.String} object.
   * @param thrown a {@link java.lang.Throwable} object.
   */
  void finest(String message , Throwable thrown);

  /**
   * <p>isFinestEnabled.</p>
   *
   * @return a boolean.
   */
  boolean isFinestEnabled();

  /**
   * <p>fine.</p>
   *
   * @param message a {@link java.lang.String} object.
   */
  void fine(String message);

  /**
   * <p>isFineEnabled.</p>
   *
   * @return a boolean.
   */
  boolean isFineEnabled();

  /**
   * <p>info.</p>
   *
   * @param message a {@link java.lang.String} object.
   */
  void info(String message);

  /**
   * <p>warning.</p>
   *
   * @param message a {@link java.lang.String} object.
   */
  void warning(String message);

  /**
   * <p>warning.</p>
   *
   * @param thrown a {@link java.lang.Throwable} object.
   */
  void warning(Throwable thrown);

  /**
   * <p>warning.</p>
   *
   * @param message a {@link java.lang.String} object.
   * @param thrown a {@link java.lang.Throwable} object.
   */
  void warning(String message , Throwable thrown);

  /**
   * <p>severe.</p>
   *
   * @param message a {@link java.lang.String} object.
   */
  void severe(String message);

  /**
   * <p>severe.</p>
   *
   * @param thrown a {@link java.lang.Throwable} object.
   */
  void severe(Throwable thrown);

  /**
   * <p>severe.</p>
   *
   * @param message a {@link java.lang.String} object.
   * @param thrown a {@link java.lang.Throwable} object.
   */
  void severe(String message , Throwable thrown);

  /**
   * <p>log.</p>
   *
   * @param level a {@link java.util.logging.Level} object.
   * @param message a {@link java.lang.String} object.
   */
  void log(Level level , String message);

  /**
   * <p>log.</p>
   *
   * @param level a {@link java.util.logging.Level} object.
   * @param message a {@link java.lang.String} object.
   * @param thrown a {@link java.lang.Throwable} object.
   */
  void log(Level level , String message , Throwable thrown);

//  void log(LogEvent logEvent);
  /**
   * <p>log.</p>
   *
   * @param logEvent a {@link org.rapidpm.dependencies.core.logger.LogEvent} object.
   */
  default void log(LogEvent logEvent) {
    LogRecord logRecord = logEvent.getLogRecord();
    Level level = logEvent.getLogRecord().getLevel();
    String message = logRecord.getMessage();
    Throwable thrown = logRecord.getThrown();
    log(level, message, thrown);
  }

  /**
   * <p>getLevel.</p>
   *
   * @return a {@link java.util.logging.Level} object.
   */
  Level getLevel();

  /**
   * <p>isLoggable.</p>
   *
   * @param level a {@link java.util.logging.Level} object.
   * @return a boolean.
   */
  boolean isLoggable(Level level);

}
