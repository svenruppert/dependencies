/*
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the EUPL, Version 1.2 (the "Licence");
 * you may not use this file except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package com.svenruppert.functional;

/*-
 * #%L
 * SRU - Functional
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2026 Sven Ruppert
 * %%
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * #L%
 */

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * <p>ExceptionFunctions interface.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public interface ExceptionFunctions {

  /**
   * <p>message.</p>
   *
   * @return a {@link Function} object.
   */
  static Function<Exception, String> message() {
    return (e) -> {
      Objects.requireNonNull(e, "Exception instance was null.");
      final String message = e.getMessage();
      final String simpleName = e.getClass().getSimpleName();
      return (message != null ? simpleName + " - " + message : simpleName + " - no message");
    };
  }

  /**
   * <p>toStackTraceStream.</p>
   *
   * @return a {@link Function} object.
   */
  static Function<Exception, Stream<StackTraceElement>> toStackTraceStream() {
    return (e) -> {
      final StackTraceElement[] stackTrace = e.getStackTrace();
      return Arrays.stream(stackTrace);
      //      return (stackTrace != null)
      //          ? Arrays.stream(stackTrace)
      //          : Stream.empty();
    };
  }
}