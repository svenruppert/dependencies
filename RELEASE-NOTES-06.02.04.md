# SRU Dependencies — Release Notes 06.02.04

**Release date:** 2026-07-02
**Previous release:** 06.02.03

---

## Highlights

- **Selectable deploy targets, inherited by every child project.** The parent
  POM now supports three deploy destinations — Maven Central (default), the
  in-house repository `repo.jsentinel.eu`, and its access-protected
  `sensitive` channels — switchable per build via profile, without touching
  any child POM.
- **Infrastructure-only release.** No source-level changes, no dependency or
  plugin moves. The four modules are re-released so that the tagged POM of
  every artefact line carries the new deploy profiles.

There are **no breaking changes** and **no code changes**. Consumers upgrade
from 06.02.03 without any migration.

---

## Deploy-target selection

`distributionManagement` is now fully parameterised: the repository ids join
the URLs as `deploy-repo-*` properties, with the Maven Central Portal
ids/endpoints as the defaults. The ids double as the credential lookup keys in
`~/.m2/settings.xml`. Two new profiles override all four properties:

| Build | Releases → | Snapshots → | Server id (settings.xml) |
|---|---|---|---|
| default / `-P_deploy` | Maven Central Portal | `central-snapshots` | `central` / `central-snapshots` |
| `-P_deploy-jsentinel` | `repo.jsentinel.eu/releases` | `repo.jsentinel.eu/snapshots` | `jsentinel-releases` / `jsentinel-snapshots` |
| `-P_deploy-jsentinel-sensitive` | `repo.jsentinel.eu/sensitive` | `repo.jsentinel.eu/sensitive-snapshots` | `jsentinel-sensitive` / `jsentinel-sensitive-snapshots` |

Usage:

```bash
# Maven Central (unchanged flow: bundle build + Central Portal upload)
./mvnw -P_deploy,_release_sign-artifacts,_release_prepare clean deploy

# in-house repository — plain maven-deploy-plugin push
./mvnw -P_deploy-jsentinel clean deploy

# access-protected channels
./mvnw -P_deploy-jsentinel-sensitive clean deploy
```

The jsentinel profiles must **not** be combined with `-P_deploy` — that
profile skips the deploy plugin and builds a Central bundle instead.

On the Reposilite side each channel is served by a dedicated, route-scoped
access token (`maven-release`, `maven-snapshot`, `sensitive-deploy`,
`sensitive-snapshots`); no deploy token carries manager rights or access to a
channel it does not serve.

---

## Verification

- Reactor `clean verify` on JDK 26 / Maven 4 (Checkstyle + SpotBugs gates
  active): BUILD SUCCESS.
- `help:effective-pom` on the parent and a child module confirms the id/URL
  resolution for all three targets, with Maven Central as the default.
- End-to-end deploy tests: `-P_deploy-jsentinel` and
  `-P_deploy-jsentinel-sensitive` each uploaded the full 06.02.04-SNAPSHOT
  reactor (jars, test-jars, CycloneDX SBOMs, merged `maven-metadata.xml`) to
  their respective channels.
