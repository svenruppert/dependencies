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
package com.svenruppert.functional.result;

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

import java.util.Objects;

/**
 * Convenience factories for {@code Result<T, Throwable>}. Catches checked and unchecked
 * exceptions thrown by the supplier and represents them as a {@link Result.Failure},
 * keeping cause and stacktrace intact.
 *
 * <p>{@link Error}s are not caught — they propagate through, as they indicate JVM-level
 * faults that callers should not normally recover from.
 */
public final class Try {

  private Try() {
  }

  public static <T> Result<T, Throwable> of(ThrowingSupplier<T> supplier) {
    Objects.requireNonNull(supplier);
    try {
      return Result.success(supplier.get());
    } catch (Error e) {
      throw e;
    } catch (Throwable t) {
      return Result.failure(t);
    }
  }
}
