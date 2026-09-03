#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(git rev-parse --show-toplevel)"
VALIDATOR="$ROOT_DIR/.github/scripts/validate-commit-message.sh"

RANGE="${1:-}"
MAX_COMMITS="${MAX_COMMITS:-50}"

resolve_default_base_branch() {
  if [[ -n "${GITHUB_BASE_REF:-}" ]]; then
    echo "origin/${GITHUB_BASE_REF}"
    return
  fi

  if git show-ref --verify --quiet refs/remotes/origin/main; then
    echo "origin/main"
    return
  fi

  if git show-ref --verify --quiet refs/remotes/origin/master; then
    echo "origin/master"
    return
  fi

  echo ""
}

if [[ ! -f "$VALIDATOR" ]]; then
  echo "Commit message validator not found: $VALIDATOR" >&2
  exit 1
fi

if [[ -z "$RANGE" ]]; then
  if [[ "${GITHUB_EVENT_NAME:-}" == "pull_request" ]]; then
    BASE_SHA="${GITHUB_BASE_SHA:-}"
    HEAD_SHA="${GITHUB_HEAD_SHA:-${GITHUB_SHA:-HEAD}}"

    if [[ -z "$BASE_SHA" ]]; then
      echo "GITHUB_BASE_SHA is not available" >&2
      exit 1
    fi

    MERGE_BASE="$(git merge-base "$BASE_SHA" "$HEAD_SHA")"
    RANGE="$MERGE_BASE..$HEAD_SHA"
  else
    BEFORE_SHA="${GITHUB_BEFORE_SHA:-${GITHUB_BEFORE:-}}"
    AFTER_SHA="${GITHUB_AFTER_SHA:-${GITHUB_SHA:-HEAD}}"
    ZERO_SHA="0000000000000000000000000000000000000000"

    if [[ -n "$BEFORE_SHA" && "$BEFORE_SHA" != "$ZERO_SHA" ]]; then
      # Push normale: valida solo i commit inclusi nel push corrente.
      RANGE="$BEFORE_SHA..$AFTER_SHA"
    else
      # Nuovo branch / primo push:
      # evita di validare tutta la history.
      BASE_BRANCH="$(resolve_default_base_branch)"

      if [[ -z "$BASE_BRANCH" ]]; then
        echo "Unable to determine base branch" >&2
        exit 1
      fi

      MERGE_BASE="$(git merge-base "$BASE_BRANCH" "$AFTER_SHA")"
      RANGE="$MERGE_BASE..$AFTER_SHA"
    fi
  fi
fi

if [[ -z "$RANGE" ]]; then
  echo "Commit range not available" >&2
  exit 1
fi

if ! git rev-list "$RANGE" >/dev/null 2>&1; then
  echo "Invalid or unavailable commit range: $RANGE" >&2
  exit 1
fi

echo "Validating up to $MAX_COMMITS commits in range: $RANGE"

COMMITS_FOUND=false

while IFS= read -r msg || [[ -n "$msg" ]]; do
  if [[ -z "$msg" ]]; then
    continue
  fi

  COMMITS_FOUND=true

  bash "$VALIDATOR" "$msg"

done < <(
  git log \
    --no-merges \
    -n "$MAX_COMMITS" \
    --pretty=format:%s \
    "$RANGE"
)

if [[ "$COMMITS_FOUND" == false ]]; then
  echo "No commits to validate in range: $RANGE"
  exit 0
fi

echo "Commit messages conform to the standard"