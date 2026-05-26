# SRU Dependencies — Release Notes 06.02.01

**Release date:** 2026-05-26
**Previous release:** 06.02.00

---

## Highlights

- **Security:** Jackson Core 3.x is bumped past `GHSA-2m67-wjpj-xhg9` (CVSS 7.5, `maxDocumentLength` enforcement bypass). The advisory affected `tools.jackson.core:jackson-core` `>= 3.0.0, <= 3.1.0`; this release ships `3.1.3`.
- **`logger-adapter` module retired.** The `HasLogger` mixin now lives natively in `core`, with a `ClassValue`-based logger cache and a single shared `StackWalker`. Downstream consumers should migrate from `com.svenruppert:logger-adapter` to `com.svenruppert:core` (see the *Breaking changes* section below).
- **CycloneDX SBOMs in the default lifecycle.** Every `mvn install` / `mvn deploy` now produces `bom.xml` + `bom.json` per module (plus a reactor-aggregate at the parent) and attaches them as `*-cyclonedx.{xml,json}` sub-artefacts. No profile flag required.
- **Build hardening for downstream merge-safety.** `maven-source-plugin` now uses the conventional Apache execution IDs (`attach-sources`, `attach-test-sources`) and the `*-no-fork` goal variants, so downstream POMs that ship their own source-attach setup merge with the parent's configuration instead of producing duplicate `sources` attachments.
- **Refreshed dependency floor.** Sixteen properties or pinned versions bumped — see the *Build / tooling* section.
- **Documentation.** Root README gains a "Releasing" section that documents the four release profiles, the GPG / SBOM / Sonatype flow, and the manual release variant.

---

## Breaking changes

### `com.svenruppert:logger-adapter` is no longer published

The `logger-adapter` module was retired during 06.02.01 development. The `HasLogger` interface — the only public type the module exposed — is now provided directly by `com.svenruppert:core`.

**Migration:** replace any dependency on `com.svenruppert:logger-adapter` with a dependency on `com.svenruppert:core`. The fully-qualified class name (`com.svenruppert.dependencies.core.logger.HasLogger`) is unchanged, so no source-level edits are needed; only the dependency coordinates move.

The implementation is also faster: `LOGGER_CACHE` is now a `ClassValue<LoggingService>` (no `ConcurrentMap` boxing, GC-aware against class-unloading) and `StackWalker.getInstance(...)` is shared across `staticLogger()` calls instead of being allocated per invocation.

### `jakarta.annotation-api` 2.1.1 → 3.0.0

Major version bump. Source-compatible for the annotations actually used in this reactor (`@PostConstruct`, `@Generated`), but consumers that pin `jakarta.annotation-api` to a 2.x version explicitly will get a runtime classpath split if they pull `com.svenruppert:ddi` 06.02.01 alongside. The fix on the consumer side is either to remove the explicit pin or to bump it to 3.x as well.

No other breaking changes.

---

## Security

### Jackson Core — GHSA-2m67-wjpj-xhg9 (CVSS 7.5, high)

Three parser entry points in Jackson Core 3.0.0 through 3.1.0 do not enforce `StreamReadConstraints.maxDocumentLength` consistently:

* Blocking parsers (`ReaderBasedJsonParser`, `UTF8StreamJsonParser`) skip validation of the final in-memory buffer.
* Async parsers (`NonBlockingByteArrayJsonParser`, `NonBlockingByteBufferJsonParser`, `NonBlockingUtf8JsonParserBase`) skip validation of the final chunk on `endOfInput()`.
* The `UTF8DataInputJsonParser` path does not call `validateDocumentLength(...)` at all.

Applications that rely on `maxDocumentLength` as a DoS-defence ceiling could accept oversized JSON payloads without an exception. Fixed in Jackson Core 3.1.1.

This release pins `tools.jackson.core:jackson-core` and `tools.jackson.core:jackson-databind` to `3.1.3` (the current patched line) in the `<dependencyManagement>` block. Neither artefact is consumed by a reactor module — the pin is purely for downstream projects that inherit the parent.

The Dependabot alert (#34) auto-closed on 985e74d.

---

## core

* `HasLogger`:
  * `LOGGER_CACHE` migrated from `ConcurrentHashMap<Class<?>, LoggingService>` to `ClassValue<LoggingService>`. `ClassValue` is GC-aware against class-unloading (matters in dynamic / container-style setups), and lookup is a direct identity check rather than a hash-map probe.
  * `staticLogger()` now reuses a single shared `StackWalker` instance (`WALKER`) instead of allocating a new one per call.
  * Unused imports removed (`java.util.Map`, `java.util.concurrent.ConcurrentHashMap`).
* No API change — the public surface is identical to 06.02.00; existing callers keep working without recompilation.

---

## ddi

No source changes in 06.02.01; the module continues at 06.02.00's feature level (constructor injection, hermetic `DIContainer` instances, configurable scan prefix, `@Named` / `@Qualifier` narrowing). The constructor-injection path documented in `ddi/README.md` lands fully in this release after the matching dependency bumps in `core` and the parent POM.

PIT baseline (unchanged from 06.02.00): **311 / 324 (96 %)**, line coverage 95 %, test strength 96 %.

---

## functional-reactive

No source changes in 06.02.01.

---

## Build / tooling

### CycloneDX SBOM generation (default lifecycle)

`org.cyclonedx:cyclonedx-maven-plugin:2.9.1` is now bound to the `package` phase via the parent POM's active `<build><plugins>` block. Every reactor module emits `target/bom.xml` and `target/bom.json` (both CycloneDX 1.6, with serial number) and attaches them as `cyclonedx`-classified sub-artefacts. They follow the regular install / deploy flow and end up next to the primary jar on Maven Central.

Configuration: `outputFormat=all`, `projectType=library`, scopes compile + runtime, `includeLicenseText=false`. The plugin honours `-Dcyclonedx.skip=true` as a per-build opt-out.

### Source-plugin merge-safety

The `maven-source-plugin` execution in `_release_prepare` is split into two named executions following the Apache convention:

* `<id>attach-sources</id>` with goal `jar-no-fork` — produces `*-sources.jar`.
* `<id>attach-test-sources</id>` with goal `test-jar-no-fork` — produces `*-test-sources.jar`.

Both `*-no-fork` variants produce the same artefacts as `jar` / `test-jar` but skip the redundant lifecycle fork at `generate-sources`. Downstream POMs that use the same execution IDs now merge with the parent's configuration instead of producing duplicate `sources` / `test-sources` attachments (which used to surface as Maven's "duplicate attached artefact" error).

### `_release_prepare` annotated as internal

A leading comment on the profile makes the auto-activation contract explicit: the profile is meant to be activated by `maven-release-plugin` via `<releaseProfiles>_release_prepare</releaseProfiles>`, not by `-P_release_prepare` on the command line. The `requireReleaseDeps` enforcer rule would refuse to build against `-SNAPSHOT` dependencies anyway, but the comment saves the next reader a debugging round.

### Documentation comment on the test-jar binding

The `maven-jar-plugin:test-jar` block in the active plugin section now carries an explanatory comment about why test-jars are published (downstream test-fixture reuse via classifier `tests`) and why `<goal>jar</goal>` must not be redeclared (the Maven lifecycle already binds it for `jar`-packaged modules).

### Dependency bumps

| Property / artefact | 06.02.00 | 06.02.01 |
|---|---|---|
| `pitest.version` | 1.23.1 | 1.25.0 |
| `junit-jupiter-api.version` | 6.1.0-RC1 | 6.1.0 |
| `junit-platform-launcher.version` | 6.1.0-RC1 | 6.1.0 |
| `asm.version` | 9.9.1 | 9.10.1 |
| `jakarta.annotation-api` | 2.1.1 | 3.0.0 |
| `jakarta.inject-api` | 2.0.1 | 2.0.1.MR |
| `javassist` | 3.30.2-GA | 3.31.0-GA |
| `byte-buddy` | 1.18.7-jdk5 | 1.18.8-jdk5 |
| `tools.jackson.core:jackson-core` | 3.1.0 | 3.1.3 |
| `tools.jackson.core:jackson-databind` | 3.1.0 | 3.1.3 |
| `com.fasterxml.jackson.core:jackson-core` | 2.21.1 | 2.21.3 |
| `com.fasterxml.jackson.core:jackson-databind` | 2.21.1 | 2.21.3 |
| `com.fasterxml.jackson.datatype:jackson-datatype-jdk8` | 2.21.1 | 2.21.3 |
| `com.google.code.gson:gson` | 2.13.2 | 2.14.0 |
| `commons-codec:commons-codec` | 1.21.0 | 1.22.0 |
| `commons-io:commons-io` | 2.21.0 | 2.22.0 |
| `org.testcontainers:testcontainers` | 2.0.4 | 2.0.5 |
| `io.gatling.highcharts:gatling-charts-highcharts` | 3.15.0 | 3.15.1 |
| `puppycrawl:checkstyle` | 13.2.0 | 13.4.2 |

### Housekeeping

* Six accidentally tracked `.DS_Store` files untracked; `.gitignore` rule on line 1 (`.DS_Store`) already covered the pattern. A copy-paste typo on the last `.gitignore` line concatenating `webpack.config.js.DS_Store` was split into two separate entries.

---

## Documentation

* `README.md` (root):
  * "Modules" table refreshed — `logger-adapter` row removed (module retired), `core` description mentions the new `ClassValue`-cached `HasLogger` directly, `ddi` description mentions constructor injection alongside field injection.
  * New "Releasing" section documents the four release profiles, the GPG / SBOM / Sonatype flow with `maven-release-plugin`, and the manual deploy variant used historically for the 06.02.00 cut.
* `ddi/README.md`:
  * New "Constructor injection" section covering the Phase 3.2 surface (`@Inject` on a single constructor, recursive parameter resolution, `@Named` / `@Qualifier` on parameters, `setAccessible(true)` for non-public constructors).
  * "Current development status" refreshed for the completed phase-2 and phase-3.2 work plus the still-open phase-3 items.

---

## Reactor verification

`./mvnw clean verify` (Maven 4.0.0-rc-5, JDK 26):

| Module | Tests | Failures | SpotBugs findings | SBOM |
|----------------------|-------|----------|-------------------|------|
| core | 0 | 0 | 0 | ✓ |
| core-properties | 0 | 0 | 0 | ✓ |
| functional-reactive | 304 | 0 | 0 | ✓ |
| ddi | 124 | 0 | 0 | ✓ |
| parent (aggregator) | — | — | — | ✓ |

PIT mutation coverage on `ddi`: **311 / 324 (96 %)**, line coverage 95 %, test strength 96 %, 1 NO_COVERAGE (the long-standing PIT mapping quirk on `matchesAllQualifiers`).

---

## Commit log

```
HEAD    release 06.02.01                                                          (to land)
a086a17 build: enable CycloneDX SBOM generation in the default lifecycle
4396d73 core: optimize HasLogger; clean up and delete legacy logger-adapter; bump dependencies
985e74d security: bump tools.jackson.core jackson-core / jackson-databind 3.1.0 → 3.1.1
07d8bdf housekeeping: untrack .DS_Store files and fix .gitignore typo
c9c6263 prepare 06.02.01-SNAPSHOT
```
