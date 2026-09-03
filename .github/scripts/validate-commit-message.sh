#!/usr/bin/env bash
set -euo pipefail

MSG="${1:-}"

if [[ -z "$MSG" ]]; then
  echo "Commit message not provided" >&2
  exit 1
fi

# Ignore auto-generated merge commits.
if [[ "$MSG" =~ ^Merge[[:space:]] ]]; then
  exit 0
fi

COMMIT_REGEX='^(feat|feature|fix|chore|docs|refactor|test|ci)(\([^)]*\))?[[:space:]]*:[[:space:]]+\[[A-Z][A-Z0-9]*-[0-9]+\][[:space:]]+.+$'

if [[ ! "$MSG" =~ $COMMIT_REGEX ]]; then
  echo "" >&2
  echo "========================================" >&2
  echo "Invalid commit message format" >&2
  echo "========================================" >&2
  echo "Message: '$MSG'" >&2
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
