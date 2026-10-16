import java.nio.charset.StandardCharsets

File report = new File(basedir, "target/pst/generate-widget-client/pst-report.html")
assert report.isFile() : "PST report not generated at default path: " + report

String html = report.getText(StandardCharsets.UTF_8.name())

List<String> rows = html.split('<tr class="scenario-row"').drop(1).collect { it.substring(0, it.indexOf('</tr>')) }

def cell = { String row, String css ->
    def matcher = row =~ /class="${css}"[^>]*>([^<]*)</
    matcher.find() ? matcher.group(1).trim() : null
}
def status = { String row ->
    def matcher = row =~ /class="status-code [^"]*" title="([^"]*)">([^<]*)</
    matcher.find() ? [details: matcher.group(1), code: matcher.group(2).trim()] : null
}
def find = { String scope, String target, String scenario ->
    rows.find { cell(it, 'scope-cell') == scope && cell(it, 'target') == target && cell(it, 'scenario') == scenario }
}

assert html.contains('data-operation-id="updateWidget"') : "selected operation missing"
assert !html.contains('data-operation-id="getWidget"') : "operation filter from PST config not applied"
assert !html.contains('getGadget') : "other OpenAPI Generator execution must not be used"
assert html.contains('/widgets/{widgetId}')
assert html.contains('Expected status code')
assert !html.contains('Expectation origin')

def longName = find('PAYLOAD', '$.name', 'REPLACED_WITH_LONG_STRING')
assert longName != null : "expected payload scenario missing"
assert status(longName) == [details: 'INVALID · POLICY_INVALID', code: '422']

def override = find('PAYLOAD', '$.quantity', 'REPLACED_WITH_NULL')
assert override != null : "override scenario missing"
assert status(override).code == '299'
assert status(override).details.endsWith('TARGET_OVERRIDE')

def pathParam = find('PATH_PARAMS', '{widgetId}', 'REPLACED_WITH_MALFORMED_UUID')
assert pathParam != null : "path param scenario missing"
assert status(pathParam) == [details: 'UNKNOWN · POLICY_UNKNOWN', code: '404']

File log = new File(basedir, "build.log")
String buildLog = log.getText(StandardCharsets.UTF_8.name())
assert buildLog.contains('PST OpenAPI Generator execution: generate-widget-client')
assert buildLog.contains('PST operations designed: 1')
assert buildLog.contains('PST report generated: ')

return true
