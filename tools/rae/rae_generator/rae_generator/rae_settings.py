"""Validated versioned settings for RAE test identity."""
from pathlib import Path
import yaml


def load_append_report_name(path: Path) -> bool:
    """Default to unsuffixed IDs when no configuration exists."""
    if not path.exists():
        return False
    try:
        raw = path.read_text(encoding='utf-8')
        data = yaml.safe_load(raw)
        literal = yaml.load(raw, Loader=yaml.BaseLoader)
    except (OSError, UnicodeError, yaml.YAMLError) as exc:
        raise ValueError(f'RAE settings non leggibili: {path}: {exc}') from exc
    if (not isinstance(data, dict) or set(data) != {'version', 'test_ids'}
            or type(data['version']) is not int or data['version'] != 1
            or not isinstance(data['test_ids'], dict)
            or set(data['test_ids']) != {'append_report_name'}
            or type(data['test_ids']['append_report_name']) is not bool
            or literal['test_ids']['append_report_name'] not in ('true', 'false')):
        raise ValueError('RAE settings: attesi version: 1 e test_ids.append_report_name booleano')
    return data['test_ids']['append_report_name']
