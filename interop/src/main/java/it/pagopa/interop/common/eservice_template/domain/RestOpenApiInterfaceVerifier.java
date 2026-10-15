package it.pagopa.interop.common.eservice_template.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class RestOpenApiInterfaceVerifier {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void assertCustomized(
            byte[] original,
            byte[] customized,
            RestInterfaceConfiguration expected,
            boolean yaml
    ) {
        assertConfiguredValues(customized, expected);
        assertStructurePreserved(original, customized, yaml);
    }

    public void assertConfiguredValues(byte[] customized, RestInterfaceConfiguration expected) {
        OpenAPI customizedApi = parse(customized);
        assertServerUrls(customizedApi, expected.serverUrls());
        assertContact(customizedApi, expected);
    }

    public void assertStructurePreserved(byte[] original, byte[] customized, boolean yaml) {
        OpenAPI originalApi = parse(original);
        OpenAPI customizedApi = parse(customized);

        if (yaml) {
            assertNoYamlAnchors(customized);
        }
        assertEqual(
                toTree(originalApi.getComponents() == null ? null : originalApi.getComponents().getSchemas()),
                toTree(customizedApi.getComponents() == null ? null : customizedApi.getComponents().getSchemas()),
                "Schemi OpenAPI modificati o espansi"
        );
        assertEqual(
                toTree(originalApi.getPaths()),
                toTree(customizedApi.getPaths()),
                "Path o riferimenti OpenAPI modificati"
        );
    }

    public OpenAPI assertValid(byte[] content) {
        return parse(content);
    }

    private OpenAPI parse(byte[] content) {
        String spec = new String(content, StandardCharsets.UTF_8);
        ParseOptions options = new ParseOptions();
        options.setResolve(false);
        options.setResolveFully(false);
        options.setFlatten(false);
        SwaggerParseResult result = new OpenAPIV3Parser().readContents(spec, null, options);
        if (result == null || result.getOpenAPI() == null || !result.getMessages().isEmpty()) {
            throw new AssertionError("Specifica OpenAPI non valida: "
                    + (result == null ? "nessun risultato parser" : result.getMessages()));
        }
        return result.getOpenAPI();
    }

    private void assertServerUrls(OpenAPI api, List<String> expectedUrls) {
        List<String> actualUrls = api.getServers() == null
                ? List.of()
                : api.getServers().stream().map(server -> server.getUrl()).toList();
        if (!expectedUrls.equals(actualUrls)) {
            throw new AssertionError("Server OpenAPI errati: attesi %s, trovati %s".formatted(expectedUrls, actualUrls));
        }
    }

    private void assertContact(OpenAPI api, RestInterfaceConfiguration expected) {
        if (api.getInfo() == null || api.getInfo().getContact() == null) {
            throw new AssertionError("Contatto OpenAPI mancante");
        }
        var contact = api.getInfo().getContact();
        assertEquals(expected.contactName(), contact.getName(), "Nome contatto");
        assertEquals(expected.contactEmail(), contact.getEmail(), "Email contatto");
        assertEquals(expected.contactUrl(), contact.getUrl(), "URL contatto");
        assertEquals(expected.termsAndConditionsUrl(), api.getInfo().getTermsOfService(), "Termini e condizioni");
    }

    private void assertNoYamlAnchors(byte[] content) {
        String yaml = new String(content, StandardCharsets.UTF_8);
        var parser = new ParserImpl(new StreamReader(new StringReader(yaml)), new LoaderOptions());
        while (!parser.checkEvent(Event.ID.StreamEnd)) {
            Event event = parser.getEvent();
            if (event instanceof AliasEvent
                    || (event instanceof NodeEvent nodeEvent && nodeEvent.getAnchor() != null)) {
                throw new AssertionError("La specifica YAML contiene anchor o alias");
            }
        }
    }

    private JsonNode toTree(Object value) {
        return objectMapper.valueToTree(value);
    }

    private static void assertEqual(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(String expected, String actual, String field) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError("%s errato: atteso <%s>, trovato <%s>".formatted(field, expected, actual));
        }
    }
}
