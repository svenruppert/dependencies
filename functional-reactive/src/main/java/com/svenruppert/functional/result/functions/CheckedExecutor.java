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
import com.svenruppert.functional.result.Unit;

import java.util.function.Supplier;

/**
 * Executor variant of {@link CheckedSupplier} for side-effecting computations that may
 * throw any {@link Throwable}. Success is represented by {@link Unit#INSTANCE} so that
 * the no-null-success invariant of {@link Result} is preserved.
 *
 * <p>{@link Error}s propagate rather than being captured.
 */
@FunctionalInterface
public interface CheckedExecutor extends Supplier<Result<Unit, Throwable>> {

  void executeWithException() throws Throwable;

  @Override
  default Result<Unit, Throwable> get() {
    try {
      executeWithException();
      return Result.success(Unit.INSTANCE);
    } catch (Error e) {
      throw e;
    } catch (Throwable t) {
      return Result.failure(t);
    }
  }

  default Result<Unit, Throwable> execute() {
    return get();
  }
}
