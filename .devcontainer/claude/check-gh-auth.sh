#!/usr/bin/env bash
set -euo pipefail

# Always force HTTPS for GitHub
git config --global url."https://github.com/".insteadOf git@github.com:  >/dev/null 2>&1 || true
git config --global url."https://github.com/".insteadOf ssh://git@github.com/ >/dev/null 2>&1 || true

# If already authenticated, exit silently
if gh auth status -h github.com >/dev/null 2>&1; then
  exit 0
fi

# If PAT provided from host, auto-login
if [[ -n "${DRAMAFINDER_PAT:-}" ]]; then
  echo "Authenticating to GitHub using DRAMAFINDER_PAT..."
  echo "$DRAMAFINDER_PAT" | gh auth login -h github.com -p https --with-token
  gh auth setup-git
  echo "GitHub authentication configured."
  exit 0
fi

cat <<'EOF'

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
GitHub auth is not configured inside this dev container.

To enable access manually:

  gh auth login -h github.com -p https
  gh auth setup-git

Or define DRAMAFINDER_PAT on your host before starting the container.
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

EOF