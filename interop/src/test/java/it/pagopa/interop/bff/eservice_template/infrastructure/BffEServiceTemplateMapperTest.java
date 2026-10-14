package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BffEServiceTemplateMapperTest {
    private final BffEServiceTemplateMapper mapper = Mappers.getMapper(BffEServiceTemplateMapper.class);

    @Test
    void maps_template_version_state_and_metadata_only_interface_documents() {
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
        var interfaceDocument = mapped.getVersions().get(0).getInterfaceDocument();
        assertThat(interfaceDocument.getId()).isEqualTo(interfaceId);
        assertThat(interfaceDocument.getTemplateRef().templateId()).isEqualTo(templateId);
        assertThat(interfaceDocument.getVersionRef().versionId()).isEqualTo(versionId);
        assertThat(interfaceDocument.getContent()).isNull();
        assertThat(mapped.getVersions().get(0).getDocs()).extracting("id").containsExactly(documentId);
    }

    @Test
    void merging_partial_version_reads_preserves_other_versions_and_only_matching_checksum_bytes() {
        UUID templateId = UUID.randomUUID();
        UUID versionOne = UUID.randomUUID();
        UUID versionTwo = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        EServiceTemplateInterface cached = new EServiceTemplateInterface(
                documentId,
                new EServiceTemplateRef(templateId),
                new EServiceTemplateVersionRef(versionTwo),
                "interface.yaml",
                "application/yaml",
                "interface.yaml",
                "sha256:same",
                new byte[]{1, 2, 3}
        );
        EServiceTemplate existing = EServiceTemplate.builder()
                .id(templateId)
                .version(EServiceTemplateVersion.builder().id(versionOne).version("1").build())
                .version(EServiceTemplateVersion.builder().id(versionTwo).version("2")
                        .interfaceDocument(cached).build())
                .build();
        EServiceTemplateVersionDetails sameChecksum = templateVersionDetails(
                templateId, versionTwo, documentId, "sha256:same"
        );

        EServiceTemplate refreshed = mapper.toEServiceTemplate(sameChecksum, existing);

        assertThat(refreshed.getVersions()).extracting(EServiceTemplateVersion::getId)
                .containsExactly(versionOne, versionTwo);
        assertThat(refreshed.getVersion(2).getInterfaceDocument().getContent())
                .containsExactly(1, 2, 3);

        EServiceTemplate changedChecksum = mapper.toEServiceTemplate(
                templateVersionDetails(templateId, versionTwo, documentId, null),
                refreshed
        );
        assertThat(changedChecksum.getVersion(2).getInterfaceDocument().getContent()).isNull();
    }

    private static EServiceTemplateVersionDetails templateVersionDetails(
            UUID templateId,
            UUID versionId,
            UUID documentId,
            String checksum
    ) {
        EServiceTemplateDetails template = new EServiceTemplateDetails()
                .id(templateId)
                .name("template")
                .technology(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology.SOAP)
                .mode(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.DELIVER);
        EServiceDoc document = new EServiceDoc()
                .id(documentId)
                .name("interface.yaml")
                .prettyName("interface.yaml")
                .contentType("application/yaml")
                .checksum(checksum);
        return new EServiceTemplateVersionDetails()
                .id(versionId)
                .version(2)
                .state(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState.DRAFT)
                ._interface(document)
                .eserviceTemplate(template);
    }
}
