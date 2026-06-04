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
package com.svenruppert.functional.matcher;

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

import com.svenruppert.functional.model.DataRecords;
import com.svenruppert.functional.model.Result;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @deprecated Use {@link com.svenruppert.functional.result.matcher.Case} which is
 * built around the modern {@code Result<T, E>} and no longer inherits from
 * {@code DataRecords.Pair}. Scheduled for removal in the next major release.
 */
@Deprecated(forRemoval = true)
public class Case<T>
    extends DataRecords.Pair<Supplier<Boolean>, Supplier<Result<T>>> {

  /**
   * <p>Constructor for Case.</p>
   *
   * @param booleanSupplier a {@link Supplier} object.
   * @param resultSupplier  a {@link Supplier} object.
   */
  public Case(final Supplier<Boolean> booleanSupplier, final Supplier<Result<T>> resultSupplier) {
    super(booleanSupplier, resultSupplier);
  }

  /**
   * <p>matchCase.</p>
   *
   * @param condition a {@link Supplier} object.
   * @param value     a {@link Supplier} object.
   * @param <T>       a T object.
   * @return a {@link Case} object.
   */
  public static <T> Case<T> matchCase(Supplier<Boolean> condition,
                                      Supplier<Result<T>> value) {
    return new Case<>(condition, value);
  }

  /**
   * <p>matchCase.</p>
   *
   * @param value a {@link Supplier} object.
   * @param <T>   a T object.
   * @return a {@link DefaultCase} object.
   */
  public static <T> DefaultCase<T> matchCase(Supplier<Result<T>> value) {
    return new DefaultCase<>(() -> true, value);
  }

  /**
   * <p>match.</p>
   *
   * @param defaultCase a {@link DefaultCase} object.
   * @param matchers    a {@link Case} object.
   * @param <T>         a T object.
   * @return a {@link Result} object.
   */
  @SafeVarargs
  public static <T> Result<T> match(DefaultCase<T> defaultCase, Case<T>... matchers) {
    return Stream
        .of(matchers)
        .filter(Case::isMatching)
        .map(Case::result)
        .findFirst()
        .orElseGet(defaultCase::result);
  }

  /**
   * <p>isMatching.</p>
   *
   * @return a boolean.
   */
  private boolean isMatching() {
    return getT1().get();
  }

  /**
   * <p>result.</p>
   *
   * @return a {@link Result} object.
   */
  public Result<T> result() {
    return getT2().get();
  }

  public static class DefaultCase<T>
      extends Case<T> {
    public DefaultCase(final Supplier<Boolean> booleanSupplier, final Supplier<Result<T>> resultSupplier) {
      super(booleanSupplier, resultSupplier);
    }
  }
}