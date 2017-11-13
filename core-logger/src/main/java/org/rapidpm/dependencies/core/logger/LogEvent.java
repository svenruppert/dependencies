package org.rapidpm.dependencies.core.logger;

import java.util.EventObject;
import java.util.logging.LogRecord;

/**
 *
 */
public class LogEvent<T> extends EventObject {
  final LogRecord logRecord;
  final T member;

  public LogEvent(LogRecord logRecord , T member) {
    super(member);
    this.logRecord = logRecord;
    this.member = member;
  }

  public T getMember() {
    return member;
  }

  public LogRecord getLogRecord() {
    return logRecord;
  }
}
