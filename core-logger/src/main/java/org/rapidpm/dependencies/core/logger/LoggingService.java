package org.rapidpm.dependencies.core.logger;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 */
public interface LoggingService {


  void finest(String message);

  void finest(Throwable thrown);

  void finest(String message , Throwable thrown);

  boolean isFinestEnabled();

  void fine(String message);

  boolean isFineEnabled();

  void info(String message);

  void warning(String message);

  void warning(Throwable thrown);

  void warning(String message , Throwable thrown);

  void severe(String message);

  void severe(Throwable thrown);

  void severe(String message , Throwable thrown);

  void log(Level level , String message);

  void log(Level level , String message , Throwable thrown);

//  void log(LogEvent logEvent);
  default void log(LogEvent logEvent) {
    LogRecord logRecord = logEvent.getLogRecord();
    Level level = logEvent.getLogRecord().getLevel();
    String message = logRecord.getMessage();
    Throwable thrown = logRecord.getThrown();
    log(level, message, thrown);
  }

  Level getLevel();

  boolean isLoggable(Level level);

}
