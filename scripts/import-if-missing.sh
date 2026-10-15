#!/usr/bin/env bash
set -euo pipefail
if [[ -f app/src/main/AndroidManifest.xml ]]; then
  echo "AURIA source is already present. Skipping import."
  exit 0
fi
echo "Preparing complete Levyra source in AURIA..."
bash scripts/import-original.sh
