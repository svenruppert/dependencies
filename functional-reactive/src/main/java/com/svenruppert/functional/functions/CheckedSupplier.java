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
package com.svenruppert.functional.functions;

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

import com.svenruppert.functional.model.Result;

import java.util.function.Supplier;

import static com.svenruppert.functional.ExceptionFunctions.message;

/**
 * Created by svenruppert on 25.04.17.
 *
 * @deprecated Use {@link com.svenruppert.functional.result.functions.CheckedSupplier}
 * instead. The modern variant returns {@code Result<T, Throwable>} and preserves the
 * original cause and stacktrace; this legacy variant collapses any failure into a
 * String message. Scheduled for removal in the next major release.
 */
@Deprecated(forRemoval = true)
@FunctionalInterface
public interface CheckedSupplier<T>
    extends Supplier<Result<T>> {

  @Override
  default Result<T> get() {
    try {
      return Result.success(getWithException());
    } catch (Exception e) {
      return Result.failure(message().apply(e));
    }
  }

  T getWithException()
      throws Exception;

  default T getOrElse(Supplier<T> supplier) {
    return get().getOrElse(supplier);
  }
}