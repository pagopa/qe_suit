package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WsdlInterfaceVerifierTest {
    private final WsdlInterfaceVerifier verifier = new WsdlInterfaceVerifier();

    @Test
    void all_template_wsdl_fixtures_are_well_formed_and_use_expected_service_ports() throws IOException {
        List<String> fixtures = List.of(
                "soap11-un-indirizzo.wsdl",
                "soap12-un-indirizzo.wsdl",
                "soap11-indirizzo-commentato-prima.wsdl",
                "soap11-indirizzo-commentato-dopo.wsdl",
                "soap12-indirizzo-commentato-prima.wsdl",
                "soap12-indirizzo-commentato-dopo.wsdl",
                "soap11-prefisso-alternativo.wsdl",
                "soap12-prefisso-alternativo.wsdl",
                "soap11-due-indirizzi.wsdl",
                "soap12-due-indirizzi.wsdl",
                "soap11-soap12-due-indirizzi.wsdl",
                "soap11-due-indirizzi-originali-uguali.wsdl",
                "soap12-due-indirizzi-originali-uguali.wsdl"
        );
        for (String fixture : fixtures) {
            byte[] content = resource(fixture);
            verifier.assertWellFormed(content);
            List<SoapAddressConfiguration> expected = fixture.contains("due-indirizzi")
                    ? List.of(
                            new SoapAddressConfiguration("ServizioPagamenti", "PortaUno",
                                    fixture.contains("originali-uguali") ? "https://originale.example/stesso"
                                            : fixture.contains("soap11-soap12") ? "https://originale.example/uguale"
                                            : "https://originale.example/uno"),
                            new SoapAddressConfiguration("ServizioPagamenti", "PortaDue",
                                    fixture.contains("originali-uguali") ? "https://originale.example/stesso" : "https://originale.example/due")
                    )
                    : List.of(new SoapAddressConfiguration(
                            "ServizioPagamenti",
                            "PortaUno",
                            "https://originale.example/pay"
                    ));
            verifier.assertAddresses(content, expected);
        }
    }

    @Test
    void compares_only_active_locations_with_alternate_prefixes_comments_cdata_quotes_and_crlf() {
        String original = """
                <wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                  xmlns:soapAlt="http://schemas.xmlsoap.org/wsdl/soap/"
                  targetNamespace="urn:test">
                <!-- <soapAlt:address location="https://comment.example"/> -->
                <wsdl:service name="ServizioPagamenti"><wsdl:port name="PortaUno">
                <soapAlt:address location='https://same.example?a=1&amp;b=2'/>
                </wsdl:port></wsdl:service>
                <![CDATA[<soapAlt:address location="https://cdata.example"/>]]>
                </wsdl:definitions>
                """.replace("\n", "\r\n");
        String customized = original.replace(
                "https://same.example?a=1&amp;b=2",
                "https://erogatore.example/api?p=1&amp;q=2"
        );
        byte[] originalBytes = original.getBytes(StandardCharsets.UTF_8);
        byte[] customizedBytes = customized.getBytes(StandardCharsets.UTF_8);

        verifier.assertWellFormed(customizedBytes);
        verifier.assertAddresses(customizedBytes, List.of(new SoapAddressConfiguration(
                "ServizioPagamenti", "PortaUno", "https://erogatore.example/api?p=1&q=2"
        )));
        verifier.assertOnlyConfiguredLocationsDiffer(originalBytes, customizedBytes);
    }

    @Test
    void unrelated_address_elements_are_not_treated_as_soap_locations() {
        String original = """
                <w:definitions xmlns:w="http://schemas.xmlsoap.org/wsdl/"
                  xmlns:s="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:x="urn:unrelated">
                <w:service name="ServizioPagamenti"><w:port name="PortaUno">
                <x:address location="https://untouched.example"/>
                <s:address location="https://originale.example"/>
                </w:port></w:service></w:definitions>
                """;
        String customized = original.replace(
                "https://originale.example",
                "https://erogatore.example"
        );

        verifier.assertOnlyConfiguredLocationsDiffer(
                original.getBytes(StandardCharsets.UTF_8),
                customized.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void well_formedness_is_independent_from_wsdl_service_address_validation() {
        byte[] xml = ("<definitions xmlns:s=\"http://schemas.xmlsoap.org/wsdl/soap/\">"
                + "<s:address location=\"https://host.example\"/></definitions>")
                .getBytes(StandardCharsets.UTF_8);

        verifier.assertWellFormed(xml);
        assertThatThrownBy(() -> verifier.assertAddresses(xml, List.of()))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("servizio");
    }

    @Test
    void detects_swapped_addresses_and_non_location_byte_changes() throws IOException {
        byte[] twoPorts = resource("soap11-due-indirizzi.wsdl");
        assertThatThrownBy(() -> verifier.assertAddresses(twoPorts, List.of(
                new SoapAddressConfiguration("ServizioPagamenti", "PortaUno", "https://erogatore.example/due"),
                new SoapAddressConfiguration("ServizioPagamenti", "PortaDue", "https://erogatore.example/uno")
        ))).isInstanceOf(AssertionError.class);

        byte[] original = resource("soap11-un-indirizzo.wsdl");
        String changed = new String(original, StandardCharsets.UTF_8)
                .replace("https://originale.example/pay", "https://erogatore.example/pay")
                .replace("Template immutabile", "Template modificato");
        assertThatThrownBy(() -> verifier.assertOnlyConfiguredLocationsDiffer(
                original, changed.getBytes(StandardCharsets.UTF_8)
        )).isInstanceOf(AssertionError.class);
    }

    private static byte[] resource(String name) throws IOException {
        try (var stream = WsdlInterfaceVerifierTest.class.getResourceAsStream(
                "/assets/eservice-template/" + name
        )) {
            if (stream == null) throw new IOException("Fixture mancante: " + name);
            return stream.readAllBytes();
        }
    }
}
