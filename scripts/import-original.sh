#!/usr/bin/env bash
# One-command import of the full Levyra source into import/levyra-source.
# Designed for a Codespace created on that branch.
set -euo pipefail
TARGET_BRANCH="import/levyra-source"
SOURCE_URL="https://github.com/LUC4N3X/Levyra-deepsound.git"
CURRENT_BRANCH="$(git branch --show-current)"
if [[ "$CURRENT_BRANCH" != "$TARGET_BRANCH" ]]; then
  echo "Refusing import: current branch '$CURRENT_BRANCH' is not '$TARGET_BRANCH'."
  echo "Create Codespace on '$TARGET_BRANCH' to keep main untouched."
  exit 2
fi
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
git clone --depth=1 --branch main "$SOURCE_URL" "$TMP/levyra"
UPSTREAM_SHA="$(git -C "$TMP/levyra" rev-parse HEAD)"
rsync -a --exclude='.git/' "$TMP/levyra/" "$PWD/"
cat > AURIA_UPSTREAM.md <<EOF
# AURIA and upstream credits

Original project: [Levyra](https://github.com/LUC4N3X/Levyra-deepsound).
Imported upstream commit: \`$UPSTREAM_SHA\`.

The original Levyra code is distributed under GNU GPL-3.0.
Keep the original LICENSE file, copyright notices and attribution.
AURIA is an independent modification, not affiliated with upstream.

This branch is for staging and testing the full Android/Windows source.
Merge to main only after verifying both platform builds.
EOF
git config user.name "AURIA Source Import"
git config user.email "179149340+cristiancutizaca@users.noreply.github.com"
git add -A
if git diff --cached --quiet; then
  echo "Full source already present; no new changes."
  exit 0
fi
git commit -m "chore: import full Levyra source into AURIA staging"
git push origin "HEAD:$TARGET_BRANCH"
echo "Full source imported to https://github.com/cristiancutizaca/AURIA/tree/$TARGET_BRANCH"
