#!/usr/bin/env bash
set -euo pipefail

BRANCH="${1:-${GITHUB_HEAD_REF:-${GITHUB_REF_NAME:-}}}"

if [[ -z "$BRANCH" ]]; then
  echo "Branch name not available" >&2
  exit 1
fi

# Branches long-lived / di integrazione: non li forziamo a seguire il naming feature/*
if [[ "$BRANCH" =~ ^(main|develop|master)$ ]] || [[ "$BRANCH" =~ ^release\/[^/]+$ ]]; then
  echo "Branch '$BRANCH' allowed as long-lived branch"
  exit 0
fi

# eccezioni comuni automatiche
if [[ "$BRANCH" =~ ^(dependabot|renovate)/ ]]; then
  echo "Bot branch allowed"
  exit 0
fi

if [[ ! "$BRANCH" =~ ^(feature|bugfix|hotfix|chore)\/[a-z0-9._-]+$ ]]; then
  echo "Branch name non conforme allo standard: $BRANCH" >&2
  exit 1
fi

echo "Branch '$BRANCH' conforms to the standard"

