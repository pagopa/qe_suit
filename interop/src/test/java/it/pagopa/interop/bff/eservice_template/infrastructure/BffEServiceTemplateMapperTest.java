package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorMapper;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.domain.*;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BffEServiceTemplateMapperTest {

    private final BffEServiceTemplateMapper mapper =
            Mappers.getMapper(BffEServiceTemplateMapper.class);

    @BeforeEach
    void injectMapperDependencies() {
        ReflectionTestUtils.setField(
                mapper,
                "bffEServiceDescriptorMapper",
                Mappers.getMapper(BffEServiceDescriptorMapper.class)
        );
    }

    @Test
    void preservingVersions_keeps_version_list_mutable() {
        EServiceTemplateVersion existingVersion = EServiceTemplateVersion.builder()
                .id(UUID.randomUUID())
                .version("1")
                .state(EServiceTemplateVersionState.DRAFT)
                .build();

        EServiceTemplate existingTemplate = EServiceTemplate.builder()
                .id(UUID.randomUUID())
                .name("existing-template")
                .description("existing-description")
                .intendedTarget("existing-target")
                .versions(List.of(existingVersion))
                .riskAnalyses(List.of())
                .build();

        EServiceTemplateDetails source = new EServiceTemplateDetails()
                .id(existingTemplate.getId())
                .creator(
                        new CompactOrganization()
                                .id(UUID.randomUUID())
                                .name("creator")
                )
                .name("updated-template")
                .description("updated-description")
                .intendedTarget("updated-target")
                .technology(
                        it.pagopa.interop.generated.openapi.clients.bff.model
                                .EServiceTechnology.REST
                )
                .mode(
                        it.pagopa.interop.generated.openapi.clients.bff.model
                                .EServiceMode.DELIVER
                )
                .versions(List.of(
                        new CompactEServiceTemplateVersion()
                                .id(existingVersion.getId())
                                .version(1)
                                .state(
                                        it.pagopa.interop.generated.openapi.clients.bff.model
                                                .EServiceTemplateVersionState.DRAFT
                                )
                ))
                .riskAnalysis(List.of());

        EServiceTemplate mapped =
                mapper.toEServiceTemplatePreservingVersions(source, existingTemplate);

        assertDoesNotThrow(() -> mapped.addVersion(
                EServiceTemplateVersion.builder()
                        .id(UUID.randomUUID())
                        .version("2")
                        .state(EServiceTemplateVersionState.DRAFT)
                        .build()
        ));

        assertThat(mapped.getVersions()).hasSize(2);
        assertThat(existingTemplate.getVersions()).hasSize(1);
    }

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
                .technology(
                        it.pagopa.interop.generated.openapi.clients.bff.model
                                .EServiceTechnology.REST
                )
                .mode(
                        it.pagopa.interop.generated.openapi.clients.bff.model
                                .EServiceMode.DELIVER
                )
                .personalData(false)
                .versions(List.of(
                        new CompactEServiceTemplateVersion()
                                .state(
                                        it.pagopa.interop.generated.openapi.clients.bff.model
                                                .EServiceTemplateVersionState.PUBLISHED
                                )
                ));

        EServiceTemplateVersionDetails response =
                new EServiceTemplateVersionDetails()
                        .id(versionId)
                        .version(1)
                        .state(
                                it.pagopa.interop.generated.openapi.clients.bff.model
                                        .EServiceTemplateVersionState.PUBLISHED
                        )
                        ._interface(
                                new EServiceDoc()
                                        .id(interfaceId)
                                        .name("interface.wsdl")
                                        .contentType("application/xml")
                        )
                        .docs(List.of(
                                new EServiceDoc()
                                        .id(documentId)
                                        .name("extra.pdf")
                                        .contentType("application/pdf")
                        ))
                        .eserviceTemplate(template);

        EServiceTemplate mapped = mapper.toEServiceTemplate(response);

        assertThat(mapped.getId()).isEqualTo(templateId);
        assertThat(mapped.getState()).isEqualTo(EServiceTemplateState.PUBLISHED);
        assertThat(mapped.getTechnology()).isEqualTo(EServiceTechnology.REST);
        assertThat(mapped.getMode()).isEqualTo(EServiceMode.DELIVER);
        assertThat(mapped.getVersions()).hasSize(1);

        EServiceTemplateVersion version = mapped.getVersions().get(0);

        assertThat(version.getId()).isEqualTo(versionId);
        assertThat(version.getVersion()).isEqualTo("1");
        assertThat(version.getState())
                .isEqualTo(EServiceTemplateVersionState.PUBLISHED);

        EServiceTemplateInterface interfaceDocument =
                version.getInterfaceDocument();

        assertThat(interfaceDocument).isNotNull();
        assertThat(interfaceDocument.getId()).isEqualTo(interfaceId);
        assertThat(interfaceDocument.getTemplateRef().id())
                .isEqualTo(templateId);
        assertThat(interfaceDocument.getVersionRef().versionId())
                .isEqualTo(versionId);
        assertThat(interfaceDocument.getName()).isEqualTo("interface.wsdl");
        assertThat(interfaceDocument.getContentType()).isEqualTo("application/xml");
        assertThat(interfaceDocument.getContent()).isNull();

        assertThat(version.getDocs())
                .extracting("id")
                .containsExactly(documentId);
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
                .version(
                        EServiceTemplateVersion.builder()
                                .id(versionOne)
                                .version("1")
                                .build()
                )
                .version(
                        EServiceTemplateVersion.builder()
                                .id(versionTwo)
                                .version("2")
                                .interfaceDocument(cached)
                                .build()
                )
                .build();

        EServiceTemplateVersionDetails sameChecksum = templateVersionDetails(
                templateId,
                versionTwo,
                documentId,
                "sha256:same"
        );

        EServiceTemplate refreshed =
                mapper.toEServiceTemplate(sameChecksum, existing);

        assertThat(refreshed.getVersions())
                .extracting(EServiceTemplateVersion::getId)
                .containsExactly(versionOne, versionTwo);

        assertThat(refreshed.getVersion(2).getInterfaceDocument().getContent())
                .containsExactly((byte) 1, (byte) 2, (byte) 3);

        EServiceTemplate changedChecksum = mapper.toEServiceTemplate(
                templateVersionDetails(
                        templateId,
                        versionTwo,
                        documentId,
                        null
                ),
                refreshed
        );

        assertThat(changedChecksum.getVersion(2).getInterfaceDocument().getContent())
                .isNull();
    }

    @Test
    void preservingVersions_keeps_existing_interface_document_and_content() {
        UUID templateId = UUID.randomUUID();
        UUID versionId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        EServiceTemplateInterface cached = new EServiceTemplateInterface(
                documentId,
                new EServiceTemplateRef(templateId),
                new EServiceTemplateVersionRef(versionId),
                "interface.yaml",
                "application/yaml",
                "interface.yaml",
                "sha256:same",
                new byte[]{1, 2, 3}
        );

        EServiceTemplateVersion existingVersion = EServiceTemplateVersion.builder()
                .id(versionId)
                .version("1")
                .state(EServiceTemplateVersionState.DRAFT)
                .interfaceDocument(cached)
                .build();

        EServiceTemplate existing = EServiceTemplate.builder()
                .id(templateId)
                .versions(List.of(existingVersion))
                .riskAnalyses(List.of())
                .build();

        EServiceTemplateDetails source = new EServiceTemplateDetails()
                .id(templateId)
                .name("template")
                .technology(
                        it.pagopa.interop.generated.openapi.clients.bff.model
                                .EServiceTechnology.REST
                )
                .mode(
                        it.pagopa.interop.generated.openapi.clients.bff.model
                                .EServiceMode.DELIVER
                )
                .versions(List.of(
                        new CompactEServiceTemplateVersion()
                                .id(versionId)
                                .version(2)
                                .state(
                                        it.pagopa.interop.generated.openapi.clients.bff.model
                                                .EServiceTemplateVersionState.PUBLISHED
                                )
                ));

        EServiceTemplate mapped =
                mapper.toEServiceTemplatePreservingVersions(source, existing);

        assertThat(mapped.getVersions()).hasSize(1);

        EServiceTemplateVersion mappedVersion = mapped.getVersions().get(0);

        assertThat(mappedVersion.getVersion()).isEqualTo("2");
        assertThat(mappedVersion.getState())
                .isEqualTo(EServiceTemplateVersionState.PUBLISHED);
        assertThat(mappedVersion.getInterfaceDocument()).isSameAs(cached);
        assertThat(mappedVersion.getInterfaceDocument().getContent())
                .containsExactly((byte) 1, (byte) 2, (byte) 3);

        assertThat(existingVersion.getVersion()).isEqualTo("1");
        assertThat(existingVersion.getState())
                .isEqualTo(EServiceTemplateVersionState.DRAFT);
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
                .technology(
                        it.pagopa.interop.generated.openapi.clients.bff.model
                                .EServiceTechnology.SOAP
                )
                .mode(
                        it.pagopa.interop.generated.openapi.clients.bff.model
                                .EServiceMode.DELIVER
                );

        EServiceDoc document = new EServiceDoc()
                .id(documentId)
                .name("interface.yaml")
                .prettyName("interface.yaml")
                .contentType("application/yaml")
                .checksum(checksum);

        return new EServiceTemplateVersionDetails()
                .id(versionId)
                .version(2)
                .state(
                        it.pagopa.interop.generated.openapi.clients.bff.model
                                .EServiceTemplateVersionState.DRAFT
                )
                ._interface(document)
                .eserviceTemplate(template);
    }
}