# SRU — Core

Small, dependency-light utility surface used as the common base of every other module in the `com.svenruppert:dependencies` reactor. No framework, no opinionated runtime — just a tight set of helpers that show up in nearly every project.

## Licence and module

Licence: EUPL 1.2. Part of the multi-module build `com.svenruppert:dependencies`.

```xml
<dependency>
    <groupId>com.svenruppert</groupId>
    <artifactId>core</artifactId>
</dependency>
```

The only external runtime dependency is SLF4J (via `logger-adapter`); transitive consumers usually get this for free.

## JDK

Compiled and tested against the JDK declared by the parent build (currently JDK 26). The minimum supported runtime is JDK 17.

## Public API

All public types live under `com.svenruppert.dependencies.core`.

### `StringUtils`

Static, locale-safe helpers for common string operations — null/empty checks, defensive trimming, basic case conversions, and a few `String.format`-style shortcuts. Uses `Locale.ROOT` for any case operation so that the Turkish dotless-I case does not silently corrupt comparisons.

### `fs/PathUtils`, `fs/DirectoryUtils`

Filesystem helpers built on `java.nio.file`. `PathUtils` covers path normalisation, parent-resolution and existence checks; `DirectoryUtils` adds recursive walk, sub-directory enumeration, and safe-delete semantics.

### `net/HttpStatus`

Enum-like type collecting all standard HTTP status codes plus a curated set of non-standard ones (with a flag that marks them as such). Default messages, a `family()` accessor that returns the group (1xx, 2xx, …), and a cached `fromCode(int)` lookup keep the lookup constant-time.

```java
HttpStatus status = HttpStatus.fromCode(418);
String family = status.family();    // "4xx"
boolean odd  = status.nonStandard(); // true
```

### `net/PortUtils`

Tools for picking and probing local TCP ports — useful when wiring up integration tests that must bind to a free port at runtime.

### `logger/HasLogger`

Mixin interface for SLF4J-style logging. Add `implements HasLogger` to any class to get a `logger()` method (instance-scoped, computed on demand) plus a `staticLogger()` helper for static contexts. The interface delegates to the `logger-adapter` module's `HasLogger` implementation, so callers depend on a single API regardless of which logging back-end is on the classpath.

```java
public class OrderService implements HasLogger {
    public void place(Order o) {
        logger().info("placing order {}", o.id());
    }
}
```

### `serviceprovider/ServiceProvider`

Thin wrapper around `java.util.ServiceLoader` with helpers for "load all", "load first", and "load by predicate". Caches the loader internally so repeated lookups do not re-scan the classpath.

### `stream/ImmutableListCollector`, `stream/ImmutableSetCollector`

Custom `Collector` implementations that return immutable views over an `ArrayList` / `LinkedHashSet`. Useful when you want stream pipelines to produce read-only results without depending on Guava.

```java
List<String> tags = stream.map(Tag::name).collect(ImmutableListCollector.toImmutableList());
```

### `system/ExitHandler`, `system/SystemExitHandler`

Injectable `System.exit(int)` abstraction. Production code calls `ExitHandler.exit(code)`; tests swap in a stub that records the call instead of terminating the JVM.

### `basepattern/builder/NestedBuilder`

Reusable scaffolding for builder hierarchies where one builder returns to its parent at the end. The base class encapsulates the back-reference and the `done()` call so concrete builders only define their own field setters.

## Building and testing

`core` currently ships without unit tests of its own — the helpers are exercised by downstream modules (`ddi`, `functional-reactive`). Build it through the reactor:

```sh
./mvnw -pl core compile
```

## Compatibility

The public API of `core` is stable. Method additions are non-breaking; removals are announced one major version ahead through deprecation.

## See also

* The parent [README](../README.md) — overview of every module in the reactor.
* [logger-adapter](../logger-adapter/) — the implementation that backs `HasLogger`.
