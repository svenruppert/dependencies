# SRU Dependencies — Release Notes 06.02.02

**Release date:** 2026-06-04
**Previous release:** 06.02.01

---

## Highlights

- **`functional-reactive` legacy API removed.** The single-type `com.svenruppert.functional.model.Result<T>`, the bridge class `com.svenruppert.functional.result.Results`, the six legacy checked-function interfaces under `com.svenruppert.functional.functions.*`, and the legacy `com.svenruppert.functional.matcher.Case` are deleted. Consumers must migrate to the modern `com.svenruppert.functional.result.Result<T, E>` API introduced (additive) in 06.02.00 — see *Breaking changes* below.
- **Modern `Result<T, E>` API completed.** New `Unit` non-null void-marker, new `result.functions.CheckedExecutor` returning `Result<Unit, Throwable>`, and a pattern-matching `result.matcher.Case` that operates directly on `Result<T, E>`. `Converting`, `StringFunctions`, `SystemProperties` and `Transformations` are now fully on the modern path — no more `@Deprecated(forRemoval = true)` carry-over from 06.02.01.
- **`core` gains `MediaType`.** Typed MIME / media-type enum modelled after RFC 9110 §8.3 with RFC 6839 structured-syntax-suffix family predicates (`isJsonFamily()` matches `*+json`, `isXmlFamily()` matches `*+xml`). Replaces the legacy `HttpStatusContentTypes` string-constant collection. 33 constants across `application/`, `text/`, `image/`, `multipart/`, `audio/`, `video/`; case-insensitive header lookup via `fromMime(String)`.

---

## Breaking changes

### `functional-reactive` — legacy `Result<T>` and friends are gone

The following types — all deprecated in 06.02.01 with `@Deprecated(forRemoval = true)` — have been removed:

| Removed type | Replacement |
|---|---|
| `com.svenruppert.functional.model.Result<T>` | `com.svenruppert.functional.result.Result<T, E>` |
| `com.svenruppert.functional.result.Results` (bridge) | direct construction via `Result.success(...)` / `Result.failure(...)` |
| `com.svenruppert.functional.matcher.Case` | `com.svenruppert.functional.result.matcher.Case` |
| `com.svenruppert.functional.functions.CheckedBiFunction` | `com.svenruppert.functional.result.functions.CheckedBiFunction` |
| `com.svenruppert.functional.functions.CheckedConsumer` | use `CheckedExecutor` (closure-captured argument) or roll your own |
| `com.svenruppert.functional.functions.CheckedExecutor` | `com.svenruppert.functional.result.functions.CheckedExecutor` (now returns `Result<Unit, Throwable>`) |
| `com.svenruppert.functional.functions.CheckedFunction` | `com.svenruppert.functional.result.functions.CheckedFunction` |
| `com.svenruppert.functional.functions.CheckedSupplier` | `com.svenruppert.functional.result.functions.CheckedSupplier` |
| `com.svenruppert.functional.functions.CheckedTriFunction` | `com.svenruppert.functional.result.functions.CheckedTriFunction` |

Note that `com.svenruppert.functional.functions.CheckedPredicate`, `QuadFunction` and `TriFunction` stay in the legacy package — they were never tied to `Result<T>` and remain in place.

The deprecated typo alias `StringFunctions.indexOfCoseSensitive()` (kept around since the rename to `indexOfCaseSensitive()`) has also been removed.

**Migration recipe:**

1. Rewrite imports:
   ```sh
   find . -name "*.java" -exec sed -i '' \
     's|com\.svenruppert\.functional\.model\.Result|com.svenruppert.functional.result.Result|g;
      s|com\.svenruppert\.functional\.matcher\.Case|com.svenruppert.functional.result.matcher.Case|g;
      s|com\.svenruppert\.functional\.functions\.Checked|com.svenruppert.functional.result.functions.Checked|g' \
     {} +
   ```
   `CheckedPredicate` keeps the old import — re-add it by hand if the sed above stripped it.
2. Add the second type parameter at every call site. `Result<T>` becomes `Result<T, E>` with `E` chosen for the call (`String` for messages, `Throwable` for caught exceptions, a domain enum for typed errors).
3. Stop relying on `Result.success(null)`. The modern `Success` rejects `null`; lift possibly-null references with `Result.ofNullable(value, errorIfNull)`. For side-effecting / void operations use `Result<Unit, E>` and return `Unit.INSTANCE` on success.
4. Replace direct construction calls that went through the `Results` bridge (`Results.success(...)`, `Results.failure(...)`) with `Result.success(...)` / `Result.failure(...)`.
5. If you used `CheckedExecutor` from the legacy package, note the new return type: `Result<Unit, Throwable>` instead of the old `Result<Void>`. `Errors` propagate; only `Throwable` subtypes other than `Error` are captured into the failure channel.

The 06.02.00 entry plus the bridge layer gave consumers a full minor-version window to migrate. From 06.02.02 onwards the modern `result.*` packages are the only `Result` surface in `functional-reactive`.

### `core` — `HttpStatusContentTypes` replaced by `MediaType`

The string-constant collection `HttpStatusContentTypes` is superseded by the typed `MediaType` enum. Migration is one-to-one for the constants that previously existed; new family predicates (`isJsonFamily()`, `isXmlFamily()`, `isApplication()`, `isText()` …) and the charset helpers (`withCharset(Charset)`, `withCharsetUtf8()`) are available alongside.

If you only consumed the raw `"application/json"` string, `MediaType.APPLICATION_JSON.mime()` is the drop-in.

No other breaking changes.

---

## core

### `MediaType` (new)

`com.svenruppert.dependencies.core.net.MediaType` — typed media-type enum modelled after RFC 9110 §8.3. Highlights:

* **33 constants** across `application/*`, `text/*`, `image/*`, `multipart/*`, `audio/*`, `video/*` — including the JSON family (`application/json`, `application/problem+json`, `application/hal+json`, `application/ld+json`, `application/x-ndjson`), the XML family (`application/xml`, `application/soap+xml`, `image/svg+xml`, `text/xml`), Server-Sent Events (`text/event-stream`), modern image formats (`image/webp`, `image/avif`) and the form / multipart payloads.
* **Family predicates** — `isJsonFamily()` and `isXmlFamily()` recognise the structured-syntax suffixes from RFC 6839 (`*+json`, `*+xml`), so `APPLICATION_PROBLEM_JSON.isJsonFamily()` returns `true`.
* **Charset helpers** — `withCharset(Charset)` and `withCharsetUtf8()` emit a lowercase charset parameter as RFC 9110 §8.3.2 recommends.
* **Header lookup** — `MediaType.fromMime(String)` accepts a full `Content-Type` header (parameters after `;` are stripped, surrounding whitespace trimmed, type/subtype lower-cased) and throws `IllegalArgumentException` if no constant matches.

16 JUnit 5 tests cover the family predicates, the charset rendering, the lookup normalisation path, and the negative cases. No SpotBugs findings.

---

## functional-reactive

### Modern API — completion pass

* **`Result<T, E>`** sealed type stays the canonical entry point (`Success<T, E>(T value)` requires a non-null value; `Failure<T, E>(E error)` requires a non-null error). The combinators (`map`, `flatMap`, `mapError`, `recover`, `recoverWith`, `peek`, `peekFailure`, `fold`, `getOrElse`, `getOrThrow`, `toOptional`, `stream`, `mapAsync`, `flatMapAsync`) are unchanged from 06.02.00.
* **`Unit`** (new) — `enum Unit { INSTANCE }`. Use `Result<Unit, E>` for void-returning operations; `Result.success(Unit.INSTANCE)` satisfies the no-null-success invariant.
* **`result.functions.CheckedExecutor`** (new) — `FunctionalInterface` extending `Supplier<Result<Unit, Throwable>>`. `executeWithException()` may throw any `Throwable`; the default `get()` returns `Result.failure(t)` for ordinary throwables and lets `Error` propagate. The sibling `result.functions.CheckedBiFunction`, `CheckedFunction`, `CheckedSupplier` and `CheckedTriFunction` follow the same convention.
* **`result.matcher.Case<T, E>`** — lazy pattern-matching helper. `match(defaultCase, matchers...)` evaluates each case's condition supplier in declaration order and returns the first matching case's result supplier output; later conditions and the default case are never invoked. Both the condition and the result supplier are null-checked; a case-result supplier returning `null` throws `NullPointerException` rather than producing a corrupt `Result`.

### `Converting`

* `convertToStringResult`, `convertToBooleanResult`, `convertToIntegerResult`, `convertToDoubleResult` are the only surface left. Errors render as `"ExceptionType - message"`; a `null` payload becomes `Result.failure("value was null")` because the modern `Result` forbids `Success(null)`.
* All deprecated `convertToX` methods (returning the legacy `Result<X>`) are removed.

### `StringFunctions`

* `atResult()` is the only 1-based character accessor left; the deprecated `at()` returning the legacy `Result<String>` is removed. The guard ordering (null check **before** `value.isEmpty()` and length math) prevents the NPE that the eagerly bound `value::isEmpty` method reference in the legacy implementation could produce.
* `indexOfCoseSensitive()` (the deprecated typo alias) is removed. Use `indexOfCaseSensitive()`.
* The remaining `match(...)` call sites inside the file (e.g. `containsCaseSensitive`, `truncate`, `surround`) drop the verbose `com.svenruppert.functional.result.Result` qualifier in favour of the unqualified `Result` import.

### `SystemProperties`

* All accessors return modern `Result<T, String>`: `systemPropertyResult` (with and without default), `systemPropertyBooleanResult`, `systemPropertyIntResult`, `systemPropertyDoubleResult`. Missing keys yield a failure whose message embeds the qualified key, e.g. `"system property 'com.example.Foo.bar' was null"`.
* All deprecated legacy accessors that returned `Result<T>` are removed.
* `BiFunction<Class, ...>` raw types tightened to `BiFunction<Class<?>, ...>` across the public surface.

### `Transformations`

* Imports rewired from `com.svenruppert.functional.functions.Checked*` to `com.svenruppert.functional.result.functions.Checked*`. The signatures of the public helpers remain source-compatible for callers that already pass lambdas (the new checked-function interfaces are also `@FunctionalInterface`).

### Tests

| Test class | Tests | Notes |
|---|---|---|
| `ConvertingModernTest` | 14 | covers the four modern converters and the `"ExceptionType - message"` rendering |
| `StringFunctionsAtResultTest` | 8 | guard ordering, null / empty / OOB cases |
| `SystemPropertiesTest` | 13 | qualified-key composition, default-value path, type-coerced accessors |
| `result.CheckedFunctionsTest` | 11 | the new modern checked-function interfaces |
| `result.matcher.CaseTest` | 10 | laziness, default invocation, null-safety on suppliers |

Legacy test classes covering the removed types (`model.ResultTest`, `model.ResultFailurePropagationTest`, `result.ResultsBridgeTest`, `matcher.CaseTest` under the legacy package, `functions.Checked*Test`, `ConvertingTest`, `DemoCase`, `DemoExceptions`, `DemoMigration`, `DemoResult`) are deleted alongside the production code they covered.

New demos in `src/test/java/demo/` (`DemoResultModern`, `DemoCaseModern`, updated `DemoMigration`) show the modern API end-to-end.

---

## ddi

No source changes in 06.02.02. PIT baseline unchanged from 06.02.00: **311 / 324 (96 %)**, line coverage 95 %, test strength 96 %.

---

## Build / tooling

* Reactor version bumped from `06.02.01` to `06.02.02-SNAPSHOT` during development; this release cuts the matching non-SNAPSHOT.
* No plugin or dependency bumps in this cycle — the CycloneDX SBOM lifecycle, source-plugin merge-safety configuration and Maven 4 wrapper carried over from 06.02.01 remain in effect.

---

## Documentation

* `RESULT-MIGRATION-CONCEPT.md` documents the two-step migration window (06.02.00 additive, 06.02.02 removal) and the modern API surface. Use it as a reference when migrating consumers off the legacy `Result<T>`.

---

## Reactor verification

`./mvnw clean verify` (Maven 4.0.0-rc-5, JDK 26):

| Module | Tests | Failures | SpotBugs findings | SBOM |
|----------------------|-------|----------|-------------------|------|
| core | 16 | 0 | 0 | ✓ |
| core-properties | 0 | 0 | 0 | ✓ |
| functional-reactive | — | 0 | 0 | ✓ |
| ddi | 124 | 0 | 0 | ✓ |
| parent (aggregator) | — | — | — | ✓ |

PIT mutation coverage on `ddi`: **311 / 324 (96 %)**, line coverage 95 %, test strength 96 %.

---

## Commit log

```
HEAD    release 06.02.02                                                          (to land)
HEAD~1  functional-reactive: remove legacy Result<T>, Results bridge,
        functions/Checked*, matcher/Case; modern result.* is the only API     (to land)
HEAD~2  core: add MediaType enum (RFC 9110 §8.3 + RFC 6839 family predicates) (to land)
9be4406 functional-reactive: migrate to modern Result<T, E> API
f5c1ef9 release 06.02.01: version bump, central-publishing workaround, bundle script
```
