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
package com.svenruppert.functional.model;

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

import com.svenruppert.functional.functions.CheckedFunction;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Copyright (C) 2017 RapidPM - Sven Ruppert
 * Licensed under the EUPL, Version 1.2 (the "Licence");
 * you may not use this file except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * <a href="https://joinup.ec.europa.eu/software/page/eupl">EUPL 1.2</a>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * <p>
 * Created by Sven Ruppert - RapidPM - Team on 16.03.17.
 */
public interface Result<T> {

  static <T> Result<T> failure(String errorMessage) {
    Objects.requireNonNull(errorMessage);
    return new Failure<>(errorMessage);
  }

  static <T> Result<T> success(T value) {
    return new Success<>(value);
  }

  static <T> Result<T> ofNullable(T value) {
    return ofNullable(value, "Object was null");
  }

  static <T> Result<T> ofNullable(T value, String failedMessage) {
    return (Objects.nonNull(value))
        ? success(value)
        : failure(failedMessage);
  }

  static <T> Result<T> fromOptional(Optional<T> optional) {
    Objects.requireNonNull(optional);
    return optional
        .map(Result::success)
        .orElseGet(() -> failure("Optional hold a null value"));
  }

  void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction);

  void ifPresentOrElse(Consumer<T> success, Consumer<String> failure);

  void ifPresentOrElseAsync(Consumer<? super T> action, Runnable emptyAction);

  void ifPresentOrElseAsync(Consumer<T> success, Consumer<String> failure);

  T get();

  T getOrElse(Supplier<T> supplier);

  Boolean isPresent();

  Boolean isAbsent();

  Result<T> ifPresent(Consumer<T> consumer);

  Result<T> ifAbsent(Runnable action);

  Result<T> ifFailed(Consumer<String> failed);

  default Stream<T> stream() {
    if (!isPresent()) {
      return Stream.empty();
    } else {
      return Stream.of(get());
    }
  }

  default Result<T> or(Supplier<? extends Result<? extends T>> supplier) {
    Objects.requireNonNull(supplier);
    if (isPresent()) {
      return this;
    } else {
      @SuppressWarnings("unchecked")
      Result<T> r = (Result<T>) supplier.get();
      return Objects.requireNonNull(r);
    }
  }

  default Optional<T> toOptional() {
    return Optional.ofNullable(get());
  }

  default <V, R> Result<R> thenCombine(V value, BiFunction<T, V, Result<R>> func) {
    return func.apply(get(), value);
  }

  default <V, R> Result<R> thenCombineFlat(V value, BiFunction<T, V, R> func) {
    return Result.ofNullable(func.apply(get(), value));
  }

  default <V, R> CompletableFuture<Result<R>> thenCombineAsync(V value, BiFunction<T, V, Result<R>> func) {
    return CompletableFuture.supplyAsync(() -> func.apply(get(), value));
  }

  default <U> Result<U> map(Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);
    return isPresent()
        ? ((CheckedFunction<T, U>) mapper::apply).apply(get())
        : this.asFailure();
  }

  default <U> Result<U> flatMap(Function<? super T, Result<U>> mapper) {
    Objects.requireNonNull(mapper);
    return this.isPresent()
        ? mapper.apply(get())
        : this.asFailure();
  }


  default <U> Result<U> asFailure() {
    return (isAbsent())
        ? ofNullable(null)
        : failure("converted to Failure orig was " + this);
  }


  abstract class AbstractResult<T>
      implements Result<T> {
    protected final T value;

    public AbstractResult(T value) {
      this.value = value;
    }

    @Override
    public Result<T> ifPresent(Consumer<T> consumer) {
      Objects.requireNonNull(consumer);
      if (value != null) consumer.accept(value);
      return (this instanceof Failure)
          ? this
          : ofNullable(value);
    }

    @Override
    public Result<T> ifAbsent(Runnable action) {
      Objects.requireNonNull(action);
      if (value == null) action.run();
      return (this instanceof Failure)
          ? this
          : ofNullable(value);
    }

    public Boolean isPresent() {
      return (value != null) ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean isAbsent() {
      return (value == null) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public T get() {
      return Objects.requireNonNull(value);
    }

    @Override
    public T getOrElse(Supplier<T> supplier) {
      Objects.requireNonNull(supplier);
      return (value != null) ? value : Objects.requireNonNull(supplier.get());
    }
  }

  class Success<T>
      extends AbstractResult<T> {

    public Success(T value) {
      super(value);
    }

    @Override
    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
      action.accept(value);
    }

    @Override
    public void ifPresentOrElse(final Consumer<T> success, final Consumer<String> failure) {
      // TODO check if usefull -> Objects.requireNonNull(value);
      success.accept(value);
    }

    @Override
    public void ifPresentOrElseAsync(Consumer<? super T> action, Runnable emptyAction) {
      CompletableFuture.runAsync(() -> action.accept(value));
    }

    @Override
    public void ifPresentOrElseAsync(Consumer<T> success, Consumer<String> failure) {
      CompletableFuture.runAsync(() -> success.accept(value));
    }

    public Result<T> ifFailed(Consumer<String> failed) {
      Objects.requireNonNull(failed);
      //nothing do do , I am a Success
      return ofNullable(value);
    }

  }

  class Failure<T>
      extends AbstractResult<T> {

    private final String errorMessage;

    public Failure(final String errorMessage) {
      super(null);
      this.errorMessage = errorMessage;
    }

    @Override
    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
      emptyAction.run();
    }

    @Override
    public void ifPresentOrElse(final Consumer<T> success, final Consumer<String> failure) {
      failure.accept(errorMessage);
    }

    @Override
    public void ifPresentOrElseAsync(Consumer<? super T> action, Runnable emptyAction) {
      CompletableFuture.runAsync(emptyAction);
    }

    @Override
    public void ifPresentOrElseAsync(Consumer<T> success, Consumer<String> failure) {
      CompletableFuture.runAsync(() -> failure.accept(errorMessage));
    }

    public Result<T> ifFailed(Consumer<String> failed) {
      Objects.requireNonNull(failed);
      failed.accept(errorMessage);
      // I am a Failure - hold errorMessage, value already null;
      return this;
    }
  }
}