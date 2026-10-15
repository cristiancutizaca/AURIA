#!/usr/bin/env bash
# Import all original Levyra files ONLY into the AURIA import branch.
# Use GitHub Codespaces (or any authenticated Linux/macOS git shell).
set -euo pipefail
SOURCE="https://github.com/LUC4N3X/Levyra-deepsound.git"
DEST="https://github.com/cristiancutizaca/AURIA.git"
BRANCH="import/levyra-source"
TEMPDIR="$(mktemp -d)"
trap 'rm -rf "$TEMPDIR"' EXIT

git clone --depth 1 --branch main "$SOURCE" "$TEMPDIR/source"
git clone --branch "$BRANCH" "$DEST" "$TEMPDIR/auria"

# Copy source, preserving all source files, including binary images and native libraries.
# Never overwrite the target repository's git history.
tar -C "$TEMPDIR/source" --exclude='./.git' -cf - . | tar -C "$TEMPDIR/auria" -xf -
cd "$TEMPDIR/auria"
cat > AURIA_UPSTREAM.md <<'EOF'
# AURIA: upstream source and attribution

Original software: Levyra by LUC4N3X
Source: https://github.com/LUC4N3X/Levyra-deepsound

Levyra is distributed under GPL-3.0. Preserve LICENSE and original
copyright/attribution notices. This is an independently modified version.

This branch contains the imported base source for future AURIA features.
Do not merge into main until the import and Android/Windows builds are verified.
EOF
git config user.name "AURIA Source Import"
git config user.email "179149340+cristiancutizaca@users.noreply.github.com"
git add -A
if git diff --cached --quiet; then
  echo "Source was already imported; no changes."
  exit 0
fi
git commit -m "chore: import complete Levyra source into AURIA staging branch"
git push origin "HEAD:$BRANCH"
echo "Uploaded all source files to https://github.com/cristiancutizaca/AURIA/tree/$BRANCH"
