"""Versioned, narrowly scoped normalization rules for QA matcher comparisons.

Rules change only the comparison copy of Java stack-frame method names.
Raw evidence, stored logs and historical YAML are never rewritten.
"""
from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import re
import yaml

from .qa_invariant_engine import FRAME


@dataclass(frozen=True)
class IgnoreRule:
    id: str
    pattern: re.Pattern[str]
    replacement: str


@dataclass(frozen=True)
class IgnoreRules:
    rules: tuple[IgnoreRule, ...] = ()

    @classmethod
    def load(cls, path: Path | None) -> 'IgnoreRules':
        if path is None:
            return cls()
        try:
            source = yaml.safe_load(path.read_text(encoding='utf-8'))
        except (OSError, UnicodeError, yaml.YAMLError) as exc:
            raise ValueError(f'Regole di ignore non leggibili: {path}: {exc}') from exc
        if not isinstance(source, dict) or set(source) != {'version', 'rules'} or source['version'] != 1:
            raise ValueError('Ignore rules: attesi version: 1 e rules (lista)')
        items = source['rules']
        if not isinstance(items, list) or len(items) > 20:
            raise ValueError('Ignore rules: rules deve essere una lista di massimo 20 elementi')
        ids = set()
        parsed = []
        for item in items:
            if not isinstance(item, dict) or set(item) != {'id', 'scope', 'pattern', 'replacement'}:
                raise ValueError('Ignore rule: richiesti id, scope, pattern, replacement')
            rid, scope, pattern, replacement = (item[k] for k in ('id', 'scope', 'pattern', 'replacement'))
            if not isinstance(rid, str) or not re.fullmatch(r'[a-z][a-z0-9_-]{0,63}', rid) or rid in ids:
                raise ValueError('Ignore rule: id mancante, non valido o duplicato')
            ids.add(rid)
            if scope != 'stack_method':
                raise ValueError(f'Ignore rule {rid}: solo scope=stack_method è autorizzato')
            if not isinstance(pattern, str) or not (1 <= len(pattern) <= 160):
                raise ValueError(f'Ignore rule {rid}: pattern non valido')
            if not isinstance(replacement, str) or not re.fullmatch(r'<[A-Z][A-Z0-9_]{0,40}>', replacement):
                raise ValueError(f'Ignore rule {rid}: replacement deve essere un marcatore <...>')
            try:
                compiled = re.compile(pattern)
            except re.error as exc:
                raise ValueError(f'Ignore rule {rid}: regex non valida: {exc}') from exc
            if compiled.search('') or compiled.groups:
                raise ValueError(f'Ignore rule {rid}: vietati match vuoti e gruppi di cattura')
            # Only replace a bounded *numeric* variable surrounded by unchanged
            # method identity on both sides. This forbids wildcarding methods.
            parsed.append(IgnoreRule(rid, compiled, replacement))
        return cls(tuple(parsed))

    def normalize(self, error: str) -> str:
        if not self.rules:
            return error
        out = []
        for line in error.splitlines(keepends=True):
            match = FRAME.match(line.rstrip('\r\n'))
            if match is None:
                out.append(line)
                continue
            method = match['method']
            for rule in self.rules:
                def substitute(found: re.Match[str]) -> str:
                    start, end = found.span()
                    if (not found.group().isascii() or not found.group().isdigit()
                            or start < 2 or len(method) - end < 2):
                        raise ValueError(
                            f'Ignore rule {rule.id}: consentite solo porzioni numeriche '
                            f'di un metodo stack con contesto stabile')
                    return rule.replacement
                method = rule.pattern.sub(substitute, method)
            # Replace ONLY method span, not source or diagnostic message.
            original = match['method']
            if method != original:
                start, end = match.span('method')
                line = line[:start] + method + line[end:]
            out.append(line)
        return ''.join(out)
