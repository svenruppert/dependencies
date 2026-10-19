# SRU Dependencies — Release Notes 06.02.06

**Release date:** 2026-10-19
**Previous release:** 06.02.05

---

## Highlights

- **Dependency refresh, stable releases only.** The managed dependency set
  moves to the latest stable releases. The deliberate pre-release pins of the
  logging stack (SLF4J 2.1.0-alpha1 / Log4j 3.0.0-beta3) stay unchanged.
- **Build-plugin refresh.** Every managed build plugin with a newer stable
  release moves forward, including a major step of OWASP Dependency-Check
  (12 → 13).
- **No API changes.** The only source change removes a SpotBugs suppression
  that the newer SpotBugs reports as useless.

There is **no migration effort** for consumers.

---

## Managed dependency updates

| Dependency | 06.02.05 | 06.02.06 |
|---|---|---|
| `com.github.spotbugs:spotbugs-annotations` | 4.10.4 | 4.10.5 |
| `net.bytebuddy:byte-buddy` | 1.18.12-jdk5 | 1.18.15-jdk5 |
| `tools.jackson.core:jackson-core` / `jackson-databind` | 3.2.2 | 3.2.4 |
| `com.fasterxml.jackson.core:jackson-core` / `jackson-databind` | 2.22.2 | 2.22.4 |
| `com.fasterxml.jackson.datatype:jackson-datatype-jdk8` | 2.22.2 | 2.22.4 |
| `commons-codec:commons-codec` | 1.22.1 | 1.23.0 |
| `org.apache.commons:commons-lang3` | 3.20.0 | 3.21.0 |
| `io.rest-assured:rest-assured` | 6.0.1 | 6.2.0 |
| `io.gatling.highcharts:gatling-charts-highcharts` | 3.15.1 | 3.16.0 |

`com.fasterxml.jackson.core:jackson-annotations` stays at **2.22**, the newest
release of the Jackson 2.x annotations line.

---

## Build chain

| Component | 06.02.05 | 06.02.06 |
|---|---|---|
| `maven-compiler-plugin` | 3.15.0 | 3.16.0 |
| `maven-surefire-plugin` / `maven-failsafe-plugin` | 3.5.5 | 3.6.0 |
| `maven-install-plugin` / `maven-deploy-plugin` | 3.1.4 | 3.2.0 |
| `maven-jar-plugin` | 3.5.0 | 3.5.1 |
| `spotbugs-maven-plugin` | 4.10.2.0 | 4.10.5.0 |
| `org.apache.felix:maven-bundle-plugin` | 6.0.2 | 6.2.0 |
| `build-helper-maven-plugin` | 3.6.1 | 3.6.2 |
| `exec-maven-plugin` | 3.6.3 | 3.6.4 |
| `versions-maven-plugin` | 2.21.0 | 2.23.0 |
| `cyclonedx-maven-plugin` | 2.9.2 | 2.9.3 |
| `org.owasp:dependency-check-maven` | 12.2.2 | 13.0.0 |
| `central-publishing-maven-plugin` | 0.11.0 | 0.12.0 |
| PIT (`pitest-maven` + `org.pitest:pitest`) | 1.30.0 | 1.30.1 |
| ASM (PIT runtime) | 9.10.1 | 9.11.1 |
| Checkstyle (checker under `maven-checkstyle-plugin`) | 14.1.0 | 14.3.0 |

---

## Source change: `core`

SpotBugs 4.10.5 recognises that the `Random` in
`PortUtils.nextFreePortForTest()` is reused inside the retry loop and reports
the existing `DMI_RANDOM_USED_ONLY_ONCE` suppression as useless
(`US_USELESS_SUPPRESSION_ON_METHOD`). The annotation and its import are
removed; behaviour is unchanged.

---

## Verification

- Reactor `clean verify` on JDK 26 / Maven 4.0.0-rc-5 (tests, Checkstyle,
  SpotBugs — 0 findings, licence check, CycloneDX SBOMs): **BUILD SUCCESS**,
  all five reactor entries green (2026-10-19).
- `versions:display-plugin-updates` against `version-number-rules.xml`
  reports no further plugin updates.
