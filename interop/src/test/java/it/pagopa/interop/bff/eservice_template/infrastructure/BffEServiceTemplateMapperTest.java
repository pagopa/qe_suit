package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorMapper;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.DocumentRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BffEServiceTemplateMapperTest {
    private final BffEServiceTemplateMapper mapper = new BffEServiceTemplateMapper(
            Mappers.getMapper(BffEServiceDescriptorMapper.class)
    );

    @Test
    void maps_template_version_state_and_document_references_from_bff_response() {
        UUID templateId = UUID.randomUUID();
        UUID versionId = UUID.randomUUID();
        UUID interfaceId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        EServiceTemplateDetails template = new EServiceTemplateDetails()
                .id(templateId)
                .name("template")
                .description("template description")
                .intendedTarget("interop entities")
                .technology(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology.REST)
                .mode(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.DELIVER)
                .personalData(false);
        EServiceTemplateVersionDetails response = new EServiceTemplateVersionDetails()
                .id(versionId)
                .version(1)
                .state(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState.PUBLISHED)
                ._interface(new EServiceDoc().id(interfaceId).name("interface.wsdl").contentType("application/xml"))
                .docs(List.of(new EServiceDoc().id(documentId).name("extra.pdf").contentType("application/pdf")))
                .eserviceTemplate(template);

        var mapped = mapper.toEServiceTemplate(response);

        assertThat(mapped.getId()).isEqualTo(templateId);
        assertThat(mapped.getTechnology()).isEqualTo(EServiceTechnology.REST);
        assertThat(mapped.getMode()).isEqualTo(EServiceMode.DELIVER);
        assertThat(mapped.getVersions()).hasSize(1);
        assertThat(mapped.getVersions().get(0).getId()).isEqualTo(versionId);
        assertThat(mapped.getVersions().get(0).getState()).isEqualTo(EServiceTemplateVersionState.PUBLISHED);
        assertThat(mapped.getVersions().get(0).getInterfaceDocument())
                .isEqualTo(DocumentRef.builder().id(interfaceId).build());
        assertThat(mapped.getVersions().get(0).getDocs())
                .containsExactly(DocumentRef.builder().id(documentId).build());
    }
}
