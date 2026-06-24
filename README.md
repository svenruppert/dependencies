# This is the base definition of the versions used by my projects.

[![Licence](https://img.shields.io/badge/Licence-EUPL%201.2-blue.svg)](https://eupl.eu/)

## Info

Multi-module Maven project published under `com.svenruppert`. The parent POM centralises the dependency and plugin versions; every module ships as an independent artefact and can be consumed on its own. Build chain: Apache Maven 4, JDK 17+ (built against JDK 26 in CI), Surefire-based test execution, PIT mutation testing on selected modules, SpotBugs gate.

## Modules

| Module | Artefact | Purpose | Web |
|---|---|---|---|
| [core](core/) | `com.svenruppert:core` | Small, dependency-light utility surface used as the common base of every other module: `StringUtils`, `fs/PathUtils` + `fs/DirectoryUtils`, `net/HttpStatus` + `net/PortUtils`, `serviceprovider/ServiceProvider`, immutable-collection collectors, `system/SystemExitHandler`, `basepattern/builder/NestedBuilder`, and the SLF4J-backed `logger/HasLogger` mixin (with `ClassValue`-cached loggers and a shared `StackWalker`). | — |
| [core-properties](core-properties/) | `com.svenruppert:core-properties` | Single-class layered `Properties` resolver — merges values from classpath, working directory, user-home, and a system-property-configurable directory. | — |
| [functional-reactive](functional-reactive/) | `com.svenruppert:functional-reactive` | Functional toolkit: sealed `Result<T, E>` + `Try`, checked function interfaces, record-based tuples (`Single`…`Sept`), `Memoizer`, `Case`, `StringFunctions`, `CompletableFutureQueue`, plus the legacy `model.Result<T>` behind a bridge. | [frp.svenruppert.com](https://frp.svenruppert.com) |
| [ddi](ddi/) | `com.svenruppert:ddi` | Dynamic Dependency Injection: classpath-scan bootstrap, `@Inject` field **and** constructor injection, `@Produces` / `Producer<T>`, `ClassResolver`, `ProducerResolver`, scope manager, optional `@Named` / `@Qualifier` narrowing, instance-based `DIContainer` for hermetic test setups. | [ddi.svenruppert.com](https://ddi.svenruppert.com) |

Each module's `README.md` is a self-contained handbook covering coordinates, public API, and usage examples.

The historical `logger-adapter` module was retired in 06.02.01 — the `HasLogger` mixin lives in `core` now and the SLF4J integration is consumed through the standard `org.slf4j:slf4j-api` dependency.

## Releasing

Releases of this reactor target Maven Central via Sonatype's `central-publishing-maven-plugin`. The release-relevant work is split across four profiles in the parent POM; each profile owns one concern, and the standard `maven-release-plugin` orchestrates them.

### Profile map

| Profile | Owned concern | Activation |
|---|---|---|
| `_release_prepare` | Source and Javadoc jars, EUPL header check, `requireReleaseDeps` enforcement (no `-SNAPSHOT` deps). | Auto-activated by `maven-release-plugin` via `<releaseProfiles>_release_prepare</releaseProfiles>` in `_deploy`. **Do not pass `-P_release_prepare` manually.** |
| `_release_sign-artifacts` | GPG signing of every released artefact (`*.jar`, `*-sources.jar`, `*-javadoc.jar`, `*-tests.jar`, `*-cyclonedx.{xml,json}` and the POMs). | Pass `-P_release_sign-artifacts` explicitly on the command line. |
| `_release_scan_dependencies` | OWASP Dependency-Check scan (`failBuildOnCVSS=8`); requires `NVD_API_KEY` to be set. | Pass `-P_release_scan_dependencies` explicitly. |
| `_deploy` | `central-publishing-maven-plugin` plus the `maven-release-plugin` configuration (`autoVersionSubmodules`, `releaseProfiles=_release_prepare`). | Pass `-P_deploy` explicitly for `deploy` / `release:*` goals. |

SBOMs (CycloneDX, both `bom.xml` and `bom.json` per module plus the reactor-aggregate) are generated in the **default** lifecycle from the `package` phase onwards, so they are always in sync with the artefact even when running a plain `mvn install`. No profile flag is required for that.

### Pre-flight checklist

* `JAVA_HOME` points to JDK 17+ (the wrapper resolves Maven 4.0.0-rc-5 itself).
* `NVD_API_KEY` is set in the environment (used by `_release_scan_dependencies`).
* A GPG key matching the developer entry is available on the local keyring and unlocked.
* Working tree is clean and on `develop`; `git fetch` is up to date with `origin`.
* `./mvnw clean verify` is green (full reactor tests, SpotBugs, CycloneDX SBOM generation).

### Release flow

```sh
# 1. Final smoke test (also produces the BOMs and runs SpotBugs)
./mvnw clean verify

# 2. Optional: explicit vulnerability scan
./mvnw -P_release_scan_dependencies verify

# 3. Cut the release tag + bump to the next SNAPSHOT via maven-release-plugin.
#    This consumes the _deploy profile (which sets autoVersionSubmodules and
#    forwards into _release_prepare), and the -Darguments string is what the
#    plugin passes to its inner mvn invocation that builds the tagged source.
./mvnw -P_deploy release:prepare release:perform \
    -DreleaseVersion=06.02.01 \
    -DdevelopmentVersion=06.02.02-SNAPSHOT \
    -Darguments="-P_release_sign-artifacts,_release_scan_dependencies"
```

`release:prepare` creates two commits (release version + next snapshot version) plus a lightweight tag. `release:perform` checks out the tag, runs `mvn verify deploy` against it with `_release_prepare` auto-activated, the `_release_sign-artifacts` and `_release_scan_dependencies` profiles passed through `-Darguments`, and the `_deploy` profile in effect — that gives signed jars, signed source/javadoc/test/SBOM artefacts, an OWASP scan, and a Sonatype Central upload in one shot.

### Current limitation — Maven 4 + central-publishing-maven-plugin

`central-publishing-maven-plugin` 0.10.0 did not understand the Maven 4 consumer-POM model: it emits the consumer POM as a `consumer`-classified artefact (`*-consumer.pom`), and the Central Portal validator rejects the deployment with `Failed to associate file with coordinates …`. As of 06.02.03 the plugin is pinned at 0.11.0; whether 0.11.0 resolves this is **still to be verified at the next deploy**. Until confirmed, the `_deploy` profile keeps `<skipPublishing>true</skipPublishing>` so that `mvn deploy` builds a signed bundle locally but does **not** upload it, and the manual bundle flow below remains the working path.

The working release flow against the current plugin is therefore:

```sh
# 1. Final smoke test
./mvnw clean verify

# 2. Bump versions across the reactor (or use release:prepare, which still works)
./mvnw versions:set -DnewVersion=06.02.01 -DprocessAllModules=true -DgenerateBackupPoms=false

# 3. Verify on the release version
./mvnw clean verify

# 4. Commit + tag
git commit -am "release 06.02.01"
git tag 06.02.01

# 5. Build the signed bundle (uploads nothing thanks to skipPublishing=true)
./mvnw -P_deploy,_release_sign-artifacts,_release_prepare clean deploy

# 6. Strip the Maven-4 consumer POMs and repackage the bundle
./scripts/clean-bundle-for-central.sh

# 7. Upload target/central-publishing/central-bundle.zip manually via
#    https://central.sonatype.com/publishing → Publish Component
#    (Deployment Name e.g. com.svenruppert:dependencies:<version>, Automatic publish.)

# 8. Bump to the next development cycle and commit
./mvnw versions:set -DnewVersion=06.02.02-SNAPSHOT -DprocessAllModules=true -DgenerateBackupPoms=false
git commit -am "prepare 06.02.02-SNAPSHOT"

# 9. Push branch + tag
git push origin develop 06.02.01
```

The `_deploy` profile in `pom.xml` carries a comment block documenting the same workaround at the point where `skipPublishing` is set.

### After the deploy

* Verify on [Maven Central](https://central.sonatype.com/namespace/com.svenruppert) (full indexing typically takes 10–60 minutes for a fresh artefact).
* Add a new section in this README under "Versions" and a corresponding `RELEASE-NOTES-<version>.md` at the repository root with the per-module change set, migration notes for any breaking change, and the reactor-level verification numbers.

## Versions

## 06.02.03

Maintenance release: dependency / plugin refresh, migration of the deploy
infrastructure to the Sonatype Central Portal endpoints, and adoption of
SpotBugs 4.10.2. No source-level API changes — consumers upgrade without code
changes. See [RELEASE-NOTES-06.02.03.md](RELEASE-NOTES-06.02.03.md) for the full
breakdown.

Highlights:

* Dependencies — jackson 2.21.3 → 2.22.0 and 3.1.3 → 3.2.0, HikariCP 7.0.2 → 7.1.0, byte-buddy 1.18.8 → 1.18.10, javassist 3.31.0-GA → 3.32.0-GA, pitest 1.25.0 → 1.25.5, checkstyle 13.4.2 → 13.6.0.
* Plugins — spotbugs-maven-plugin 4.9.8.3 → 4.10.2.0, maven-dependency-plugin 3.10.0 → 3.11.0, cyclonedx-maven-plugin 2.9.1 → 2.9.2, central-publishing-maven-plugin 0.10.0 → 0.11.0. The Maven core plugins offering only `4.0.0-beta` / `3.6.0-M1` pre-releases were intentionally left on their stable lines.
* Deploy — `distributionManagement` and the deploy URLs moved to the Central Portal (`central.sonatype.com`); the OSSRH host `s01.oss.sonatype.org` was decommissioned on 2025-06-30. Repository ids are now `central` / `central-snapshots`.
* Quality — SpotBugs 4.10.2 added two new detectors; both findings were fixed at source rather than excluded. `functional-reactive`: `Case` validates in its factory methods so the constructor no longer throws (`CT_CONSTRUCTOR_THROW`). `ddi`: `DIContainer` synchronizes on a private lock instead of `this` (`USO_UNSAFE_METHOD_SYNCHRONIZATION`). Both changes are behaviour-preserving.
* Verification — core 16/16, core-properties 4/4, functional-reactive 312/312 (1 skipped), ddi 124/124; 0 SpotBugs findings reactor-wide.

## 06.02.02

Removal of the legacy single-type `Result<T>` API from `functional-reactive` and
the new typed `MediaType` enum in `core`. See
[RELEASE-NOTES-06.02.02.md](RELEASE-NOTES-06.02.02.md) for the full breakdown.

**Breaking** — `functional-reactive`: the legacy `com.svenruppert.functional.model.Result<T>`, the `functional.result.Results` bridge, the six `functional.functions.Checked*` interfaces and `functional.matcher.Case` (all deprecated `forRemoval` in 06.02.01) are deleted; migrate to `com.svenruppert.functional.result.Result<T, E>`. `core`: the `HttpStatusContentTypes` string constants are replaced by the typed `MediaType` enum.

Highlights:

* `functional-reactive` — modern `Result<T, E>` is now the only `Result` surface. New `Unit` void-marker, `result.functions.CheckedExecutor` returning `Result<Unit, Throwable>`, and a `result.matcher.Case<T, E>` with lazy condition evaluation.
* `core` — new `MediaType` enum (RFC 9110 §8.3 with RFC 6839 `*+json` / `*+xml` structured-syntax-suffix predicates), 33 constants plus charset helpers and `fromMime(String)`.
* Release tooling — `scripts/clean-bundle-for-central.sh` rebuilt for Maven 4.

## 06.02.01

Security patch, build-chain hardening, and the retirement of the historical `logger-adapter` module. See [RELEASE-NOTES-06.02.01.md](RELEASE-NOTES-06.02.01.md) for the full breakdown.

**Breaking** — `com.svenruppert:logger-adapter` is no longer published. The `HasLogger` mixin now lives natively in `com.svenruppert:core`; consumers replace the dependency coordinate but keep the import (`com.svenruppert.dependencies.core.logger.HasLogger` is unchanged). Also: `jakarta.annotation-api` jumps from 2.1.1 to 3.0.0 — consumers that pin the 2.x line explicitly should align or drop the pin.

Highlights:

* Security — `tools.jackson.core:jackson-core` / `jackson-databind` bumped past `GHSA-2m67-wjpj-xhg9` (CVSS 7.5, `maxDocumentLength` enforcement bypass). Now pinned at `3.1.3` in `<dependencyManagement>`.
* `core` — `HasLogger` rewritten on top of `ClassValue<LoggingService>` (no `ConcurrentMap` boxing, class-unloading-safe) with a shared `StackWalker` instance. No API change.
* Build — `org.cyclonedx:cyclonedx-maven-plugin:2.9.1` now runs in the default lifecycle: every `mvn install` / `mvn deploy` emits per-module `bom.xml` + `bom.json` plus a reactor-aggregate, attached as `*-cyclonedx.{xml,json}` sub-artefacts.
* Build — `maven-source-plugin` execution split into Apache-conventional `attach-sources` / `attach-test-sources` with the `*-no-fork` goal variants, so downstream POMs that ship their own source-attach setup merge with the parent instead of producing duplicate `sources` attachments.
* Dependencies — 19 properties / pinned versions refreshed (pitest 1.25.0, junit-jupiter / junit-platform-launcher 6.1.0, ASM 9.10.1, javassist 3.31.0-GA, jakarta.annotation-api 3.0.0, jakarta.inject-api 2.0.1.MR, jackson 3.1.3 / 2.21.3, gson 2.14.0, commons-codec 1.22.0, commons-io 2.22.0, checkstyle 13.4.2, …).
* Quality — DDI: 124 / 124 tests green, PIT 311 / 324 (96 %), 0 SpotBugs findings; functional-reactive: 304 / 304 green.
* Documentation — Root README gains a "Releasing" section that documents the four release profiles, the GPG / SBOM / Sonatype flow, and the manual deploy variant. `ddi/README.md` gains a "Constructor injection" section.
* Deploy — `_deploy` profile now sets `<skipPublishing>true</skipPublishing>` on `central-publishing-maven-plugin` (v0.10.0 is incompatible with Maven 4 consumer POMs; the validator rejects the `-consumer.pom` classifier). `scripts/clean-bundle-for-central.sh` strips those files from the staged bundle so the resulting `central-bundle.zip` can be uploaded manually via [https://central.sonatype.com/publishing](https://central.sonatype.com/publishing). 06.02.01 was published that way.

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