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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Disjoint union of a success value of type {@code T} or a failure value of type {@code E}.
 * Empty and failure are intentionally distinct: a {@link Success} must hold a non-null value;
 * use {@link #ofNullable(Object, Object)} to lift a possibly-null reference into a {@code Result}.
 */
public sealed interface Result<T, E> permits Result.Success, Result.Failure {

  static <T, E> Result<T, E> success(T value) {
    return new Success<>(value);
  }

  static <T, E> Result<T, E> failure(E error) {
    return new Failure<>(error);
  }

  static <T, E> Result<T, E> ofNullable(T value, E errorIfNull) {
    return value != null ? success(value) : failure(errorIfNull);
  }

  static <T, E> Result<T, E> ofNullable(T value, Supplier<? extends E> errorIfNull) {
    Objects.requireNonNull(errorIfNull);
    return value != null ? success(value) : failure(errorIfNull.get());
  }

  record Success<T, E>(T value) implements Result<T, E> {
    public Success {
      Objects.requireNonNull(value, "Success value must not be null; use ofNullable to lift possibly-null references");
    }

    @Override
    public boolean isSuccess() {
      return true;
    }

    @Override
    public boolean isFailure() {
      return false;
    }
  }

  record Failure<T, E>(E error) implements Result<T, E> {
    public Failure {
      Objects.requireNonNull(error, "Failure error must not be null");
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public boolean isFailure() {
      return true;
    }
  }

  boolean isSuccess();

  boolean isFailure();

  default T getOrThrow() {
    return switch (this) {
      case Success<T, E>(T value) -> value;
      case Failure<T, E>(E error) ->
          throw new NoSuchElementException("Result is a failure: " + error);
    };
  }

  default T getOrElse(T fallback) {
    return switch (this) {
      case Success<T, E>(T value) -> value;
      case Failure<T, E> __ -> fallback;
    };
  }

  default T getOrElse(Function<? super E, ? extends T> fromError) {
    Objects.requireNonNull(fromError);
    return switch (this) {
      case Success<T, E>(T value) -> value;
      case Failure<T, E>(E error) -> fromError.apply(error);
    };
  }

  default <R> R fold(Function<? super T, ? extends R> onSuccess,
                     Function<? super E, ? extends R> onFailure) {
    Objects.requireNonNull(onSuccess);
    Objects.requireNonNull(onFailure);
    return switch (this) {
      case Success<T, E>(T value) -> onSuccess.apply(value);
      case Failure<T, E>(E error) -> onFailure.apply(error);
    };
  }

  default Result<T, E> peek(Consumer<? super T> action) {
    Objects.requireNonNull(action);
    if (this instanceof Success<T, E>(T value)) {
      action.accept(value);
    }
    return this;
  }

  default Result<T, E> peekFailure(Consumer<? super E> action) {
    Objects.requireNonNull(action);
    if (this instanceof Failure<T, E>(E error)) {
      action.accept(error);
    }
    return this;
  }

  default Result<T, E> recover(Function<? super E, ? extends T> recovery) {
    Objects.requireNonNull(recovery);
    return switch (this) {
      case Success<T, E> s -> s;
      case Failure<T, E>(E error) -> success(recovery.apply(error));
    };
  }

  default Result<T, E> recoverWith(Function<? super E, ? extends Result<T, E>> recovery) {
    Objects.requireNonNull(recovery);
    return switch (this) {
      case Success<T, E> s -> s;
      case Failure<T, E>(E error) -> Objects.requireNonNull(recovery.apply(error),
          "recoverWith function must not return null");
    };
  }

  default <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);
    return switch (this) {
      case Success<T, E>(T value) -> success(mapper.apply(value));
      case Failure<T, E>(E error) -> failure(error);
    };
  }

  default <U> Result<U, E> flatMap(Function<? super T, ? extends Result<U, E>> mapper) {
    Objects.requireNonNull(mapper);
    return switch (this) {
      case Success<T, E>(T value) -> Objects.requireNonNull(mapper.apply(value),
          "flatMap function must not return null");
      case Failure<T, E>(E error) -> failure(error);
    };
  }

  default <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper) {
    Objects.requireNonNull(mapper);
    return switch (this) {
      case Success<T, E>(T value) -> success(value);
      case Failure<T, E>(E error) -> failure(mapper.apply(error));
    };
  }

  default Optional<T> toOptional() {
    return switch (this) {
      case Success<T, E>(T value) -> Optional.of(value);
      case Failure<T, E> __ -> Optional.empty();
    };
  }

  default Stream<T> stream() {
    return switch (this) {
      case Success<T, E>(T value) -> Stream.of(value);
      case Failure<T, E> __ -> Stream.empty();
    };
  }

  /**
   * Async equivalent of {@link #map(Function)}. The mapper runs on the supplied {@link Executor};
   * failures short-circuit without scheduling work.
   */
  default <U> CompletableFuture<Result<U, E>> mapAsync(Function<? super T, ? extends U> mapper,
                                                      Executor executor) {
    Objects.requireNonNull(mapper);
    Objects.requireNonNull(executor);
    return switch (this) {
      case Success<T, E>(T value) ->
          CompletableFuture.supplyAsync(() -> Result.<U, E>success(mapper.apply(value)), executor);
      case Failure<T, E>(E error) ->
          CompletableFuture.completedFuture(Result.failure(error));
    };
  }

  default <U> CompletableFuture<Result<U, E>> mapAsync(Function<? super T, ? extends U> mapper) {
    return mapAsync(mapper, ForkJoinPool.commonPool());
  }

  /**
   * Async equivalent of {@link #flatMap(Function)}. The mapper itself is invoked on the
   * supplied {@link Executor}; its returned future is then awaited. Failures short-circuit.
   */
  default <U> CompletableFuture<Result<U, E>> flatMapAsync(
      Function<? super T, ? extends CompletableFuture<Result<U, E>>> mapper,
      Executor executor) {
    Objects.requireNonNull(mapper);
    Objects.requireNonNull(executor);
    return switch (this) {
      case Success<T, E>(T value) -> CompletableFuture
          .supplyAsync(() -> mapper.apply(value), executor)
          .thenCompose(Function.identity());
      case Failure<T, E>(E error) ->
          CompletableFuture.completedFuture(Result.failure(error));
    };
  }

  default <U> CompletableFuture<Result<U, E>> flatMapAsync(
      Function<? super T, ? extends CompletableFuture<Result<U, E>>> mapper) {
    return flatMapAsync(mapper, ForkJoinPool.commonPool());
  }
}
