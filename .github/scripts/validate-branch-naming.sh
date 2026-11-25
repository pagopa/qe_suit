#!/usr/bin/env bash
set -euo pipefail

BRANCH="${1:-${GITHUB_HEAD_REF:-${GITHUB_REF_NAME:-}}}"

if [[ -z "$BRANCH" ]]; then
  echo "Branch name not available" >&2
  exit 1
fi

# Branch long-lived / di integrazione.
if [[ "$BRANCH" =~ ^(main|develop|master)$ ]] || [[ "$BRANCH" =~ ^release/[^/]+$ ]]; then
  echo "Branch '$BRANCH' allowed as long-lived branch"
  exit 0
fi

# Branch creati automaticamente da bot.
if [[ "$BRANCH" =~ ^(dependabot|renovate)/ ]]; then
  echo "Bot branch allowed"
  exit 0
fi

# Solo convenzione RAE attuale: rae/<namespace>/rae_<suite>_<YYYYMMDDHHMMSS>.
if [[ "$BRANCH" =~ ^rae/([A-Za-z0-9][A-Za-z0-9._-]*)/(rae_[A-Za-z0-9._-]+_[0-9]{14})$ ]]; then
  NAMESPACE="${BASH_REMATCH[1]}"
  if [[ "$NAMESPACE" != "." && "$NAMESPACE" != ".." ]]; then
    echo "RAE branch '$BRANCH' conforms to the standard"
    exit 0
  fi
fi

# Gli altri branch mantengono la convenzione a un solo livello.
# Il vecchio formato rae/rae_* non e' piu' ammesso.
if [[ ! "$BRANCH" =~ ^(feature|bugfix|hotfix|fix|chore|refactor)/[A-Za-z0-9._-]+$ ]]; then
  echo "Branch name non conforme allo standard: $BRANCH" >&2
  exit 1
fi

echo "Branch '$BRANCH' conforms to the standard"
