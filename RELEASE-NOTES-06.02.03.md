# SRU Dependencies — Release Notes 06.02.03

**Release date:** 2026-07-02
**Previous release:** 06.02.02

---

## Highlights

- **Maintenance release — no source-level API changes.** A dependency and
  plugin refresh, the migration of the deploy infrastructure to the Sonatype
  Central Portal endpoints, and the adoption of SpotBugs 4.10.2 with two
  behaviour-preserving internal fixes it surfaced.
- **Deploy infrastructure moved to the Central Portal.** The legacy OSSRH host
  `s01.oss.sonatype.org` was decommissioned on 2025-06-30. Releases and
  snapshots now resolve against `central.sonatype.com`; `distributionManagement`
  uses the Central Portal repository ids `central` / `central-snapshots`.
- **SpotBugs raised to 4.10.2.** Two new detector families ship with it; both
  findings against the reactor were fixed at source rather than excluded — see
  *Build & quality* below.
- **BOM modernisation.** Several managed entries move to their maintained
  successors (HttpClient 5, Jakarta Servlet, `org.dom4j:dom4j`, Datafaker) and
  every pre-release pin is replaced by the latest stable version. The archived
  OpenTracing artefacts are dropped. See *Managed-dependency modernisation*.
- **`slf4j-simple` is no longer a transitive compile dependency.** The SLF4J
  binding moves to `test` scope in the parent — published libraries now ship
  only `slf4j-api`; consumers bring their own binding.
- **Licence headers aligned with EUPL 1.2.** The generated file headers claimed
  EUPL 1.1; a custom licence descriptor under `src/license/` brings all 194
  source files in line with the declared EUPL 1.2.

There are **no source-level API changes**. Consumers of the four modules
upgrade from 06.02.02 to 06.02.03 without code changes. Consumers that import
this POM as a BOM should review *Managed-dependency modernisation* below —
several managed coordinates moved to successor artefacts and the OpenTracing
entries are gone.

---

## Dependency & plugin refresh

All version moves are in the parent POM's central `dependencyManagement` /
`pluginManagement`, so every module inherits them.

| Artefact | 06.02.02 | 06.02.03 |
|---|---|---|
| `com.fasterxml.jackson.core:jackson-core` / `jackson-databind`, `jackson-datatype-jdk8` | 2.21.3 | 2.22.0 |
| `tools.jackson.core:jackson-core` / `jackson-databind` | 3.1.3 | 3.2.0 |
| `com.github.spotbugs:spotbugs-annotations` | 4.9.8 | 4.10.2 |
| `com.zaxxer:HikariCP` | 7.0.2 | 7.1.0 |
| `net.bytebuddy:byte-buddy` | 1.18.8-jdk5 | 1.18.10-jdk5 |
| `org.javassist:javassist` | 3.31.0-GA | 3.32.0-GA |
| `org.pitest:pitest` | 1.25.0 | 1.25.5 |
| `com.puppycrawl.tools:checkstyle` | 13.4.2 | 13.7.0 |
| `com.github.spotbugs:spotbugs-maven-plugin` | 4.9.8.3 | 4.10.2.0 |
| `org.apache.maven.plugins:maven-dependency-plugin` | 3.10.0 | 3.11.0 |
| `org.cyclonedx:cyclonedx-maven-plugin` | 2.9.1 | 2.9.2 |
| `org.sonatype.central:central-publishing-maven-plugin` | 0.10.0 | 0.11.0 |

The Maven core plugins offering only `4.0.0-beta` / `3.6.0-M1` pre-releases
(clean, compiler, deploy, install, jar, resources, source, surefire, failsafe)
were deliberately **not** bumped — they stay on their current stable lines until
a stable successor ships.

---

## Managed-dependency modernisation (BOM consumers)

These changes affect only the managed entries in `dependencyManagement` — none
of the four modules uses the artefacts concerned. Projects that import this POM
as a BOM and rely on one of these entries need to adjust their coordinates.

### Coordinates moved to successor artefacts

| Managed entry (06.02.02) | Replacement (06.02.03) |
|---|---|
| `org.apache.httpcomponents:httpclient` 4.5.14 | `org.apache.httpcomponents.client5:httpclient5` 5.6.1 |
| `org.apache.httpcomponents:fluent-hc` 4.5.14 | `org.apache.httpcomponents.client5:httpclient5-fluent` 5.6.1 |
| `javax.servlet:javax.servlet-api` 4.0.1 | `jakarta.servlet:jakarta.servlet-api` 6.1.0 |
| `dom4j:org.dom4j` 2.1.1 (defunct coordinates) | `org.dom4j:dom4j` 2.1.4 |
| `com.github.javafaker:javafaker` 1.0.2 (abandoned) | `net.datafaker:datafaker` 2.4.3 |

HttpClient 5 (`org.apache.hc.client5.*`) and Jakarta Servlet
(`jakarta.servlet.*`) are API-incompatible with their predecessors — consumers
switching along with the BOM migrate their imports.

### Removed managed entries

* **OpenTracing** — all six artefacts (`opentracing-api`, `opentracing-util`,
  `jdbi-opentracing`, `opentracing-cdi`, `opentracing-jdbc`,
  `opentracing-jaxrs2`). The project is archived and superseded by
  OpenTelemetry; the contrib artefacts had been frozen for years.
* **`org.cache2k:cache2k-base-bom` 1.9.1.Alpha** — the artefact does not exist
  in the cache2k 2.x line; `cache2k-jcache` 2.6.1.Final stays managed.

### Pre-release pins replaced by stable versions

| Artefact | 06.02.02 | 06.02.03 |
|---|---|---|
| `org.slf4j:slf4j-api` / `slf4j-simple` / `slf4j-ext` | 2.1.0-alpha1 | 2.0.17 |
| `org.apache.logging.log4j:log4j-api` / `log4j-core` / `log4j-1.2-api` | 3.0.0-beta2 / beta3 (mixed) | 2.26.0 |
| `com.fasterxml.jackson.core:jackson-annotations` | 3.0-rc5 | 2.22 |
| `org.assertj:assertj-core` | 4.0.0-M1 | 3.27.3 |

`jackson-annotations` 2.22 restores consistency with the managed
`jackson-databind` 2.22.0 line (3.0-rc5 would have been forced onto 2.x
consumers). The SLF4J and Log4j versions are now fed by the `slf4j.version` /
`log4j.version` properties instead of hard-coded pre-release literals.

Known trade-off: `assertj-core` 3.27.3 carries CVE-2026-24400 (XXE via
`isXmlEqualTo`, CVSS 7.3) with no fixed 3.x release available; the artefact is
test-scoped and below the OWASP gate (`failBuildOnCVSS` 8). Revisit when
AssertJ 4.0.0 goes stable.

### `slf4j-simple` scope change

In the parent's shared `<dependencies>`, `slf4j-simple` moves from compile to
**`test` scope**. A published library must not force an SLF4J binding onto its
consumers; downstream projects that silently relied on the transitive binding
now declare their own.

---

## Licence & POM hygiene

* **EUPL 1.2 file headers.** `license-maven-plugin` ships no EUPL 1.2
  descriptor, so the generated headers still carried the EUPL 1.1 text while
  the project declares EUPL 1.2. A custom licence definition
  (`src/license/licenses.properties` + `eupl_v1_2/header.txt` +
  `license.txt` with the official SPDX text) plus a `licenseResolver` pointing
  at it regenerates the headers of all 194 Java sources with the correct
  EUPL 1.2 notice. Trailing whitespace introduced by the regeneration was
  stripped so the project's own Checkstyle gate stays green.
* **Checkstyle excludes fixed.** The validate execution listed ten separate
  `<excludes>` elements; Maven overwrites same-named parameter elements, so
  only the last one was honoured. All patterns now live in a single
  comma-separated element — the node/generated-sources excludes are effective
  again.
* **Dead configuration removed.** The non-existent
  `org.apache.maven.plugins:maven-exec-plugin` pluginManagement entry, the
  retired `maven-repository-plugin` (2.4, last released 2014), the unused
  `jacoco.version` / `opentracing-api.version` properties and roughly 150
  lines of commented-out ballast (legacy deploy URLs, jcip/jsr305 blocks, the
  old versions-plugin rule set) are gone.

---

## Deploy: migration to the Sonatype Central Portal

The OSSRH staging host `s01.oss.sonatype.org` was shut down on 2025-06-30. The
parent POM is updated accordingly:

- `deploy-repo-url` → `https://central.sonatype.com/api/v1/publisher/upload`
- `deploy-repo-snapshots-url` → `https://central.sonatype.com/repository/maven-snapshots/`
- `distributionManagement` repository ids renamed to `central` (releases) and
  `central-snapshots` (snapshots), matching the Central Portal convention so the
  credentials under `~/.m2/settings.xml` are picked up by both the
  `central-publishing-maven-plugin` (releases) and the standard
  `maven-deploy-plugin` (snapshots).

The Maven 4 consumer-POM workaround documented for the `_deploy` profile is
**unchanged** by this release: `central-publishing-maven-plugin` is bumped to
0.11.0, but whether 0.11.0 resolves the `-consumer.pom` classifier rejection is
to be re-verified at deploy time. Until confirmed, the manual bundle flow
(`scripts/clean-bundle-for-central.sh` → upload via
[central.sonatype.com/publishing](https://central.sonatype.com/publishing))
remains the working path.

---

## Build & quality

SpotBugs 4.10.2 introduces two new detectors that flagged existing code. Both
were fixed at source — no detector was excluded — so the new checks stay active
for future code.

### `functional-reactive` — `Case` constructor no longer throws (`CT_CONSTRUCTOR_THROW`)

`com.svenruppert.functional.result.matcher.Case` is a non-final class (its
`DefaultCase` subclass extends it) whose constructor validated its arguments via
`Objects.requireNonNull`. A throwing constructor on a subclassable class is open
to finalizer attacks. The null-checks moved into the static factory methods
(`matchCase(...)`); the constructor is now a plain field assignment. Public
behaviour is identical — `matchCase(null, …)` / `matchCase((Supplier) null)`
still throw `NullPointerException` with the same messages.

### `ddi` — `DIContainer` uses a private monitor (`USO_UNSAFE_METHOD_SYNCHRONIZATION`)

`DIContainer` is a singleton exposed via the static `global()` accessor, and its
eleven lifecycle / activation methods (`bootstrap`, `clearReflectionModel`, the
four `activatePackages` overloads, the three `activateDI` overloads,
`cleanUpScopes`, `reInitAllScopes`) synchronized on `this`. External callers
holding `DIContainer.global()` could therefore contend for — or deadlock on —
the container's intrinsic lock. The eleven methods now synchronize on a private
`final Object lock` instead. Mutual exclusion among them (including the existing
re-entrant nesting `activateDI → bootstrap` and `clearReflectionModel →
reInitAllScopes`) is preserved; the container's lock is simply no longer
reachable from outside.

---

## Verification

Reactor build on JDK 26 / Maven 4, `clean verify` with the SpotBugs gate active:

| Module | Tests | SpotBugs |
|---|---|---|
| `core` | 16 / 16 green | 0 findings |
| `core-properties` | 4 / 4 green | 0 findings |
| `functional-reactive` | 312 / 312 green (1 skipped) | 0 findings |
| `ddi` | 124 / 124 green | 0 findings |

`BUILD SUCCESS` across the full reactor.
