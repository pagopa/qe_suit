#!/usr/bin/env bash
set -euo pipefail

RANGE="${1:-}"

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
      # Push normale: valida solo i commit inclusi nel push
      RANGE="$BEFORE_SHA..$AFTER_SHA"
    else
      # Branch nuovo / primo push:
      # non usare semplicemente "$AFTER_SHA", perché includerebbe tutta la history.
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

echo "Validating commits in range: $RANGE"

COMMITS_FOUND=false

while IFS= read -r msg || [[ -n "$msg" ]]; do
  if [[ -z "$msg" ]]; then
    continue
  fi

  COMMITS_FOUND=true

  if [[ ! "$msg" =~ ^(feat|fix|chore|docs|refactor|test|ci)(\([^)]*\))?[[:space:]]*:[[:space:]]+\[[A-Z][A-Z0-9]*-[0-9]+\][[:space:]]+.+$ ]]; then
    echo "" >&2
    echo "========================================" >&2
    echo "Invalid commit message format" >&2
    echo "========================================" >&2
    echo "Message: $msg" >&2
    echo "" >&2
    echo "Expected formats:" >&2
    echo "  type(scope): [TICKET-ID] message" >&2
    echo "  type(scope) : [TICKET-ID] message" >&2
    echo "  type: [TICKET-ID] message" >&2
    echo "  type : [TICKET-ID] message" >&2
    echo "" >&2
    echo "Examples:" >&2
    echo "  feat(payment): [QA-123] add retry on timeout" >&2
    echo "  fix : [QA-15533] fix title assertion" >&2
    echo "========================================" >&2
    exit 1
  fi

done < <(git log --no-merges --pretty=format:%s "$RANGE")

if [[ "$COMMITS_FOUND" == false ]]; then
  echo "No commits to validate in range: $RANGE"
  exit 0
fi

echo "Commit messages conform to the standard"