# SRU — Core Properties

Single-class layered `Properties` resolver. Merges the same logical properties file from up to four sources in a defined order, so applications can ship sensible defaults inside the classpath while still letting operators override individual values from the working directory, the user home, or an explicit configuration directory.

## Licence and module

Licence: EUPL 1.2. Part of the multi-module build `com.svenruppert:dependencies`.

```xml
<dependency>
    <groupId>com.svenruppert</groupId>
    <artifactId>core-properties</artifactId>
</dependency>
```

Runtime dependency: `com.svenruppert:core` (for `HasLogger`).

## Public API

```java
package com.svenruppert.dependencies.core.properties;

public class PropertiesResolver {
    public static final String CONFIG_LOCATION_PROPERTY = "rapidpm.configlocation";

    public Properties get(String name);
}
```

`name` is the file name without the `.properties` extension. The resolver looks for `<name>.properties` in the four sources below and merges the loaded properties into one `Properties` instance:

| Order | Source                                                                                              | Override rank |
|-------|-----------------------------------------------------------------------------------------------------|---------------|
| 1     | The root of the classpath (`getClass().getResourceAsStream("/<name>.properties")`)                  | lowest        |
| 2     | The current working directory (`./<name>.properties`)                                               |               |
| 3     | The user home directory (`~/.<name>.properties`, resolved via `System.getProperty("user.home")`)    |               |
| 4     | A directory pointed to by the system property `rapidpm.configlocation`                              | highest       |

Sources further down the table take precedence — later sources overwrite earlier ones for the same key.

If a particular source is missing, the resolver continues silently with a debug-level log line instead of failing. A missing properties file on every source returns an empty `Properties` object.

## Usage

```java
PropertiesResolver resolver = new PropertiesResolver();
Properties props = resolver.get("app");

String dbUrl = props.getProperty("db.url");
```

To point the resolver at an explicit configuration directory on the command line:

```sh
java -Drapidpm.configlocation=/etc/myapp -jar myapp.jar
```

The CLI value above takes precedence over the classpath, working-directory, and user-home copies of `app.properties`.

## JDK

Compiled and tested against the JDK declared by the parent build (currently JDK 26). The minimum supported runtime is JDK 17.

## Building and testing

```sh
./mvnw -pl core-properties test
```

Today the module has no unit tests of its own (the resolver predates the current test-coverage push); contributions welcome.

## See also

* [core](../core/) — provides the `HasLogger` mixin used internally.
* The parent [README](../README.md) — overview of every module in the reactor.
