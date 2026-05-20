# SRU — Functional Reactive

Project page: **[frp.svenruppert.com](https://frp.svenruppert.com)**

Nano functional toolkit for core Java: a sealed `Result<T, E>` with records, `Try` exception capture, checked function interfaces, record-based tuples, a thread-safe `Memoizer`, a small `Case`-pattern helper, `StringFunctions`, and a `CompletableFutureQueue` for ordered async pipelines. The legacy `com.svenruppert.functional.model.Result<T>` stays available behind a bridge so older callers keep working.

YouTube series (background, design choices, walk-throughs):

* (EN): [https://youtu.be/jMP9r5_bi5c](https://youtu.be/jMP9r5_bi5c)
* (DE): [https://youtu.be/S5ysVvritIg](https://youtu.be/S5ysVvritIg)

## Licence and module

Licence: EUPL 1.2. Part of the multi-module build `com.svenruppert:dependencies`.

```xml
<dependency>
    <groupId>com.svenruppert</groupId>
    <artifactId>functional-reactive</artifactId>
</dependency>
```

## JDK

Compiled and tested against the JDK declared by the parent build (currently JDK 26). The minimum supported runtime is JDK 17 because the new `Result<T, E>` uses sealed interfaces and records.

If you consume this module under JPMS, add the modules you actually depend on:

```java
module com.svenruppert.functional.reactive {
    exports com.svenruppert.functional;
    exports com.svenruppert.functional.functions;
    exports com.svenruppert.functional.matcher;
    exports com.svenruppert.functional.memoizer;
    exports com.svenruppert.functional.model;
    exports com.svenruppert.functional.model.serial;
    exports com.svenruppert.functional.reactive;
    exports com.svenruppert.functional.result;
    exports com.svenruppert.functional.result.functions;
    exports com.svenruppert.functional.tuple;
}
```

## Result<T, E>

A `sealed interface` with two records, `Success<T, E>(T value)` and `Failure<T, E>(E error)`. Use it whenever a method can fail in a way the caller is expected to handle — checked exceptions and `Optional` both leave gaps that `Result` fills cleanly.

```java
Result<Integer, String> r = Result.success(42);

String label = r
    .map(n -> n * 2)
    .map(Object::toString)
    .recover(err -> "fallback: " + err)
    .getOrThrow();
```

Factories:

* `Result.success(T)`
* `Result.failure(E)`
* `Result.ofNullable(T value, E errorIfNull)`
* `Result.ofNullable(T value, Supplier<? extends E> errorIfNull)`

Default methods on every `Result`:

* Inspection: `isSuccess()`, `isFailure()`.
* Extraction: `getOrThrow()`, `getOrElse(T)`, `getOrElse(Function<E, T>)`, `toOptional()`, `stream()`.
* Transformation: `map(Function)`, `flatMap(Function)`, `mapError(Function)`.
* Recovery: `recover(Function<E, T>)`, `recoverWith(Function<E, Result>)`.
* Side effects: `peek(Consumer<T>)`, `peekFailure(Consumer<E>)`.
* Fold: `fold(Function<T, R> onSuccess, Function<E, R> onFailure)`.

Async overloads run the transformation on the supplied `Executor` (or the common pool by default):

```java
ExecutorService io = Executors.newVirtualThreadPerTaskExecutor();
CompletableFuture<Result<String, Throwable>> future = Try
    .of(this::readFromDisk)
    .mapAsync(payload -> parse(payload), io);
```

## Try

Captures a checked-exception-throwing supplier as a `Result<T, Throwable>` — the original cause and stack trace are preserved.

```java
Result<Path, Throwable> file = Try.of(() -> Files.createTempFile("sru-", ".tmp"));
file.peek(p -> log("created {}", p))
    .peekFailure(e -> log("creation failed: {}", e.getMessage()));
```

Pair it with `ThrowingSupplier<T>` (which permits `throws Exception`) to wrap any JDK API that forces a try/catch on the caller.

## Checked function interfaces

Live in `com.svenruppert.functional.result.functions` and allow `throws Exception` in their `apply`/`get`:

* `CheckedSupplier<T>`
* `CheckedFunction<T, R>`
* `CheckedBiFunction<T, U, R>`
* `CheckedTriFunction<T, U, V, R>`

Each interface offers `andThen(...)` plus a bridge that lifts the call into a `Result<T, Throwable>`:

```java
CheckedFunction<String, URL> toUrl = URI::create;
Function<String, Result<URL, Throwable>> safe = toUrl.lifted();
```

## Tuples

Record-based tuples in `com.svenruppert.functional.tuple`:

| Type | Arity |
|------|-------|
| `Single<T1>` | 1 |
| `Pair<T1, T2>` | 2 |
| `Triple<T1, T2, T3>` | 3 |
| `Quad<…>` | 4 |
| `Quint<…>` | 5 |
| `Sext<…>` | 6 |
| `Sept<…>` | 7 |

Use them when you need a small, immutable ad-hoc data structure without writing a class. Record components are accessed by position: `pair.t1()`, `pair.t2()`, etc. The legacy boxes in `com.svenruppert.functional.model.DataRecords` stay in place for callers that depend on the old getter convention (`getT1()`).

## Memoizer

Thread-safe lazy caches built around standard JDK functional types. The supplier variant is single-evaluation under contention (volatile + double-checked locking); the function variants delegate to `ConcurrentMap.computeIfAbsent`.

```java
Supplier<HeavyConfig> cfg = Memoizer.memoize(HeavyConfig::loadFromDisk);
HeavyConfig first = cfg.get();   // loads
HeavyConfig second = cfg.get();  // cached
assert first == second;

Function<String, Integer> wordCount = Memoizer.memoize(text -> text.split("\\s+").length);
```

## Case (pattern helper)

`Case<T, R>` provides a fluent match-style construction over plain predicates and value mappers — handy when JDK pattern matching cannot express the rule.

## StringFunctions

A grab-bag of locale-safe string operations: `indexOfCaseSensitive`, `indexOfCaseInsensitive`, camelCase / snake-case converters, padding, substring helpers, plus the older predicate stream feeds. All locale-sensitive operations use `Locale.ROOT` internally.

## CompletableFutureQueue

Builds an ordered sequence of asynchronous transformations and lets you replay it as a single `Function<T, CompletableFuture<R>>`:

```java
CompletableFutureQueue<Input, Output> pipeline = CompletableFutureQueue
    .define(this::stepOne)
    .thenCombineAsync(this::stepTwo)
    .thenCombineAsync(this::stepThree);

CompletableFuture<Output> result = pipeline.apply(input);
```

Errors raised by any stage land in the returned future's `exceptionally` instead of escaping the pipeline.

## Bridge to the legacy Result<T>

The original `com.svenruppert.functional.model.Result<T>` still exists. The static helper `Results` (in `com.svenruppert.functional.result`) converts in both directions:

```java
// legacy -> modern
Result<Path, String> modern = Results.fromLegacy(legacyResult, "value was null");

// modern -> legacy
com.svenruppert.functional.model.Result<Path> legacy = Results.toLegacy(modern);
```

For non-`String` error types use the overload `toLegacy(Result<T, E>, Function<E, String>)`.

## Tests and mutation coverage

Test count after release 06.02.00: 304 (1 skipped, 0 failures). Mutation coverage is not gated on this module today; the surface targeted by tests covers the major code paths of `Result`, `Try`, the checked-function bridges, tuples, and the locale-related `StringFunctions` work.

## Roadmap

See `IMPLEMENTATION_PLAN.md` in this directory for the planned next steps (migration of internal callers to the new `Result<T, E>`, async executor overrides on `CompletableFutureQueue`, stream/validation extensions).

## Release history

Release-by-release notes for older versions are preserved below for historical reference. For the current release, see the parent `RELEASE-NOTES-06.02.00.md`.

### 06.02.00

New `Result<T, E>` sealed type with records (`Success`, `Failure`), `Try.of(ThrowingSupplier)`, checked function interfaces in `result.functions`, record-based tuples `Single`…`Sept`, and the `Results` bridge to the legacy type. Memoizer race condition fixed, `CompletableFutureQueue.define` now wraps synchronous transformation errors into a `failedFuture`, `StringFunctions` uses `Locale.ROOT` throughout, `indexOfCoseSensitive` typo corrected (new `indexOfCaseSensitive`, old method deprecated), `Result.thenCombine*` short-circuits on Failure, `Failure.asFailure()` preserves the original error message.

### 03.00.00 — BREAKING CHANGES

Re-homed into the `com.svenruppert:dependencies` reactor as a sub-module of version 06.00.00. Same code as the historical `rapidpm`-namespace artefact, no new features.

### 02.00.04-SRU

Fixed JDK reference in `jitpack.yml`.

### 02.00.03-SRU

Updated parent and minimum Maven version to 3.9.6.

### 02.00.02-SRU

Switched groupId — JitPack custom-domain handling was unreliable. Updated to parent 05.00.03-SRU.

### 02.00.00-SRU

Namespace migration to `com.svenruppert:functional-reactive`.

### 01.01.00-RPM-SNAPSHOT

Increased test coverage and documentation. Apache licence header added to every file. Stable structure for the underlying functions (these had been in production for a long time already).

### 01.00.07-RPM

* `Transformations`
* `static <T> Function<Iterator<T>, Stream<T>> iteratorToStream()`
* `<V, R> Result<R> thenCombineFlat(V value, BiFunction<T, V, R> func)`
* Started the YouTube series about this library.
* Removed JitCI from the production pipeline.

### 01.00.03-RPM

* Added JitCI for deployment.
* Version updates.

### 01.00.02-RPM

* `static <T, R> CheckedFunction<T, R> asCheckedFunc(Function<T, R> f)`
* `Converting.convertToString`, `convertToInteger`, `convertToDouble`
* `SystemProperties` helper functions
* `Single<T>`

### 01.00.01-RPM

* Bugfix: the result was being computed twice in one path.

### 01.00.00-RPM

* Locked the API surface as `final` — no significant changes for a long time in production.
* JDK 8 used to build the jar.
* `module-info.java` template available inside `_data`.

### 00.07.06-RPM

* Parent POM and version updates.

### 00.07.05-RPM

* Parent POM and version updates.
* Added docker-based deploy scripts.

### 00.07.04-RPM

* Switched to the new version-number format (leading zeros to keep search/replace simple — `0.7.4` clashed with versions from other libraries).

### 0.7.3

* Dependency updates.
* Added `CompletableFutureQueue<T, N> thenCombineAsyncFromArray(Function<R, N>[] nextTransformations)`.

### 0.7.2

* Bugfix for `modules-info.java`.

### 0.7.1 — DO NOT USE — broken `module-info.java`

* Activated JIGSAW.
* Updated to `rapidpm-dependencies` 4.0.2.

### 0.7.0 / 0.7.0-JDK8-SNAPSHOT

* Started JDK 10/11 support.
* Using JDK 11 for development, JDK 10 for deployment.

### 0.6.2

* Last JDK-8-only release — switching to JDK 10/11 / JDK 8 model.

### 0.6.1

* Added docker scripts for cross-JDK compiles.
* Updated parent versions.
* Deactivated the Nexus mirror for drone.
* Deactivated the original IBM JDK 8/9 Docker images.

### 0.6.0

Maintenance only, no new functionality.

* Switched to dependencies version 0.6.3
    * This includes the licence header plugin.
    * Version updates.
    * Minimised profiles.
    * Removed the indirect dependency to the old Nexus.
* Updated all file headers.

### 0.5.3

* Added `CheckedTriFunction<T1, T2, T3, R> extends TriFunction<T1, T2, T3, Result<R>>`.
* Typo: `unCurryBifunction` → `unCurryBiFunction`.
* Typo: `unCurryTrifunction` → `unCurryTriFunction`.
* Curry / uncurry helpers for the checked functions in `Transformations`.

### 0.5.2

* Extended `Result<T>` with a fluent API:
    * `void ifFailed` → `Result<T> ifFailed`
    * `void ifPresent` → `Result<T> ifPresent`
    * `void ifAbsent` → `Result<T> ifAbsent`

### 0.5.1

* Renamed `Tripel` (German) to `Triple` (English).
* PIT now works with JUnit 5.

### 0.5.0

* `Result` gained `void ifFailed(Consumer<String> failed)`.
* Switched to JUnit 5.
* Updated parent POM to 3.5.7.

### 0.1.0

* `Result` gained `<U> Result<U> asFailure()`.
* `Result` gained `<U> Result<U> flatMap(Function<T, Result<U>> mapper)`.
* `ExceptionFunctions.message()` now also includes the exception classname.
* Added `CompletableFutureQueue`.

### 0.0.6

* `ExceptionFunctions` — `Function<Exception, String> message()`.
* `ExceptionFunctions` — `Function<Exception, Stream<StackTraceElement>> toStackTraceStream()`.
* Added `Sext` and `Sept` data classes.

### 0.0.5

* Added `model.serial` package with data classes implementing `Serializable`.
* `Transformations` — curry / uncurry for `BiFunction` / `TriFunction`.
* `StreamFunctions` — `<T> Function<Predicate<T>, Function<Stream<T>, Stream<T>>> streamFilter()`.
* `Result.ofNullable`.
* Renamed `bind(Consumer<T> success, Consumer<String> failure)` to `ifPresentOrElse(Consumer<T> success, Consumer<String> failure)`.
* Added the JDK 9 signature `ifPresentOrElse(Consumer<? super T>, Runnable)`.
* Added the JDK 9 signature `Stream<T> stream()`.
* Added `Result<T> or(Supplier<? extends Result<? extends T>> supplier)`.
* Added `void ifAbsent(Runnable action)`.
* Added `<U> Result<U> map(Function<? super T, ? extends U> mapper)`.

### 0.0.4

* Added `Result.thenCombine`.
* Added `Result.thenCombineAsync`.
* Added `CheckedBiFunction`.

### 0.0.3

* Added `CheckedPredicate`.

### 0.0.2

* Basic data structures: `Pair`, `Triple`, `Quad`.
* Added `fromOptional` and `toOptional` to `Result`.
* Added `CheckedFunction`, `CheckedConsumer`, `CheckedSupplier`.
* Extracted `TriFunction` from `Memoizer` into the `functions` package.
* Added `QuadFunction`.
* Added `StringFunctions`.
* Added `Transformations`.
* Ported `Strman-java` into a functional style.