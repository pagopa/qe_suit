from pathlib import Path
import pytest

from rae_generator.ignore_rules import IgnoreRules


def rules(tmp_path):
    p = tmp_path / 'ignore.yaml'
    p.write_text('''version: 1
rules:
  - id: java-generated-proxy-ordinal
    scope: stack_method
    pattern: '(?<=\\$Proxy)[0-9]+(?=\\.)'
    replacement: '<PROXY_ORDINAL>'
''', encoding='utf-8')
    return IgnoreRules.load(p)


def test_proxy_ordinals_normalized_only_in_stack(tmp_path):
    normalizer = rules(tmp_path)
    a = ('java.lang.AssertionError: $Proxy150\n'
         '\tat jdk.proxy2/jdk.proxy2.$Proxy150.isDisabled(Unknown Source)\n'
         '\tat jdk.proxy2/jdk.proxy2.$Proxy272.isDisabled(Unknown Source)\n')
    b = ('java.lang.AssertionError: $Proxy150\n'
         '\tat jdk.proxy2/jdk.proxy2.$Proxy130.isDisabled(Unknown Source)\n'
         '\tat jdk.proxy2/jdk.proxy2.$Proxy190.isDisabled(Unknown Source)\n')
    assert normalizer.normalize(a) == normalizer.normalize(b)
    assert '$Proxy150\n' in normalizer.normalize(a)


def test_other_methods_and_details_unchanged(tmp_path):
    norm = rules(tmp_path).normalize
    a = '\tat jdk.proxy2/jdk.proxy2.$Proxy150.isDisabled(Unknown Source)\n'
    assert norm(a) != norm(a.replace('isDisabled', 'isEnabled'))
    assert norm(a) != norm(a.replace('Unknown Source', 'Proxy.java:33'))
    assert norm(a) != norm(a.replace('jdk.proxy2/jdk.proxy2', 'jdk.proxy3/jdk.proxy3'))
    assert norm('ERROR 400\n' + a) != norm('ERROR 500\n' + a)


def test_bad_configuration_fails_closed(tmp_path):
    p = tmp_path / 'bad.yaml'
    for body in ('version: 2\nrules: []',
                 'version: 1\nrules: [{id: x, scope: diagnostic, pattern: ".*", replacement: "<X>"}]',
                 'version: 1\nrules: [{id: x, scope: stack_method, pattern: "[", replacement: "<X>"}]'):
        p.write_text(body, encoding='utf-8')
        with pytest.raises(ValueError):
            IgnoreRules.load(p)


def test_generated_ordinal_not_matching_random_proxy_suffix(tmp_path):
    norm = rules(tmp_path).normalize
    assert norm('  at com.company.$Proxy150Foo.run(Unknown Source)') == '  at com.company.$Proxy150Foo.run(Unknown Source)'
