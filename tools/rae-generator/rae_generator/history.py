"""Select the latest approved YAMLs already merged into develop."""
from pathlib import Path
import re
import yaml

SUITE_GENERAL = 'NrtStandardSuiteTest'
FILENAME = re.compile(r'^rae_(.+)_(\d{14})(?:_\d+)?\.yaml$')


def load_history(rae_dir: Path, suite: str, limit: int = 7) -> list[dict]:
    groups: dict[str, list[tuple[str, dict, str]]] = {suite: []}
    if suite != SUITE_GENERAL:
        groups[SUITE_GENERAL] = []
    if not rae_dir.exists():
        return []
    for path in rae_dir.glob('rae_*.yaml'):
        match = FILENAME.fullmatch(path.name)
        if not match:
            continue
        try:
            data = yaml.safe_load(path.read_text(encoding='utf-8'))
        except (yaml.YAMLError, OSError):
            continue
        if not isinstance(data, dict) or not isinstance(data.get('rae'), dict):
            continue
        target_suite = str(data['rae'].get('suite', ''))
        if target_suite not in groups:
            continue
        # Every historical file must originate from approved develop; the caller
        # is responsible for checkout of develop before calling this function.
        groups[target_suite].append((match.group(2), data, path.name))
    selected = []
    for items in groups.values():
        items.sort(key=lambda item: item[0], reverse=True)
        selected.extend(items[:limit])
    selected.sort(key=lambda item: item[0], reverse=True)
    history = []
    for _, report, source_file in selected:
        for key in ('ko', 'ko_know'):
            for entry in report.get(key, []) or []:
                if isinstance(entry, dict) and entry.get('procedure') and entry.get('result'):
                    history.append({**entry, '_source_environment': report['rae'].get('environment'),
                                    '_source_file': source_file, '_source_suite': report['rae'].get('suite')})
    return history
