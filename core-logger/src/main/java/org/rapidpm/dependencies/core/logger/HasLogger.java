package org.rapidpm.dependencies.core.logger;

/**
 *
 */
public interface HasLogger {
  default LoggingService logger() {
    return Logger.getLogger(getClass());
  }
}
