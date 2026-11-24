"""Single source of truth for RAE storage namespaces.

The base history directory always contains one subdirectory per pipeline.
No legacy unscoped history is searched, even as a fallback.
"""
from __future__ import annotations

from pathlib import Path
import re

_NAMESPACE_RE = re.compile(r"[A-Za-z0-9][A-Za-z0-9_.-]*\Z")


def validate_namespace(namespace: str) -> str:
    if not isinstance(namespace, str) or not _NAMESPACE_RE.fullmatch(namespace):
        raise ValueError("Namespace RAE non valido (usa lettere, cifre, _, -, .)")
    if namespace in (".", ".."):
        raise ValueError("Namespace RAE non valido")
    return namespace


def namespace_dir(history_root: Path, namespace: str) -> Path:
    """Return a directory strictly confined to base/namespace."""
    folder = Path(history_root) / validate_namespace(namespace)
    # Prevent a namespace aliasing another via a symlink (read and write).
    if folder.is_symlink():
        raise ValueError(f"Namespace RAE non valido: directory simbolica {folder}")
    return folder


def validate_output_path(history_root: Path, namespace: str, output: Path) -> Path:
    """Refuse output outside the configured namespace (including symlinks)."""
    expected_parent = namespace_dir(history_root, namespace).resolve()
    output = Path(output)
    if output.resolve().parent != expected_parent:
        raise ValueError(f"Output RAE fuori dal namespace {namespace}: {output}")
    if not re.fullmatch(r"rae_[A-Za-z0-9_.-]+_\d{14}(?:_\d+)?\.yaml", output.name):
        raise ValueError(f"Nome file RAE non valido: {output.name}")
    return output
