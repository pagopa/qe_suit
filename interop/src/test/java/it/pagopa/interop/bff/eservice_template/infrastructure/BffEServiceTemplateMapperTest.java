package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BffEServiceTemplateMapperTest {

    private final BffEServiceTemplateMapper mapper = Mappers.getMapper(BffEServiceTemplateMapper.class);

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
                .creator(new CompactOrganization().id(UUID.randomUUID()).name("creator"))
                .name("updated-template")
                .description("updated-description")
                .intendedTarget("updated-target")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .versions(List.of(
                        new CompactEServiceTemplateVersion()
                                .id(existingVersion.getId())
                                .version(1)
                                .state(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState.DRAFT)
                ))
                .riskAnalysis(List.of());

        EServiceTemplate mapped = mapper.toEServiceTemplatePreservingVersions(source, existingTemplate);

        assertDoesNotThrow(() -> mapped.addVersion(
                EServiceTemplateVersion.builder()
                        .id(UUID.randomUUID())
                        .version("2")
                        .state(EServiceTemplateVersionState.DRAFT)
                        .build()
        ));
        assertEquals(2, mapped.getVersions().size());
    }
}

