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
package com.svenruppert.functional.result.functions;

/*-
 * #%L
 * SRU - Functional
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2026 Sven Ruppert
 * %%
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
 * #L%
 */

import com.svenruppert.functional.result.Result;

import java.util.function.Supplier;

/**
 * Supplier variant that may throw any {@link Throwable}; result is captured as
 * {@code Result<T, Throwable>} preserving the original cause and stacktrace.
 *
 * <p>{@link Error}s propagate rather than being captured.
 */
@FunctionalInterface
public interface CheckedSupplier<T> extends Supplier<Result<T, Throwable>> {

  T getWithException() throws Throwable;

  @Override
  default Result<T, Throwable> get() {
    try {
      return Result.success(getWithException());
    } catch (Error e) {
      throw e;
    } catch (Throwable t) {
      return Result.failure(t);
    }
  }
}
