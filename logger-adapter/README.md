# SRU — Logger Adapter

Thin logging shim built around an `HasLogger` interface and a small SLF4J `MessageFormatter` re-implementation. Used by older logger-driven projects that depend on the `HasLogger` mixin without pulling in a full logging back-end.

## Status

**Currently held out of the default Maven reactor.** The parent POM keeps the line commented:

```xml
<modules>
    <!--        <module>logger-adapter</module>-->
    <module>core</module>
    ...
</modules>
```

The source tree is kept so that historical projects which still depend on
`com.svenruppert:logger-adapter` can build it on demand, and so that the
`HasLogger` API exposed by the active `core` module stays bit-identical to
its original definition here. Reactivate the module in the reactor by
removing the surrounding XML comment.

## Licence and module

Licence: EUPL 1.2. Part of the multi-module build `com.svenruppert:dependencies`.

```xml
<dependency>
    <groupId>com.svenruppert</groupId>
    <artifactId>logger-adapter</artifactId>
</dependency>
```

## Public API

### `HasLogger`

Mixin interface for SLF4J-style logging. Two entry points:

* `default LoggingService logger()` — instance-scoped logger, computed via `Logger.getLogger(getClass())`.
* `static LoggingService staticLogger()` — for static contexts; uses `StackWalker` to identify the caller class and caches the resolved logger in a `ConcurrentMap<Class<?>, LoggingService>`.

```java
public class OrderService implements HasLogger {
    public void place(Order o) {
        logger().info("placing order {}", o.id());
    }

    static void diag(String msg) {
        staticLogger().debug(msg);
    }
}
```

### `tp/org/slf4j/helpers/MessageFormatter`

Embedded copy of the SLF4J 1.x `MessageFormatter` (under its original MIT licence — see the source header). Lets the module format `"{}"`-style log messages without needing the full SLF4J distribution at compile time. Internal — production code should use SLF4J directly.

## JDK

Compiled against the JDK declared by the parent build (currently JDK 26). The minimum supported runtime is JDK 17 because the static logger uses `StackWalker.Option.RETAIN_CLASS_REFERENCE`.

## Building

The module is not built by the default reactor. To build it explicitly, either reactivate the `<module>` line in the parent POM or build the module directly:

```sh
./mvnw -pl logger-adapter -am compile
```

## See also

* [core](../core/) — re-exports `HasLogger` and is the recommended dependency for new code.
* The parent [README](../README.md) — overview of every module in the reactor.
