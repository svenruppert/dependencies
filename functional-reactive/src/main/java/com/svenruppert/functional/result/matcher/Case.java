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
package com.svenruppert.functional.result.matcher;

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

import com.svenruppert.functional.result.Result;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Lazy pattern-matching helper over the {@link Result Result&lt;T, E&gt;} type.
 *
 * <p>{@link #match(DefaultCase, Case[])} evaluates case conditions in order and returns
 * the first matching case's result. Conditions and result suppliers beyond the first
 * match are never invoked. The default case is invoked only when no case matches.
 */
public class Case<T, E> {

  final Supplier<Boolean> condition;
  final Supplier<Result<T, E>> result;

  Case(Supplier<Boolean> condition, Supplier<Result<T, E>> result) {
    this.condition = Objects.requireNonNull(condition, "condition supplier must not be null");
    this.result = Objects.requireNonNull(result, "result supplier must not be null");
  }

  public static <T, E> Case<T, E> matchCase(Supplier<Boolean> condition,
                                            Supplier<Result<T, E>> value) {
    return new Case<>(condition, value);
  }

  public static <T, E> DefaultCase<T, E> matchCase(Supplier<Result<T, E>> value) {
    return new DefaultCase<>(value);
  }

  @SafeVarargs
  public static <T, E> Result<T, E> match(DefaultCase<T, E> defaultCase,
                                          Case<T, E>... matchers) {
    Objects.requireNonNull(defaultCase, "default case must not be null");
    Objects.requireNonNull(matchers, "matchers array must not be null");
    for (Case<T, E> c : matchers) {
      Objects.requireNonNull(c, "individual case must not be null");
      Boolean match = c.condition.get();
      if (Boolean.TRUE.equals(match)) {
        return Objects.requireNonNull(c.result.get(),
            "case result supplier must not return null");
      }
    }
    return Objects.requireNonNull(defaultCase.result.get(),
        "default case result supplier must not return null");
  }

  public static final class DefaultCase<T, E> extends Case<T, E> {
    DefaultCase(Supplier<Result<T, E>> value) {
      super(() -> Boolean.TRUE, value);
    }
  }
}
