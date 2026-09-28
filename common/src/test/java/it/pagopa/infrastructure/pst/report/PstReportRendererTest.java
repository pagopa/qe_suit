package it.pagopa.infrastructure.pst.report;

import it.pagopa.infrastructure.contract.http.ContractValidity;
import it.pagopa.infrastructure.contract.http.ExpectationOrigin;
import it.pagopa.infrastructure.contract.http.RequestScope;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.NodePath;
import it.pagopa.infrastructure.pst.model.PstDocument;
import it.pagopa.infrastructure.pst.model.PstOperation;
import it.pagopa.infrastructure.pst.model.PstScenario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PstReportRendererTest {

    private final PstReportRenderer renderer = new PstReportRenderer();

    @TempDir
    Path temporaryDirectory;

    @Test
    void writesUtf8HtmlCreatingMissingOutputDirectories() throws Exception {
        Path output = temporaryDirectory.resolve("nested/pst/report.html");

        renderer.render(document(), "PST àgreement", output);

        assertTrue(Files.exists(output));
        String html = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(html.contains("<h1 id=\"pst-title\">PST àgreement</h1>"));
    }

    @Test
    void rendersGeneralSummaryFromModel() {
        String html = renderer.renderToString(document(), PstReportRenderer.DEFAULT_TITLE);

        assertTrue(html.contains("<h1 id=\"pst-title\">" + PstReportRenderer.DEFAULT_TITLE + "</h1>"));
        assertTrue(html.contains("id=\"operation-count\">2<"));
        assertTrue(html.contains("id=\"scenario-count\">4<"));
        assertFalse(html.contains("count-validity"));
    }

    @Test
    void rendersEveryOperationWithMethodPathAndScenarioCount() {
        String html = renderer.renderToString(document(), "PST");

        String create = operationSection(html, "createAgreement");
        assertTrue(create.contains("class=\"method-chip http-method\">POST<"));
        assertTrue(create.contains("class=\"operation-path\">/agreements<"));
        assertTrue(create.contains(">3 scenari<"));

        String get = operationSection(html, "getAgreementById");
        assertTrue(get.contains("class=\"method-chip http-method\">GET<"));
        assertTrue(get.contains("class=\"operation-path\">/agreements/{agreementId}<"));
        assertTrue(get.contains(">1 scenari<"));
    }

    @Test
    void rendersScenarioRowsExactlyFromModelValues() {
        String html = renderer.renderToString(document(), "PST");
        List<String> createRows = rows(operationSection(html, "createAgreement"));

        assertEquals(3, createRows.size());
        assertRow(createRows.get(0), "PAYLOAD", "$.delegationId", "REPLACED_WITH_NULL", 200, "status-2xx", "VALID · INFERRED_VALID");
        assertRow(createRows.get(1), "PAYLOAD", "$.name", "REPLACED_WITH_EMPTY_STRING", 202, "status-2xx", "INVALID · TARGET_OVERRIDE");
        assertRow(createRows.get(2), "PAYLOAD", "$.name", "REPLACED_WITH_XSS", 403, "status-4xx", "INVALID · POLICY_INVALID");

        List<String> getRows = rows(operationSection(html, "getAgreementById"));
        assertEquals(1, getRows.size());
        assertRow(getRows.get(0), "PATH_PARAMS", "{agreementId}", "REPLACED_WITH_MALFORMED_UUID", 400, "status-4xx", "UNKNOWN · POLICY_UNKNOWN");
    }

    @Test
    void rendersOnlyScopeTargetScenarioAndExpectedStatusCodeColumns() {
        String html = renderer.renderToString(document(), "PST");

        assertTrue(html.contains("<th>Scope</th><th>Target</th><th>Fuzz scenario</th>"
                + "<th class=\"expected-status-header\">Expected status code</th>"));
        assertFalse(html.contains("<th>Validity</th>"));
        assertFalse(html.contains("Expectation origin"));
        assertFalse(html.contains("validity-chip"));
    }

    @Test
    void rendersKeywordAndScopeFiltersOverSearchableRows() {
        String html = renderer.renderToString(document(), "PST");

        assertTrue(html.contains("id=\"textFilter\""));
        assertTrue(html.contains("id=\"scopeFilter\""));
        assertTrue(html.contains("<option value=\"PAYLOAD\">PAYLOAD</option>"));
        assertTrue(html.contains("<option value=\"PATH_PARAMS\">PATH_PARAMS</option>"));

        String create = operationSection(html, "createAgreement");
        assertTrue(html.contains("data-search=\"post /agreements createagreement\""));
        assertTrue(create.contains("data-search=\"$.delegationid replaced_with_null 200\""));
    }

    @Test
    void rendersPayloadTargetsAsJsonPathAndPathParamsAsPlaceholders() {
        PstDocument document = new PstDocument(List.of(new PstOperation("op", "PUT", "/items/{itemId}", List.of(
                scenario(RequestScope.PAYLOAD, "", FuzzScenario.REMOVED, ContractValidity.UNKNOWN, 400, ExpectationOrigin.POLICY_UNKNOWN),
                scenario(RequestScope.PAYLOAD, "/descriptor/id", FuzzScenario.REMOVED, ContractValidity.INVALID, 400, ExpectationOrigin.POLICY_INVALID),
                scenario(RequestScope.PAYLOAD, "/items/0/name", FuzzScenario.REMOVED, ContractValidity.INVALID, 400, ExpectationOrigin.POLICY_INVALID),
                scenario(RequestScope.PAYLOAD, "/a-b/c~1d", FuzzScenario.REMOVED, ContractValidity.INVALID, 500, ExpectationOrigin.TARGET_OVERRIDE),
                scenario(RequestScope.PATH_PARAMS, "/itemId", FuzzScenario.REPLACED_WITH_NIL_UUID, ContractValidity.UNKNOWN, 404, ExpectationOrigin.POLICY_UNKNOWN)
        ))));

        List<String> rows = rows(operationSection(renderer.renderToString(document, "PST"), "op"));

        assertTrue(rows.get(0).contains("class=\"target\">$<"), rows.get(0));
        assertTrue(rows.get(1).contains("class=\"target\">$.descriptor.id<"), rows.get(1));
        assertTrue(rows.get(2).contains("class=\"target\">$.items[0].name<"), rows.get(2));
        assertTrue(rows.get(3).contains("class=\"target\">$[&#39;a-b&#39;][&#39;c/d&#39;]<"), rows.get(3));
        assertTrue(rows.get(3).contains("status-5xx"), rows.get(3));
        assertTrue(rows.get(4).contains("class=\"target\">{itemId}<"), rows.get(4));
    }

    @Test
    void groupsScenariosByScopeInsideOperation() {
        String html = renderer.renderToString(document(), "PST");

        assertTrue(operationSection(html, "createAgreement").contains("data-scope=\"PAYLOAD\""));
        assertFalse(operationSection(html, "createAgreement").contains("data-scope=\"PATH_PARAMS\""));
        assertTrue(operationSection(html, "getAgreementById").contains("data-scope=\"PATH_PARAMS\""));
    }

    @Test
    void escapesHtmlSpecialCharactersFromModelValues() {
        PstDocument document = new PstDocument(List.of(new PstOperation(
                "op<script>alert(1)</script>",
                "POST",
                "/items/<b>{id}</b>?a=1&b=2",
                List.of(new PstScenario(
                        RequestScope.PAYLOAD,
                        NodePath.fromPointer("/na<i>me\""),
                        FuzzScenario.REPLACED_WITH_XSS,
                        ContractValidity.UNKNOWN,
                        400,
                        ExpectationOrigin.POLICY_UNKNOWN
                ))
        )));

        String html = renderer.renderToString(document, "<img src=x onerror=alert(1)>");

        assertFalse(html.contains("<script>alert(1)</script>"));
        assertFalse(html.contains("<img src=x"));
        assertFalse(html.contains("<b>{id}</b>"));
        assertFalse(html.contains("/na<i>me"));
        assertTrue(html.contains("op&lt;script&gt;alert(1)&lt;/script&gt;"));
        assertTrue(html.contains("&lt;img src=x onerror=alert(1)&gt;"));
        assertTrue(html.contains("/items/&lt;b&gt;{id}&lt;/b&gt;?a=1&amp;b=2"));
        assertTrue(html.contains("na&lt;i&gt;me&quot;"));
    }

    @Test
    void rendersEmptyDocumentWithoutFailing() {
        String html = renderer.renderToString(new PstDocument(List.of()), "PST");

        assertTrue(html.contains("id=\"operation-count\">0<"));
        assertTrue(html.contains("Nessuna operation presente nel PST."));
    }

    private void assertRow(
            String row,
            String scope,
            String target,
            String scenario,
            int status,
            String statusClass,
            String details
    ) {
        assertTrue(row.contains("class=\"scope-cell\">" + scope + "<"), row);
        assertTrue(row.contains("class=\"target\">" + target + "<"), row);
        assertTrue(row.contains("class=\"scenario\">" + scenario + "<"), row);
        assertTrue(row.contains("class=\"status-code " + statusClass + "\" title=\"" + details + "\">" + status + "<"), row);
    }

    private String operationSection(String html, String operationId) {
        Matcher matcher = Pattern.compile(
                "<section[^>]*data-operation-id=\"" + Pattern.quote(operationId) + "\"[^>]*>(.*?)</section>",
                Pattern.DOTALL
        ).matcher(html);
        assertTrue(matcher.find(), "Missing operation section " + operationId);
        return matcher.group(1);
    }

    private List<String> rows(String section) {
        Matcher matcher = Pattern.compile("<tr class=\"scenario-row\"[^>]*>(.*?)</tr>", Pattern.DOTALL).matcher(section);
        List<String> rows = new java.util.ArrayList<>();
        while (matcher.find()) rows.add(matcher.group(1));
        return rows;
    }

    private PstDocument document() {
        return new PstDocument(List.of(
                new PstOperation("createAgreement", "POST", "/agreements", List.of(
                        scenario(RequestScope.PAYLOAD, "/delegationId", FuzzScenario.REPLACED_WITH_NULL,
                                ContractValidity.VALID, 200, ExpectationOrigin.INFERRED_VALID),
                        scenario(RequestScope.PAYLOAD, "/name", FuzzScenario.REPLACED_WITH_EMPTY_STRING,
                                ContractValidity.INVALID, 202, ExpectationOrigin.TARGET_OVERRIDE),
                        scenario(RequestScope.PAYLOAD, "/name", FuzzScenario.REPLACED_WITH_XSS,
                                ContractValidity.INVALID, 403, ExpectationOrigin.POLICY_INVALID)
                )),
                new PstOperation("getAgreementById", "GET", "/agreements/{agreementId}", List.of(
                        scenario(RequestScope.PATH_PARAMS, "/agreementId", FuzzScenario.REPLACED_WITH_MALFORMED_UUID,
                                ContractValidity.UNKNOWN, 400, ExpectationOrigin.POLICY_UNKNOWN)
                ))
        ));
    }

    private PstScenario scenario(
            RequestScope scope,
            String target,
            FuzzScenario scenario,
            ContractValidity validity,
            int status,
            ExpectationOrigin origin
    ) {
        return new PstScenario(scope, NodePath.fromPointer(target), scenario, validity, status, origin);
    }
}
