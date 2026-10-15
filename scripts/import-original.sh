#!/usr/bin/env bash
# Import the complete, unmodified public Levyra source into AURIA.
# Run in GitHub Codespaces, Linux/macOS or Termux (git + tar required).
set -euo pipefail
SOURCE="https://github.com/LUC4N3X/Levyra-deepsound.git"
DEST="https://github.com/cristiancutizaca/AURIA.git"
TEMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TEMP_DIR"' EXIT

git clone --depth=1 --branch main "$SOURCE" "$TEMP_DIR/levyra"
git clone "$DEST" "$TEMP_DIR/auria"

# Keep AURIA's own git history, while importing all Levyra project files.
tar -C "$TEMP_DIR/levyra" --exclude="./.git" -cf - . | tar -C "$TEMP_DIR/auria" -xf -

cd "$TEMP_DIR/auria"
git add -A
if git diff --cached --quiet; then
  echo "No changes to import"
  exit 0
fi
git -c user.name="AURIA Developer" -c user.email="179149340+cristiancutizaca@users.noreply.github.com" commit -m "chore: import Levyra source as AURIA base"
git push origin main
printf '\nSource uploaded: https://github.com/cristiancutizaca/AURIA\n'
