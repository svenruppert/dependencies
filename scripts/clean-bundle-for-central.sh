#!/usr/bin/env bash
#
# Strips Maven-4 consumer POMs from `target/central-staging/`, regenerates the
# `central-bundle.zip`, and prints the upload URL.
#
# Background: central-publishing-maven-plugin v0.10.0 emits the Maven-4 consumer
# POM as a `consumer`-classified artefact (`*-consumer.pom`), which the
# Sonatype Central Portal validator rejects with
# "Failed to associate file with coordinates". This script removes those files
# from the staged tree and re-zips the bundle that `mvn deploy` produced.
#
# Prerequisites: run with `<skipPublishing>true</skipPublishing>` set on the
# plugin, e.g.
#
#     ./mvnw -P_deploy,_release_sign-artifacts,_release_prepare clean deploy
#
# Then run this script. The resulting bundle is uploaded manually via
# https://central.sonatype.com/publishing → Publish Component.

set -euo pipefail

REPO_ROOT=$(cd "$(dirname "$0")/.." && pwd)
STAGING="$REPO_ROOT/target/central-staging"
BUNDLE_DIR="$REPO_ROOT/target/central-publishing"
BUNDLE="$BUNDLE_DIR/central-bundle.zip"

if [ ! -d "$STAGING" ]; then
    echo "ERROR: $STAGING does not exist. Run 'mvn deploy' first." >&2
    exit 1
fi

removed=$(find "$STAGING" -name "*-consumer.pom*" -print -delete | wc -l | tr -d ' ')
echo "Removed $removed consumer-POM files from staging."

mkdir -p "$BUNDLE_DIR"
rm -f "$BUNDLE" "$STAGING/central-bundle.zip"

(cd "$STAGING" && zip -qr "$BUNDLE" . -x "central-bundle.zip" -x "*maven-metadata-central-staging*")
ls -lh "$BUNDLE"

cat <<EOF

Bundle ready: $BUNDLE

Next steps:
  1. Open https://central.sonatype.com/publishing
  2. Click 'Publish Component'
  3. Upload the bundle above
  4. Wait for validation, then publish
EOF
