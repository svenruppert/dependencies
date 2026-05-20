# This is the base definition of the versions used by my projects.

[![Licence](https://img.shields.io/badge/Licence-EUPL%201.2-blue.svg)](https://eupl.eu/)

## Info

Multi-module Maven project published under `com.svenruppert`. The parent POM centralises the dependency and plugin versions; every module ships as an independent artefact and can be consumed on its own. Build chain: Apache Maven 4, JDK 17+ (built against JDK 26 in CI), Surefire-based test execution, PIT mutation testing on selected modules, SpotBugs gate.

## Modules

| Module | Artefact | Purpose | Web |
|---|---|---|---|
| [core](core/) | `com.svenruppert:core` | Small, dependency-light utility surface: `StringUtils`, `fs/`, `net/HttpStatus`, `serviceprovider/ServiceProvider`, immutable-collection collectors, `HasLogger` re-export, `SystemExitHandler`, `NestedBuilder`. Used as the common base of every other module. | — |
| [core-properties](core-properties/) | `com.svenruppert:core-properties` | Single-class layered `Properties` resolver — merges values from classpath, working directory, user-home, and a system-property-configurable directory. | — |
| [functional-reactive](functional-reactive/) | `com.svenruppert:functional-reactive` | Functional toolkit: sealed `Result<T, E>` + `Try`, checked function interfaces, record-based tuples (`Single`…`Sept`), `Memoizer`, `Case`, `StringFunctions`, `CompletableFutureQueue`, plus the legacy `model.Result<T>` behind a bridge. | [frp.svenruppert.com](https://frp.svenruppert.com) |
| [ddi](ddi/) | `com.svenruppert:ddi` | Dynamic Dependency Injection: classpath-scan bootstrap, `@Inject` field injection, `@Produces` / `Producer<T>`, `ClassResolver`, `ProducerResolver`, scope manager, optional `@Named` / `@Qualifier` narrowing, instance-based `DIContainer` for hermetic test setups. | [ddi.svenruppert.com](https://ddi.svenruppert.com) |
| [logger-adapter](logger-adapter/) | `com.svenruppert:logger-adapter` | Thin `HasLogger` interface backed by a small SLF4J `MessageFormatter` re-implementation — used by the original logger-driven projects. Currently held out of the default Maven reactor (`<!--<module>logger-adapter</module>-->`) but kept in the tree for historical builds. | — |

Each module's `README.md` is a self-contained handbook covering coordinates, public API, and usage examples.

## Versions

## 06.02.00

Major modernisation wave across `ddi` and `functional-reactive` plus a full build-chain refresh. See [RELEASE-NOTES-06.02.00.md](RELEASE-NOTES-06.02.00.md) for the full breakdown.

**Breaking** — `ddi` consumers must move `@Inject` from `javax.inject` to `jakarta.inject` and `@PostConstruct` from `javax.annotation` to `jakarta.annotation`. The vendored `javax/inject/Inject.java` shipped in earlier releases is gone; depend on `jakarta.inject:jakarta.inject-api:2.0.1` and `jakarta.annotation:jakarta.annotation-api:2.1.1` instead.

Highlights:

* `ddi` — reflection library swapped from the unmaintained `net.oneandone.reflections8` to `org.reflections:reflections:0.10.2`; static singleton state lifted into an instance-based `DIContainer` (with `DIContainer.global()` powering the existing static facade); configurable classpath scan prefix via `DIContainer.builder().withScanPrefix(...)`; optional `@Named` / `@Qualifier` narrowing on the resolver path.
* `functional-reactive` — new `Result<T, E>` sealed type with records (`Success`, `Failure`), a `Try.of(ThrowingSupplier)` helper, checked function interfaces, and record-based tuples `Single` through `Sept`. Legacy types stay in place behind a `Results` bridge.
* Build — Apache Maven 4 wrapper pinned to `4.0.0-rc-5` (`only-script` distribution); `de.sormuras.junit:junit-platform-maven-plugin` replaced by `maven-surefire-plugin`; nine plugin patch/minor bumps plus `pitest 1.23.1` and `junit-jupiter / junit-platform-launcher 6.1.0-RC1`.
* Quality — DDI mutation coverage 88 % → 96 % (PIT), zero SpotBugs findings across the reactor, 304 + 119 tests green in `functional-reactive` and `ddi`.

## 06.01.00

* Project licence changed to the European Union Public Licence 1.2 (EUPL-1.2). Source headers and `LICENSE.txt` updated; SPDX identifier `EUPL-1.2`.
* Routine maven-plugin and dependency version bumps.

## 06.00.11

### HttpStatus

Added more codes plus default message including a flag that indicates if it is a nonStandard code.
With the message family() you will get the group of each code.
For performance a cache is added into the lookup
The method fromCode() will transform an int into the instance of the status code

### HttpResponseUtils

### JsonUtils

## 06.00.06

Merged functional reactive lib into the project as sub-module.

## 06.00.00-SRU - BREAKING CHANGE

Switched away from jitpack, it is just not working!
Started with new Major-Version for current developments and to get rid of the example stuff
that I have done for jitpack.

Using JDK 21 (LTS) - managed by sdkman
sdk install java 21.0.4-tem
sdk use java 21.0.4-tem

Used infrastructure for development

### Reposilite

java -Xmx32M -jar reposilite-3.5.18-all.jar
inside the console you need to generate a token first
token-generate --secret=admin admin m
This will generate a user admin with pwd admin and the role manager

Add maven central https://repo1.maven.org/maven2/ to the mirrored repos.

## 05.00.07-SRU

one round for jitpack...

## 05.00.06-SRU

fixed jitpack.yml

### 05.00.05-SRU

updated plugins and minimum maven version to 3.9.6

### 05.00.04-SRU

skipped

### 05.00.03-SRU

Switched to the groupd ID com.github.svenruppert
because custom domain names (com.svenruppert) are not working properly with Jitpack.
Java Packages are still under com.svenruppert

Added the Modules:

- Logger Adapter
- Core
- Core Properties

I archived the original git repos and merged everything into this one.
With this, building new versions is way easier or better - less work. :-)

### 05.00.02-SRU

updated versions

### 05.00.01-SRU

Fixed the pitest pattern
<pitest-prod-classes>com.svenruppert.*</pitest-prod-classes>
<pitest-test-classes>junit.com.svenruppert.*</pitest-test-classes>

removed the distribution repo definitions, because jitpack is doing it.

### 05.00.00-SRU - BREAKING CHANGE

With version 5.x I will switch the namespace from org.rapidpm to com.svenruppert
This has to do with organisational requirements on my side.
I will move this repo to the Github organisation **svenruppert**.
The licence will be still the same. The change on your side,
should be only the declaration of the parent pom.

* version updates
* maven plugin updates
* compile JDK will be 21 LTS
* switch from docker to podman - _tools/docker/develop

### 04.08.00-RPM

* version updates
* maven plugins updates

### 04.07.03-RPM

* version updates
* maven plugins updates

### 04.06.00-RPM

* version updates

### 04.05.03-RPM

* deploy target - via property on command line
    * deploy-repo-url
    * deploy-repo-snapshots-url

### 04.05.02-RPM

* V14.1 activated
* V15 alpha version update
* version updates
* Kotlin version update

### 04.05.00-RPM

* deactivate V08 dependencies
* deactivate V10 dependencies
* version update nodeJS/NPM (12.13.0/6.12.0)
* V14 update - V14.0.10

### 04.04.00-RPM

* added jCenter to repositories
* removed jitpack from repositories
* vaadin update V14 - V14.0.9
* cleared **_release** profiles

### 04.03.01-RPM

### 04.03.01-RPM

* update V14.0.7 to V14.0.8

### 04.03.00-RPM

* splitted the **_release** profile
    + _release
    + _release_prepare
* version updates

### 04.02.00-RPM

* version updates
* removed lic checker (flow) mix from transitive deps
* removed all indirect vaadin flow dependencies / components
* added flow minimal needed dependencies

### 04.01.15-RPM

* revert BUGFIX from 04.01.14-RPM

### 04.01.14-RPM

* BUGFIX - combobox dependency

### 04.01.13-RPM

* junit update 1.5.2
* switched VaadinXX to manual latest version compositions
* VaadinXX excludes all transitive webjars

### 04.01.12-RPM

* vaadin v14.0.3
* V10/V14/Vxx - removed the dependency to vaadin - way to heavy
  To get the dev cycle up to usable, add only the flow dependencies you need into your pom.
  example : [Nano-Vaadin-Demos](https://github.com/orgs/Nano-Vaadin-Demos/dashboard)
* added minimal Flow dependencies, no themes, no components

### 04.01.11-RPM

* logger-adapter
* functional-reactive-lib
* pitest 1.4.10
* added junit-platform-testkit to scope test
* vaadinXX switched from V14 to V15 - 15.0.0.alpha1
* cache2k update to v1.2.4.Final

### 04.01.10-RPM

* added java-faker to scope test
* vaadin v14.0.2

### 04.01.09-RPM

* Update to V14 Final
* Update to stagemonitor 0.89.0

### 04.01.08-RPM

* junit-platform-maven-plugin.timeout - default 300 [s]

### 04.01.07-RPM

* version updates
* added Google Truth as default dependency into scope test
* excluded generated NodeJS/ NPM things from lic check

### 04.01.06-RPM

* version updates
* config changes for vaadin dependencies

### 04.01.05-RPM

* version updates
* config changes for vaadin dependencies

### 04.01.04-RPM

* version updates
* added rapidpm-vaadin-dependencies for V08/V10/V14/Vxx

### 04.01.02-RPM

* replaced reflections with reflections8
* deleted module - reflections
* changed compile cycle for AnnotationProcessing
* replaced dom4j to org.dom4j
* kotlin version update

### 04.01.01-RPM

* version updates
* externalizing version definitions
* version updates
* smaller BugFixings
* reduced maven minimum version to 3.3.9 for compatibility reasons

### 04.01.00-RPM

* added development profile for NodeJS/NPM - for Vaadin 14 projects
* externalizing required maven version

To define your maven version you could override the property **maven-enforcer-plugin.version**.
The default is mostly the latest maven version.

## Properties

* **minimum-maven.version** - setting required maven version, default is latest version
* **activateJavaOnly** - true = JDK only / false JDK plus Kotlin activated
* **kotlin.compiler.jvmTarget** - default latest release
* **kotlin.version** - default mostly latest version
* **kotlin.compiler.incremental** - default true

## switched to new version string format

To make search/replace easier, I started with a new version format.
04.00.05-RPM ( -SNAPSHOT). The x.y.z is used in the same way, as before, but added RPM
and leading zeros to make this format different from others.
With this it is less possible to mix/change version numbers from
other dependencies. A **4.0.3** could be used from different
dependencies. ;-)

## Changes between 3.6.x and 3.7

There a re a few modules deactivated and removed.

* reflection : moved as module into dynamic-dependency-injection project
* jdbc* : all deactivated and removed, code is available in the history

## Version 3.*

This will be the version based on **Java8**
I will create a LTD branch for this
The version 4.* will be based on the JDK 10/11 (as soon as 11 is released)
JDK 11 will be a LTS Version again.

## Version 4.*

With the beginning of this version
the project will be compiled with the actual JDK but on source level Java 8.