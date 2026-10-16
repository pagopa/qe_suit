#!/usr/bin/env python3
"""Build a compact per-run report from Copilot CLI OpenTelemetry JSONL."""

from __future__ import annotations

import argparse
import json
import os
import re
from datetime import datetime, timezone
from pathlib import Path
from typing import Any


def _otel_value(value: Any) -> Any:
    if not isinstance(value, dict):
        return value
    scalar_keys = (
        "stringValue",
        "intValue",
        "doubleValue",
        "boolValue",
        "string_value",
        "int_value",
        "double_value",
        "bool_value",
    )
    for key in scalar_keys:
        if key in value:
            raw = value[key]
            if key in ("intValue", "int_value"):
                try:
                    return int(raw)
                except (TypeError, ValueError):
                    return raw
            return raw
    array = value.get("arrayValue") or value.get("array_value")
    if isinstance(array, dict):
        return [_otel_value(item) for item in array.get("values", [])]
    return value


def _attributes(raw: Any) -> dict[str, Any]:
    if isinstance(raw, dict):
        return {str(key): _otel_value(value) for key, value in raw.items()}
    if not isinstance(raw, list):
        return {}
    result = {}
    for item in raw:
        if not isinstance(item, dict) or "key" not in item:
            continue
        result[str(item["key"])] = _otel_value(item.get("value"))
    return result


def _walk(value: Any):
    if isinstance(value, dict):
        yield value
        for child in value.values():
            yield from _walk(child)
    elif isinstance(value, list):
        for child in value:
            yield from _walk(child)


def _load_jsonl(path: Path) -> list[Any]:
    if not path.exists():
        return []
    records = []
    for line in path.read_text(errors="replace").splitlines():
        if not line.strip():
            continue
        try:
            records.append(json.loads(line))
        except json.JSONDecodeError:
            continue
    return records


def _number(value: Any) -> int | float | None:
    if isinstance(value, bool):
        return None
    if isinstance(value, (int, float)):
        return value
    try:
        return float(value) if "." in str(value) else int(value)
    except (TypeError, ValueError):
        return None


def _sum(attributes: list[dict[str, Any]], key: str) -> int | float | None:
    values = [_number(item.get(key)) for item in attributes]
    numbers = [value for value in values if value is not None]
    return sum(numbers) if numbers else None


def _sum_first(
    attributes: list[dict[str, Any]], *keys: str
) -> int | float | None:
    for key in keys:
        value = _sum(attributes, key)
        if value is not None:
            return value
    return None


def _distinct(values: list[Any]) -> list[str]:
    result = []
    for value in values:
        if value in (None, ""):
            continue
        text = str(value)
        if text not in result:
            result.append(text)
    return result


def extract_telemetry(path: Path) -> dict[str, Any]:
    records = _load_jsonl(path)
    spans = []
    for record in records:
        for candidate in _walk(record):
            attrs = _attributes(candidate.get("attributes"))
            if "gen_ai.operation.name" in attrs:
                spans.append((candidate, attrs))

    invocations = [
        (span, attrs)
        for span, attrs in spans
        if attrs.get("gen_ai.operation.name") == "invoke_agent"
    ]
    top_level = [
        attrs
        for span, attrs in invocations
        if span.get("parentSpanId", span.get("parent_span_id")) in (None, "")
    ]
    if not top_level:
        top_level = [attrs for _, attrs in invocations if attrs.get("server.address")]
    status = "complete"
    selected = top_level
    if not selected and invocations:
        selected = [attrs for _, attrs in invocations]
        status = "fallback_all_invocations"
    if not selected:
        status = "unavailable" if not records else "no_agent_spans"

    chat_spans = [
        attrs
        for _, attrs in spans
        if attrs.get("gen_ai.operation.name") == "chat"
    ]
    nano_aiu = _sum(selected, "github.copilot.nano_aiu")
    ai_units = _sum(selected, "github.copilot.aiu")
    if ai_units is None and nano_aiu is not None:
        ai_units = nano_aiu / 1_000_000_000
    return {
        "telemetry_status": status,
        "requested_models": _distinct(
            [attrs.get("gen_ai.request.model") for attrs in selected]
        ),
        "resolved_models": _distinct(
            [attrs.get("gen_ai.response.model") for attrs in chat_spans]
        ),
        "input_tokens": _sum(selected, "gen_ai.usage.input_tokens"),
        "output_tokens": _sum(selected, "gen_ai.usage.output_tokens"),
        "cache_read_input_tokens": _sum(
            selected, "gen_ai.usage.cache_read.input_tokens"
        ),
        "cache_creation_input_tokens": _sum_first(
            selected,
            "gen_ai.usage.cache_creation.input_tokens",
            "gen_ai.usage.cache_write.input_tokens",
        ),
        "turn_count": _sum(selected, "github.copilot.turn_count"),
        "cost": _sum(selected, "github.copilot.cost"),
        "ai_units": ai_units,
    }


def _read_text(path: Path | None) -> str | None:
    if path is None or not path.exists():
        return None
    value = path.read_text(errors="replace").strip()
    return value or None


def _issue(path: Path | None) -> dict[str, Any]:
    if path is None or not path.exists():
        return {"jira_key": None, "title": None, "url": None}
    try:
        issue = json.loads(path.read_text())
    except (json.JSONDecodeError, OSError):
        return {"jira_key": None, "title": None, "url": None}
    title = issue.get("title") or ""
    match = re.match(r"^\[([A-Z][A-Z0-9_]*-[0-9]+)\]", title)
    return {
        "jira_key": match.group(1) if match else None,
        "title": title or None,
        "url": issue.get("url"),
    }


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--telemetry", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--issue", type=Path)
    parser.add_argument("--cli-version", type=Path)
    parser.add_argument("--started-at", type=Path)
    parser.add_argument("--finished-at", type=Path)
    args = parser.parse_args()

    repository = os.environ.get("GITHUB_REPOSITORY")
    run_id = os.environ.get("GITHUB_RUN_ID")
    server_url = os.environ.get("GITHUB_SERVER_URL", "https://github.com")
    report = {
        "schema_version": 1,
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "github": {
            "repository": repository,
            "run_id": int(run_id) if run_id and run_id.isdigit() else run_id,
            "run_attempt": int(os.environ.get("GITHUB_RUN_ATTEMPT", "1")),
            "run_url": (
                f"{server_url}/{repository}/actions/runs/{run_id}"
                if repository and run_id
                else None
            ),
            "workflow": os.environ.get("GITHUB_WORKFLOW"),
            "issue_number": os.environ.get("ISSUE_NUMBER"),
            "started_at": _read_text(args.started_at),
            "finished_at": _read_text(args.finished_at),
        },
        "issue": _issue(args.issue),
        "copilot": {
            "cli_version": _read_text(args.cli_version),
            "configured_model": os.environ.get("COPILOT_REQUESTED_MODEL"),
            **extract_telemetry(args.telemetry),
        },
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(report, indent=2, ensure_ascii=False) + "\n")


if __name__ == "__main__":
    main()
