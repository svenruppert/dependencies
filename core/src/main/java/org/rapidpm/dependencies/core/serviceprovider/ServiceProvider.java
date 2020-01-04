package org.rapidpm.dependencies.core.serviceprovider;

import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.dependencies.core.logger.Logger;
import org.rapidpm.frp.model.Result;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

public interface ServiceProvider<T>
    extends HasLogger {

  default boolean failWithException() {
    return false;
  }

  default Function<Class<T>, Result<? extends T>> loadServiceFkt() {
    return (service) -> {
      Iterator<T> iterator = ServiceLoader.load(service)
                                          .iterator();
      Iterable<T>  iterable = () -> iterator;
      final Set<T> set      = stream(iterable.spliterator(), false).collect(toSet());
      if (set.isEmpty()) {
        final String msg = "no implementation found for interface " + service.getName();
        Logger.getLogger(service)
              .warning(msg);
        if (failWithException()) throw new RuntimeException("no implementation found for interface " + service.getName());
        return Result.failure(msg);
      }

      if (set.size() > 1) {
        final String msg = "to many implementations found for interface " + service.getName();
        Logger.getLogger(service)
              .warning(msg);
        if (failWithException()) throw new RuntimeException("to many implementations found for interface " + service.getName());
        return Result.failure(msg);
      }
      return Result.success(set.iterator()
                               .next());
    };
  }

  Class<T> serviceInterface();

  default Result<? extends T> load() {
    return loadServiceFkt().apply(serviceInterface());
  }
}
