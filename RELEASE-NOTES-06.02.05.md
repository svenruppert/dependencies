# SRU Dependencies — Release Notes 06.02.05

**Release date:** 2026-08-31
**Previous release:** 06.02.04

---

## Highlights

- **Dependency refresh, stable releases only.** The managed dependency set
  moves to the latest stable releases across the board — with two deliberate
  exceptions in the logging stack (below).
- **Testcontainers pair repaired.** 06.02.04 managed Testcontainers core
  2.0.5 next to `org.testcontainers:selenium` 1.21.4, a module that only
  exists on the 1.x line. The Selenium module now uses the Testcontainers 2.x
  coordinates and version, consistent with the managed core.
- **Infrastructure-only release.** No source-level changes; the four modules
  are re-released so every artefact line carries the refreshed managed set.

There are **no code changes**. The only migration concern for consumers is
the Testcontainers coordinate change below.

---

## Managed dependency updates

| Dependency | 06.02.04 | 06.02.05 |
|---|---|---|
| `com.github.spotbugs:spotbugs-annotations` | 4.10.2 | 4.10.4 |
| `org.javassist:javassist` | 3.32.0-GA | 3.33.0-GA |
| `net.bytebuddy:byte-buddy` | 1.18.10-jdk5 | 1.18.12-jdk5 |
| `tools.jackson.core:jackson-core` / `jackson-databind` | 3.2.0 | 3.2.2 |
| `com.fasterxml.jackson.core:jackson-core` / `jackson-databind` | 2.22.0 | 2.22.2 |
| `com.fasterxml.jackson.datatype:jackson-datatype-jdk8` | 2.22.0 | 2.22.2 |
| `commons-codec:commons-codec` | 1.22.0 | 1.22.1 |
| `org.apache.httpcomponents.client5:httpclient5` / `httpclient5-fluent` | 5.6.1 | 5.6.4 |
| `org.dom4j:dom4j` | 2.1.4 | 2.2.0 |
| `org.testcontainers:selenium` → `org.testcontainers:testcontainers-selenium` | 1.21.4 | 2.0.5 |
| `org.assertj:assertj-core` | 3.27.3 | 3.27.7 |
| `io.rest-assured:rest-assured` | 6.0.0 | 6.0.1 |
| `net.datafaker:datafaker` | 2.4.3 | 2.7.0 |

`com.fasterxml.jackson.core:jackson-annotations` stays at **2.22** on
purpose: it is the newest release of the Jackson 2.x annotations line (which
carries no patch digit since 2.20) and belongs to the managed 2.22.x
core/databind pair. The 3.x annotations belong to the `tools.jackson` line.

---

## Consumer-visible coordinate change: Testcontainers Selenium

Testcontainers 2.x renamed its modules; the Selenium module moved from
`org.testcontainers:selenium` (ends at 1.21.4) to
`org.testcontainers:testcontainers-selenium`. The managed entry follows that
rename and now pins **2.0.5**, matching the managed
`org.testcontainers:testcontainers` core.

Consumers that reference `org.testcontainers:selenium` must switch the
`artifactId` to `testcontainers-selenium`; the old coordinates are no longer
managed by this parent.

---

## Logging stack — deliberate pre-release pins

| Property | 06.02.04 | 06.02.05 |
|---|---|---|
| `slf4j.version` | 2.0.17 | 2.1.0-alpha1 |
| `log4j.version` | 2.26.0 | 3.0.0-beta3 |

These two are the only pre-releases in the managed set: the managed Jackson
dependencies require the newer logging line. A comment in the parent POM
records the constraint so future version-update passes do not downgrade the
pair to the stable 2.0.x / 2.2x.x releases again.

---

## Build chain

| Component | 06.02.04 | 06.02.05 |
|---|---|---|
| PIT (`pitest-maven` + `org.pitest:pitest`) | 1.25.5 | 1.30.0 |
| JUnit Jupiter / Platform | 6.1.1 | 6.1.3 |
| Checkstyle (checker under `maven-checkstyle-plugin`) | 13.7.0 | 14.1.0 |

Housekeeping: `.gitignore` now keeps local Claude session state
(`/CLAUDE.md`, `/.claude/`) out of the repository.

---

## Verification

- Reactor `clean verify` on JDK 26 / Maven 4.0.0-rc-5 (tests, Checkstyle,
  SpotBugs — 0 findings, licence check, CycloneDX SBOMs): **BUILD SUCCESS**,
  all five reactor entries green (2026-08-31).
- Every changed version was verified to exist on Maven Central via its
  `maven-metadata.xml` before adoption; `./mvnw -N dependency:resolve` green
  on the corrected set.
- The full POM was scanned for pre-release qualifiers: none remain besides
  the two documented logging pins (and the Maven-4-only milestone line of
  `maven-site-plugin`, unchanged legacy).
