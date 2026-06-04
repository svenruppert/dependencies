#!/usr/bin/env bash
#
# Builds the Central upload bundle from Maven 4's `target/project-local-repo/`.
# Rewrites the dotted groupId path into the slashed layout Central expects,
# strips the Maven-4 consumer POMs, regenerates `.md5` + `.sha1` checksums,
# and zips the result as `target/central-publishing/central-bundle.zip`.
#
# Background: under Maven 4 + central-publishing-maven-plugin v0.10.0 the
# plugin's `target/central-staging/` directory ends up empty — the lifecycle
# extension hooks no longer fire ("No files to stage for artifact"). Maven 4
# does, however, always populate `target/project-local-repo/` with every
# signed artefact, classifier attachment and `.asc` signature. We rebuild the
# bundle from there.
#
# Two corrections happen on the way out:
#   1. groupId path is rewritten from `com.svenruppert/` (Maven-4 local-repo
#      layout) to `com/svenruppert/` (Maven Central / repository layout).
#   2. `*-consumer.pom*` files (the Maven-4 consumer POM classifier and its
#      `.asc` signature) are dropped — Central rejects them with
#      "Failed to associate file with coordinates".
#
# Prerequisites: run with `<skipPublishing>true</skipPublishing>` set on the
# central-publishing-maven-plugin, e.g.
#
#     ./mvnw -P_deploy,_release_sign-artifacts,_release_prepare clean deploy
#
# Then run this script. The resulting bundle is uploaded manually via
# https://central.sonatype.com/publishing → Publish Component.

set -euo pipefail

REPO_ROOT=$(cd "$(dirname "$0")/.." && pwd)
SOURCE="$REPO_ROOT/target/project-local-repo"
STAGING="$REPO_ROOT/target/central-staging"
BUNDLE_DIR="$REPO_ROOT/target/central-publishing"
BUNDLE="$BUNDLE_DIR/central-bundle.zip"

if [ ! -d "$SOURCE" ]; then
    echo "ERROR: $SOURCE does not exist. Run 'mvn deploy' first." >&2
    exit 1
fi

# Wipe and recreate the staging tree from project-local-repo. groupId path
# is rewritten by splitting on the first slash, replacing dots with slashes
# in the leading segment only.
rm -rf "$STAGING"
mkdir -p "$STAGING"

copied=0
skipped=0
while IFS= read -r -d '' file; do
    rel="${file#"$SOURCE"/}"
    case "$rel" in
        *-consumer.pom|*-consumer.pom.asc|*maven-metadata*)
            skipped=$((skipped + 1))
            continue
            ;;
    esac
    first_seg="${rel%%/*}"
    rest="${rel#*/}"
    slashed_seg=$(printf '%s' "$first_seg" | tr '.' '/')
    dest="$STAGING/$slashed_seg/$rest"
    mkdir -p "$(dirname "$dest")"
    cp "$file" "$dest"
    copied=$((copied + 1))
done < <(find "$SOURCE" -type f -print0)

echo "Copied $copied files into $STAGING (skipped $skipped consumer-POM / metadata files)."

# Generate .md5 + .sha1 for every artefact and signature. Central validates
# both on upload; missing checksums trigger "Validation failed".
checksummed=0
while IFS= read -r -d '' file; do
    case "$file" in
        *.md5|*.sha1) continue ;;
    esac
    md5 -q "$file" > "$file.md5"
    shasum -a 1 "$file" | awk '{print $1}' > "$file.sha1"
    checksummed=$((checksummed + 1))
done < <(find "$STAGING" -type f -print0)

echo "Generated md5 + sha1 checksums for $checksummed files."

mkdir -p "$BUNDLE_DIR"
rm -f "$BUNDLE" "$STAGING/central-bundle.zip"

(cd "$STAGING" && zip -qr "$BUNDLE" . -x "central-bundle.zip")
ls -lh "$BUNDLE"

cat <<EOF

Bundle ready: $BUNDLE

Next steps:
  1. Open https://central.sonatype.com/publishing
  2. Click 'Publish Component'
  3. Upload the bundle above
  4. Wait for validation, then publish
EOF
