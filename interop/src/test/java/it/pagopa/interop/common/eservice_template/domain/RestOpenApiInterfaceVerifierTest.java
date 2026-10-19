package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestOpenApiInterfaceVerifierTest {
    private final RestOpenApiInterfaceVerifier verifier = new RestOpenApiInterfaceVerifier();
    private final RestInterfaceConfiguration expected = new RestInterfaceConfiguration(
            List.of("https://erogatore.example/api"),
            "Referente Erogatore",
            "referente@erogatore.example",
            "https://erogatore.example/contatti",
            "https://erogatore.example/condizioni"
    );

    @Test
    void validates_customized_json_and_preserves_paths_schemas_and_refs() throws IOException {
        String original = textResource("openapi3-riferimenti.json");
        String customized = original
                .replace("https://originale.example/api", "https://erogatore.example/api")
                .replace("\"version\": \"1.0.0\",", "\"version\": \"1.0.0\",\n    \"termsOfService\": \"https://erogatore.example/condizioni\",")
                .replace("\"name\": \"Contatto originario\",", "\"name\": \"Referente Erogatore\",\n      \"url\": \"https://erogatore.example/contatti\",")
                .replace("originario@example.test", "referente@erogatore.example");

        verifier.assertCustomized(
                original.getBytes(StandardCharsets.UTF_8),
                customized.getBytes(StandardCharsets.UTF_8),
                expected,
                false
        );
        verifier.assertValid(customized.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void yaml_ampersands_and_asterisks_inside_quoted_strings_are_not_aliases() throws IOException {
        String original = textResource("openapi3-riferimenti.yaml");
        String customized = original
                .replace("https://originale.example/api", "https://erogatore.example/api")
                .replace("1.0.0", "1.0.0\n  termsOfService: https://erogatore.example/condizioni")
                .replace("name: Contatto originario", "name: Referente Erogatore\n    url: https://erogatore.example/contatti")
                .replace("originario@example.test", "referente@erogatore.example")
                .replace("\"Specifica originaria; i caratteri & e * qui sono testo.\"",
                        "\"Specificata con & e * dentro una stringa YAML.\"");

        verifier.assertCustomized(
                original.getBytes(StandardCharsets.UTF_8),
                customized.getBytes(StandardCharsets.UTF_8),
                expected,
                true
        );
    }

    @Test
    void yaml_anchor_and_alias_tokens_are_rejected_without_textual_false_positives() throws IOException {
        String original = textResource("openapi3-riferimenti.yaml");
        String customized = original.replace(
                "title: Servizio pagamenti",
                "title: Servizio pagamenti\n  x-template: &templateAnchor valore\n  x-reused: *templateAnchor"
        ).replace("https://originale.example/api", "https://erogatore.example/api")
                .replace("1.0.0", "1.0.0\n  termsOfService: https://erogatore.example/condizioni")
                .replace("name: Contatto originario", "name: Referente Erogatore\n    url: https://erogatore.example/contatti")
                .replace("originario@example.test", "referente@erogatore.example");

        assertThatThrownBy(() -> verifier.assertCustomized(
                original.getBytes(StandardCharsets.UTF_8),
                customized.getBytes(StandardCharsets.UTF_8),
                expected,
                true
        )).isInstanceOf(AssertionError.class).hasMessageContaining("anchor o alias");
    }

    @Test
    void schema_mutations_and_reference_expansion_are_detected() throws IOException {
        String original = textResource("openapi3-riferimenti.json");
        String customized = original
                .replace("https://originale.example/api", "https://erogatore.example/api")
                .replace("\"version\": \"1.0.0\",", "\"version\": \"1.0.0\",\n    \"termsOfService\": \"https://erogatore.example/condizioni\",")
                .replace("\"name\": \"Contatto originario\",", "\"name\": \"Referente Erogatore\",\n      \"url\": \"https://erogatore.example/contatti\",")
                .replace("originario@example.test", "referente@erogatore.example")
                .replace("\"type\": \"number\"", "\"type\": \"string\"");

        assertThatThrownBy(() -> verifier.assertCustomized(
                original.getBytes(StandardCharsets.UTF_8),
                customized.getBytes(StandardCharsets.UTF_8),
                expected,
                false
        )).isInstanceOf(AssertionError.class).hasMessageContaining("Schemi OpenAPI");
    }

    @Test
    void inlined_reference_expansion_is_detected_as_a_path_structure_change() throws IOException {
        String original = textResource("openapi3-riferimenti.json");
        String expanded = original
                .replace("https://originale.example/api", "https://erogatore.example/api")
                .replace("\"version\": \"1.0.0\",", "\"version\": \"1.0.0\",\n    \"termsOfService\": \"https://erogatore.example/condizioni\",")
                .replace("\"name\": \"Contatto originario\",", "\"name\": \"Referente Erogatore\",\n      \"url\": \"https://erogatore.example/contatti\",")
                .replace("originario@example.test", "referente@erogatore.example")
                .replace("\"$ref\": \"#/components/schemas/Pagamento\"",
                        "\"type\": \"object\", \"required\": [\"importo\"], \"properties\": {"
                                + "\"importo\": {\"type\": \"number\", \"format\": \"double\"},"
                                + "\"causale\": {\"type\": \"string\"}}");

        assertThatThrownBy(() -> verifier.assertCustomized(
                original.getBytes(StandardCharsets.UTF_8),
                expanded.getBytes(StandardCharsets.UTF_8),
                expected,
                false
        )).isInstanceOf(AssertionError.class).hasMessageContaining("Path o riferimenti");
    }

    private static String textResource(String name) throws IOException {
        try (var stream = RestOpenApiInterfaceVerifierTest.class.getResourceAsStream(
                "/assets/eservice-template/" + name
        )) {
            if (stream == null) throw new IOException("Fixture mancante: " + name);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
