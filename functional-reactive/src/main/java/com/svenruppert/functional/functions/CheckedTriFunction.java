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

import com.svenruppert.functional.model.Result;

import static com.svenruppert.functional.ExceptionFunctions.message;

public interface CheckedTriFunction<T1, T2, T3, R> extends TriFunction<T1, T2, T3, Result<R>> {
  @Override
  default Result<R> apply(T1 t1, T2 t2, T3 t3) {
    try {
      return Result.success(applyWithException(t1, t2, t3));
    } catch (Exception e) {
      return Result.failure(message().apply(e));
    }
  }

  R applyWithException(T1 t1, T2 t2, T3 t3) throws Exception;
}
