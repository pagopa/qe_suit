package it.pagopa.interop.common.eservice_template;

import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorInterface;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.eservice_template.application.OpenApiUseCase;
import it.pagopa.interop.common.eservice_template.application.SoapUseCase;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DownloadedInterfaceUseCaseTest {
    @Test
    void soap_address_failure_identifies_descriptor_and_document_and_preserves_parser_diagnostics() {
        UUID descriptorId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        byte[] content = """
                <w:definitions xmlns:w="http://schemas.xmlsoap.org/wsdl/"
                  xmlns:s="http://schemas.xmlsoap.org/wsdl/soap/">
                <w:service><w:port name="PortaUno"><s:address location="https://host.example"/>
                </w:port></w:service></w:definitions>
                """.getBytes(StandardCharsets.UTF_8);
        EServiceDescriptor descriptor = EServiceDescriptor.builder().id(descriptorId)
                .interfaceDocument(new EServiceDescriptorInterface(
                        Document.builder().id(documentId).build(), null, null, content))
                .build();

        assertThatThrownBy(() -> new SoapUseCase().shouldContainAddresses(descriptor, List.of()))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("descriptorId=" + descriptorId, "documentId=" + documentId)
                .hasMessageContaining("servizio (wsdl:service/@name): <attributo name assente>")
                .hasMessageContaining("riga 3, colonna")
                .hasCauseInstanceOf(AssertionError.class);
    }

    @Test
    void soap_requires_an_explicitly_downloaded_interface() {
        EServiceDescriptor descriptor = EServiceDescriptor.builder().build();
        assertThatThrownBy(() -> new SoapUseCase().shouldBeWellFormed(descriptor))
                .isInstanceOf(AssertionError.class).hasMessageContaining("non è stato scaricato");
    }

    @Test
    void openapi_requires_an_explicitly_downloaded_interface() {
        EServiceDescriptor descriptor = EServiceDescriptor.builder().build();
        assertThatThrownBy(() -> new OpenApiUseCase().shouldBeValid(descriptor))
                .isInstanceOf(AssertionError.class).hasMessageContaining("non è stato scaricato");
    }
}
