package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BffEServiceTemplateVersionGatewayTest {
    @Test
    void publicationAndRepeatedDownloadsKeepUniqueVersionsAliasAndInterfaceContentInGatewayContext() {
        var store = new InMemoryEntityStore();
        var chains = new TestChainFactory(mock(LastApiResponseStore.class), store);
        var restClient = mock(BffEServiceTemplateRestClient.class);
        var gateway = new BffEServiceTemplateVersionGateway(restClient, store,
                Mappers.getMapper(BffEServiceTemplateVersionMapper.class),
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
}
