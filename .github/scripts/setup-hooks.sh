#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel)"
cd "$REPO_ROOT"

git config --local core.hooksPath .githooks
chmod +x .githooks/commit-msg .githooks/pre-push

echo "Git hooks configured: core.hooksPath=.githooks"