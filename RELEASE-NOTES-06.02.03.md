# SRU Dependencies — Release Notes 06.02.03

**Release date:** 2026-06-24
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

There are **no breaking changes**. Consumers can upgrade from 06.02.02 to
06.02.03 without code changes.

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
| `com.puppycrawl.tools:checkstyle` | 13.4.2 | 13.6.0 |
| `com.github.spotbugs:spotbugs-maven-plugin` | 4.9.8.3 | 4.10.2.0 |
| `org.apache.maven.plugins:maven-dependency-plugin` | 3.10.0 | 3.11.0 |
| `org.cyclonedx:cyclonedx-maven-plugin` | 2.9.1 | 2.9.2 |
| `org.sonatype.central:central-publishing-maven-plugin` | 0.10.0 | 0.11.0 |

The Maven core plugins offering only `4.0.0-beta` / `3.6.0-M1` pre-releases
(clean, compiler, deploy, install, jar, resources, source, surefire, failsafe)
were deliberately **not** bumped — they stay on their current stable lines until
a stable successor ships.

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
