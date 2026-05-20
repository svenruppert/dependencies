# SRU Dependencies — Release Notes 06.02.00

**Release date:** 2026-05-19
**Previous release:** 06.01.00

---

## Highlights

- **DDI** has been modernised end-to-end: the reflection library is replaced with the maintained `org.reflections:reflections:0.10.2`, the `javax.inject` / `javax.annotation` annotations move to `jakarta.*`, and the previously static DI singleton state lives in an instance-based `DIContainer` with a configurable scan prefix and optional `@Named` / `@Qualifier` narrowing.
- **functional-reactive** ships a brand-new `Result<T, E>` sealed type with records, a `Try` helper, checked function interfaces, and record-based tuples (`Single` through `Sept`). The legacy types stay in place behind a bridge, so this release is additive on the functional-reactive side.
- **Build chain** is on Apache Maven 4: an `only-script` wrapper pinned to `4.0.0-rc-5` ships with the repository, the unmaintained `junit-platform-maven-plugin` is replaced by `maven-surefire-plugin`, and every plugin with a clean patch/minor bump has been refreshed.
- **Quality bar** is raised: PIT mutation coverage on DDI sits at **96 %** (287/300 killed, line coverage 95 %, test strength 96 %); **all six SpotBugs findings cleared** (one genuine fix plus a documented filter file for the remaining intentional design patterns).

---

## Breaking changes

### DDI — `javax.inject` / `javax.annotation` → `jakarta.*`

`@Inject` is no longer read from `javax.inject` and `@PostConstruct` is no longer read from `javax.annotation`. Both annotations must come from the corresponding `jakarta.*` namespace. The vendored `javax/inject/Inject.java` shipped with DDI in earlier releases is gone; the standard `jakarta.inject:jakarta.inject-api:2.0.1` and `jakarta.annotation:jakarta.annotation-api:2.1.1` artefacts are now consumed instead.

**Migration in three steps:**

1. Rewrite imports in your own source tree:
   ```sh
   find . -name "*.java" -exec sed -i '' \
     's|javax\.inject\.|jakarta.inject.|g;
      s|javax\.annotation\.PostConstruct|jakarta.annotation.PostConstruct|g' \
     {} +
   ```
2. Add the two new dependencies to your build:
   ```xml
   <dependency>
       <groupId>jakarta.inject</groupId>
       <artifactId>jakarta.inject-api</artifactId>
       <version>2.0.1</version>
   </dependency>
   <dependency>
       <groupId>jakarta.annotation</groupId>
       <artifactId>jakarta.annotation-api</artifactId>
       <version>2.1.1</version>
   </dependency>
   ```
3. Drop your existing `javax.annotation:javax.annotation-api` dependency.

No other module breaks compatibility in this release.

---

## functional-reactive

### Phase 1 — Bugfix wave (non-breaking)

- `Memoizer.doMemoize(Supplier)`: race condition in the lazy-init eliminated (`volatile` + double-checked locking).
- `CompletableFutureQueue`: class and field marked `final`; `define()` now wraps a synchronous transformation exception into a `failedFuture` instead of letting it escape from the returned `Function<T, CompletableFuture<R>>`.
- `StringFunctions`: 17 occurrences of `toLowerCase()` / `toUpperCase()` without `Locale` replaced by `Locale.ROOT` (fixes the Turkish dotless-I corner case); typo `indexOfCoseSensitive` corrected — the new `indexOfCaseSensitive()` is the canonical name, the misspelled method remains as a deprecated delegator marked `forRemoval`.
- `Result.thenCombine` / `thenCombineFlat` / `thenCombineAsync`: short-circuit on Failure (previously called `get()` without a presence check and threw NPE).
- `Result.Failure.asFailure()` now preserves the original error message on conversion instead of overwriting it with `"Object was null"`.
- API additions (non-breaking): `Result.isSuccess()` / `isFailure()` default methods as primitive companions to the boxed `isPresent()` / `isAbsent()`.
- New tests: `MemoizerConcurrencyTest`, `ResultFailurePropagationTest`, `CompletableFutureQueueTest`, plus locale-stability and `indexOfCaseSensitive` coverage in `StringFunctionsTest`.

### Phase 2 — `Result<T, E>` sealed type and tuple records

- New `com.svenruppert.functional.result` package:
  - `Result<T, E>` (`sealed interface`, permits `Success` and `Failure`) with records, factories (`success`, `failure`, `ofNullable`), default methods (`isSuccess`, `isFailure`, `getOrThrow`, `getOrElse`, `fold`, `peek`, `peekFailure`, `recover`, `recoverWith`, `map`, `flatMap`, `mapError`, `toOptional`, `stream`), and async variants (`mapAsync(Function[, Executor])`, `flatMapAsync(Function[, Executor])`).
  - `Try.of(ThrowingSupplier<T>) → Result<T, Throwable>` captures exceptions including the stack trace.
  - `Results` bridges to and from the legacy `com.svenruppert.functional.model.Result<T>`.
  - Checked-function interfaces in `com.svenruppert.functional.result.functions`: `CheckedSupplier`, `CheckedFunction`, `CheckedBiFunction`, `CheckedTriFunction`.
- New `com.svenruppert.functional.tuple` package: record-based tuples `Single`, `Pair`, `Triple`, `Quad`, `Quint`, `Sext`, `Sept`. The legacy `DataRecords` boxes stay in place.
- Tests: +67 methods across `ResultTest`, `ResultAsyncTest`, `TryTest`, `CheckedFunctionsTest`, `ResultsBridgeTest`, `TupleRecordsTest`.
- The full `functional-reactive` reactor now ships **304 tests green** (1 previously skipped, 0 failures).

---

## DDI

### Phase 1 — Bugfix wave (non-breaking)

- Removed the deprecated-for-removal `AccessController.doPrivileged` wrapper in `DI.injectIntoField`; cleaned the anonymous `PostConstruct` subclass and added a `(Class, Class<? extends Annotation>)` overload on `ReflectionsModel.getMethodsAnnotatedWith`.
- Fixed cache races: `ProducerLocator.findProducersFor`, `ImplementingClassResolver.handleManySubTypes`, the three `ReflectionsModel` caches and `InjectionScopeManager.deRegisterClassForScope` no longer combine `containsKey` + `get` (which could observe `null` under a concurrent `clearCache()`).
- Unreachable branches now scream loudly: `ImplementingClassResolver.handleOneSubType` throws `IllegalStateException` with context; `InstanceCreator`'s bare `RuntimeException("this point should never reached…")` is now a `DDIModelException` with context.
- Dead code removed: commented `activatePackages(boolean parallelExecutors, …)` overloads, `StaticMetricsProxyScanner` / `StaticLoggingProxyScanner` blocks, the unused `newSet` function, the unreachable `(!isInterface && isInterface)` branch in `createNewInstance`, and the redundant first arm of `putToScope`.
- New tests: `CacheConcurrencyTest` (16-thread cache stress) and `MutationGapTest` (12 PIT-driven gap closers).
- README modernised; new `ddi/IMPLEMENTATION_PLAN.md` introduces the phase-2 / phase-3 roadmap.
- PIT before/after: 88 % → 97 % mutation score, 0 mutations without coverage.

### Phase 2 — Modernisation wave

Five sub-phases combined into one release:

- **2.1** — Reflection library migration from `net.oneandone.reflections8:reflections8:0.11.7` to `org.reflections:reflections:0.10.2`. `PkgTypesScanner` rewritten against the new `Scanner` interface; `ConfigurationBuilder.setExpandSuperTypes(false)` set explicitly to preserve the previous filter semantics.
- **2.2** — `javax.inject` / `javax.annotation` → `jakarta.*` (**breaking**, see above).
- **2.3** — Static facade lifted into an instance-based `DIContainer`. `DI`, `ImplementingClassResolver`, `ProducerLocator` and `InjectionScopeManager` become thin static delegators to `DIContainer.global()`. `InstanceCreator`, `ProducerResolverLocator` and `ClassResolverCheck001` gain a `(DIContainer)` constructor, so injection inside a non-global container consistently uses that container's own state. New `ContainerIsolationTest` and `StaticFacadeDelegationTest`.
- **2.4** — Configurable classpath filter. `new DIContainer(String scanPrefix)` and a fluent `DIContainer.builder().withScanPrefix(...).build()`. The prefix survives `clearReflectionModel()`. Default unchanged (`com.svenruppert`). New `ScanPrefixTest`.
- **2.5** — Optional `@Named` / `@Qualifier` narrowing in front of the existing `ClassResolver` mechanism: with one match the impl is used directly, with zero matches a `DDIModelException` lists qualifiers and candidates, with multiple matches the existing flow continues. `Named` is compared by `value()`, every other qualifier via `Annotation.equals(...)`. New `QualifierTest`.

### Post-phase-2 SpotBugs cleanup

`mvnw spotbugs:check` initially reported six medium-severity findings against the new container code. After the cleanup:

- The leaky `DIContainer.reflectionsModel()` getter (`EI_EXPOSE_REP`) has been replaced with two narrow accessors `getClassesForPkg(String)` and `getActivatedPkgs()`. The now-orphan `scanPrefix()` accessor on `ReflectionsModel` is removed.
- The remaining five findings describe intentional design patterns (dual-mode container singleton + per-test instance, collaborator pattern in the helpers). They are excluded via a documented `ddi/spotbugs-exclude.xml` filter file wired into the DDI module's plugin configuration. The execution binding in the parent POM is unchanged: `spotbugs:check` still fails the build on any unexpected finding.

### DDI quality numbers (06.01.00 → 06.02.00)

| Metric             | 06.01.00 baseline | 06.02.00 |
|--------------------|-------------------|----------|
| Tests              | 86                | 119      |
| PIT mutation score | 88 % (217/246)    | 96 % (287/300) |
| PIT line coverage  | 94 %              | 95 %     |
| PIT test strength  | 92 %              | 96 %     |
| PIT without coverage | 9               | 1 (mapping quirk) |
| SpotBugs findings  | (not checked)     | 0        |

---

## Build / tooling

- **Apache Maven 4 wrapper.** `.mvn/wrapper/maven-wrapper.properties` uses `wrapperVersion=3.3.4`, `distributionType=only-script`, distribution URL pinned to `apache-maven-4.0.0-rc-5-bin.zip`. The wrapper jar and `MavenWrapperDownloader.java` are gone; new `mvnw` (Unix) and `mvnw.cmd` (Windows) scripts ship at the repository root. `./mvnw --version` reports `Apache Maven 4.0.0-rc-5`.
- **Test runner.** `de.sormuras.junit:junit-platform-maven-plugin:1.1.8` (unmaintained, breaks under Maven 4 rc-5 with a repository-list validation error) is replaced by `maven-surefire-plugin` with an equivalent effective configuration (`--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED --enable-native-access=ALL-UNNAMED`, `failIfNoTests=false`, `Test*.java` / `*Test.java` / `*Tests.java` / `*TestCase.java` includes).
- **Maven 4 compatibility.** The duplicated explicit `<repository id="central">` and `<pluginRepository id="central">` declarations have been removed from the parent POM (Maven 4 rc-5 rejects the duplicate against its own default). `minimum-maven.version` raised from `3.9.9` to `4.0.0-rc-5`.
- **Plugin patch / minor bumps:**
  | Plugin                                            | before  | after   |
  |---------------------------------------------------|---------|---------|
  | `maven-surefire-plugin`                           | 3.5.4   | 3.5.5   |
  | `maven-resources-plugin`                          | 3.4.0   | 3.5.0   |
  | `maven-enforcer-plugin`                           | 3.6.2   | 3.6.3   |
  | `org.owasp:dependency-check-maven`                | 12.2.0  | 12.2.2  |
  | `maven-shade-plugin`                              | 3.6.1   | 3.6.2   |
  | `org.apache.felix:maven-bundle-plugin`            | 6.0.0   | 6.0.2   |
  | `maven-failsafe-plugin`                           | 3.5.4   | 3.5.5   |
  | `com.github.spotbugs:spotbugs-maven-plugin`       | 4.9.8.2 | 4.9.8.3 |
  | `org.pitest:pitest-maven` (`pitest.version`)      | 1.23.0  | 1.23.1  |
- **Test framework bumps:** `junit-jupiter-api.version` and `junit-platform-launcher.version` both `6.1.0-M1 → 6.1.0-RC1`.
- **Deliberately deferred:** the Maven-4-native major plugin bumps (`maven-compiler-plugin` → `4.0.0-beta-4`, `maven-clean-plugin` → `4.0.0-beta-2`, `maven-jar-plugin` → `4.0.0-beta-1`, etc.) are still beta and carry configuration-drift risk; they will land in a follow-up release once stable.

---

## Reactor verification

`./mvnw clean verify` (Maven 4.0.0-rc-5, JDK 26):

| Module               | Tests | Failures | SpotBugs findings |
|----------------------|-------|----------|-------------------|
| core                 | 0     | 0        | 0                 |
| core-properties      | 0     | 0        | 0                 |
| functional-reactive  | 304   | 0        | 0                 |
| ddi                  | 119   | 0        | 0                 |

---

## Commit log

```
3c87022 ddi: clear all 6 SpotBugs findings (post phase-2 cleanup)
1c01cf2 build: maven 4 wrapper, surefire migration, plugin patch bumps
2a900bc ddi: phase 2 — modernisation wave (reflections, jakarta, container, qualifiers)
228586a functional-reactive: phase 2 — Result<T, E> sealed type + tuple records
c362e3f ddi: phase 1 bugfix wave (non-breaking)
d725cd5 functional-reactive: phase 1 bugfix wave (non-breaking)
```
