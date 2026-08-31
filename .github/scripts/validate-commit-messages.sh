#!/usr/bin/env bash
set -euo pipefail

RANGE="${1:-}"

if [[ -z "$RANGE" ]]; then
  if [[ "${GITHUB_EVENT_NAME:-}" == "pull_request" ]]; then
    BASE_SHA="${GITHUB_BASE_SHA:-}"
    HEAD_SHA="${GITHUB_HEAD_SHA:-${GITHUB_SHA:-}}"
    RANGE="$BASE_SHA..$HEAD_SHA"
  else
    BEFORE_SHA="${GITHUB_BEFORE_SHA:-${GITHUB_BEFORE:-}}"
    AFTER_SHA="${GITHUB_AFTER_SHA:-${GITHUB_SHA:-}}"

    # caso primo commit / evento non standard
    if [[ -z "$BEFORE_SHA" || "$BEFORE_SHA" == "0000000000000000000000000000000000000000" ]]; then
      RANGE="$AFTER_SHA"
    else
      RANGE="$BEFORE_SHA..$AFTER_SHA"
    fi
  fi
fi

if [[ -z "$RANGE" ]]; then
  echo "Commit range not available" >&2
  exit 1
fi

echo "Validating commits in range: $RANGE"

while IFS= read -r msg || [[ -n "$msg" ]]; do
  if [[ -z "$msg" ]]; then
    continue
  fi

  # Validazione formato conventional commit con ticket Jira
  # Formato: type(scope): [TICKET-ID] message
  # Esempio: feat(payment): [PROJ-123] add retry on timeout
  if [[ ! "$msg" =~ ^(feat|fix|chore|docs|refactor|test|ci)(\(.+\))?:\ \[[A-Z]+\-[0-9]+\]\ .+ ]]; then
    echo "" >&2
    echo "========================================" >&2
    echo "Invalid commit message format" >&2
    echo "========================================" >&2
    echo "Message: $msg" >&2
    echo "" >&2
    echo "Expected one of these formats:" >&2
    echo "  1) type(AFFECTED-SCOPE): [TICKET-ID] message" >&2
    echo "     Example: feat(payment): [QA-123] add retry on timeout" >&2
    echo "" >&2
    echo "  2) type: [TICKET-ID] message" >&2
    echo "     Example: feat: [PROJ-123] add retry on timeout" >&2
    echo "========================================" >&2
    exit 1
  fi
done < <(git log --pretty=format:%s "$RANGE")

echo "Commit messages conform to the standard"


