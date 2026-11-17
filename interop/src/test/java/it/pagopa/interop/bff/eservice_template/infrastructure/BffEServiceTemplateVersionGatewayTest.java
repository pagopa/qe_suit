package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorMapper;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRiskAnalysisMapper;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisForm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BffEServiceTemplateVersionGatewayTest {
    private static BffEServiceTemplateMappingService newMappingService() {
        var mapper = Mappers.getMapper(BffEServiceTemplateMapper.class);
        ReflectionTestUtils.setField(
                mapper,
                "bffEServiceDescriptorMapper",
                Mappers.getMapper(BffEServiceDescriptorMapper.class)
        );
        return new BffEServiceTemplateMappingService(mapper);
    }

    @Test
    void publicationAndRepeatedDownloadsKeepUniqueVersionsAliasAndInterfaceContentInGatewayContext() {
        var store = new InMemoryEntityStore();
        var chains = new TestChainFactory(mock(LastApiResponseStore.class), store);
        var restClient = mock(BffEServiceTemplateRestClient.class);
        var gateway = new BffEServiceTemplateVersionGateway(restClient, store,
                newMappingService(),
                mock(BffEServiceTemplateLifecycleRestClient.class));
        UUID templateId = UUID.randomUUID();
        UUID versionId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        var templateRef = EServiceTemplateRef.of(templateId);
        var versionRef = EServiceTemplateVersionRef.of(versionId);
        var draft = EServiceTemplateVersion.builder().id(versionId).version("1")
                .state(EServiceTemplateVersionState.DRAFT).build();
        var other = EServiceTemplateVersion.builder().id(UUID.randomUUID()).version("2")
                .state(EServiceTemplateVersionState.DEPRECATED).build();
        store.upsert("TPL-1", EServiceTemplate.builder().id(templateId).version(draft).version(other).build());
        var details = new EServiceTemplateVersionDetails().id(versionId).version(1)
                .state(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState.DRAFT)
                .eserviceTemplate(new EServiceTemplateDetails().id(templateId)
                        .creator(new CompactOrganization().id(UUID.randomUUID())))
                ._interface(new EServiceDoc().id(documentId).name("interface.wsdl").contentType("application/xml"));
        RawResponse versionResponse = mock(RawResponse.class);
        when(versionResponse.as(EServiceTemplateVersionDetails.class)).thenReturn(details);
        when(restClient.getEServiceTemplateVersion(templateId, versionId))
                .thenAnswer(invocation -> chains.build(() -> versionResponse, EServiceTemplateVersionDetails.class));
        RawResponse publicationResponse = mock(RawResponse.class);
        when(restClient.publishEServiceTemplateVersion(templateId, versionId))
                .thenAnswer(invocation -> chains.build(() -> {
                    details.setState(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState.PUBLISHED);
                    return publicationResponse;
                }, Void.class));
        byte[] content = {1, 2, 3};
        RawResponse documentResponse = mock(RawResponse.class);
        when(documentResponse.as(byte[].class)).thenReturn(content);
        when(restClient.getEServiceTemplateDocumentById(templateId, versionId, documentId))
                .thenAnswer(invocation -> chains.build(() -> documentResponse, byte[].class));

        gateway.publishEServiceTemplateVersion(templateRef, versionRef);
        for (int i = 0; i < 3; i++) {
            var downloaded = gateway.downloadInterface(templateRef, versionRef);
            var stored = store.getByAliasOrThrow("TPL-1", EServiceTemplate.class);
            assertThat(stored.getVersions()).extracting(EServiceTemplateVersion::getId)
                    .containsExactly(versionId, other.getId());
            assertThat(stored.getLastPublishedVersion().getInterfaceDocument()).isEqualTo(downloaded);
            assertThat(stored.getLastPublishedVersion().getInterfaceDocument().getContent()).containsExactly(content);
            assertThat(stored.findVersion(other.getId())).isEqualTo(other);
        }
    }

    @Test
    void riskAnalysisUploadAndSubsequentVersionReadsRefreshAndPersistRiskAnalysesInGatewayContext() {
        var store = new InMemoryEntityStore();
        var chains = new TestChainFactory(mock(LastApiResponseStore.class), store);
        var restClient = mock(BffEServiceTemplateRestClient.class);
        var mappingService = newMappingService();
        var versionGateway = new BffEServiceTemplateVersionGateway(
                restClient,
                store,
                mappingService,
                mock(BffEServiceTemplateLifecycleRestClient.class)
        );
        var templateGateway = new BffEServiceTemplateGateway(
                restClient,
                versionGateway,
                store,
                mappingService,
                Mappers.getMapper(BffEServiceRiskAnalysisMapper.class)
        );

        UUID templateId = UUID.randomUUID();
        UUID versionId = UUID.randomUUID();
        UUID riskAnalysisId = UUID.randomUUID();
        var templateRef = EServiceTemplateRef.of(templateId);
        var versionRef = EServiceTemplateVersionRef.of(versionId);

        var draft = EServiceTemplateVersion.builder()
                .id(versionId)
                .version("1")
                .state(EServiceTemplateVersionState.DRAFT)
                .build();
        store.upsert("TPL-RECEIVE", EServiceTemplate.builder()
                .id(templateId)
                .mode(EServiceMode.RECEIVE)
                .version(draft)
                .build());

        var bffRiskAnalysis = new EServiceTemplateRiskAnalysis()
                .id(riskAnalysisId)
                .name("receive-ra")
                .riskAnalysisForm(new RiskAnalysisForm()
                        .version("3.0")
                        .answers(Map.of("purpose", List.of("INSTITUTIONAL"))));

        var templateDetails = new EServiceTemplateDetails()
                .id(templateId)
                .creator(new CompactOrganization().id(UUID.randomUUID()))
                .mode(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.RECEIVE)
                .draftVersion(new CompactEServiceTemplateVersion()
                        .id(versionId)
                        .version(1)
                        .state(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState.DRAFT))
                .riskAnalysis(List.of(bffRiskAnalysis));

        var versionDetails = new EServiceTemplateVersionDetails()
                .id(versionId)
                .version(1)
                .state(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState.DRAFT)
                .eserviceTemplate(templateDetails);

        RawResponse createRaResponse = mock(RawResponse.class);
        when(restClient.createEServiceTemplateRiskAnalysis(eq(templateId), any()))
                .thenAnswer(invocation -> chains.build(() -> createRaResponse, Void.class));

        RawResponse templateResponse = mock(RawResponse.class);
        when(templateResponse.as(EServiceTemplateDetails.class)).thenReturn(templateDetails);
        when(restClient.getEServiceTemplate(templateId))
                .thenAnswer(invocation -> chains.build(() -> templateResponse, EServiceTemplateDetails.class));

        RawResponse versionResponse = mock(RawResponse.class);
        when(versionResponse.as(EServiceTemplateVersionDetails.class)).thenReturn(versionDetails);
        when(restClient.getEServiceTemplateVersion(templateId, versionId))
                .thenAnswer(invocation -> chains.build(() -> versionResponse, EServiceTemplateVersionDetails.class));

        EServiceRiskAnalysis form = EServiceRiskAnalysis.builder()
                .name("receive-ra")
                .riskAnalysisForm(it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm.builder()
                        .version("3.0")
                        .answers(Map.of("purpose", List.of("INSTITUTIONAL")))
                        .build())
                .build();

        EServiceTemplate updated = templateGateway.createEServiceTemplateRiskAnalysis(templateRef, TenantKind.PA, form);

        assertThat(updated.getRiskAnalyses()).extracting(EServiceRiskAnalysis::getId).containsExactly(riskAnalysisId);
        assertThat(store.getByAliasOrThrow("TPL-RECEIVE", EServiceTemplate.class).getRiskAnalyses())
                .extracting(EServiceRiskAnalysis::getId)
                .containsExactly(riskAnalysisId);

        versionGateway.getEServiceTemplateVersion(templateRef, versionRef);
        assertThat(store.getByAliasOrThrow("TPL-RECEIVE", EServiceTemplate.class).getRiskAnalyses())
                .extracting(EServiceRiskAnalysis::getId)
                .containsExactly(riskAnalysisId);
    }
}
