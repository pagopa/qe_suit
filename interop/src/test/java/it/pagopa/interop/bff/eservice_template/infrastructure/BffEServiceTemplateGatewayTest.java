package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.template.action.PollingAction;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.DocumentRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static it.pagopa.infrastructure.template.action.strategy.PollingStrategy.UNTIL_SUCCESS;

class BffEServiceTemplateGatewayTest {
    @Test
    void later_binary_download_does_not_replace_the_original_entity_store_snapshot() {
        UUID templateId = UUID.randomUUID();
        UUID versionId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        EServiceTemplate template = EServiceTemplate.builder()
                .id(templateId)
                .version(EServiceTemplateVersion.builder()
                        .id(versionId)
                        .interfaceDocument(DocumentRef.builder().id(documentId).build())
                        .build())
                .build();
        byte[] originalBytes = new byte[]{0, (byte) 0xff, 13, 10};
        byte[] latestServerBytes = new byte[]{1, (byte) 0xfe, 13, 10};
        EServiceTemplateInterface original = new EServiceTemplateInterface(
                documentId,
                new EServiceTemplateRef(templateId),
                new EServiceTemplateVersionRef(templateId),
                "interface.wsdl",
                "application/xml",
                originalBytes
        );
        EntityStore entityStore = new InMemoryEntityStore();
        entityStore.upsert(original);

        BffEServiceTemplateRestClient restClient = mock(BffEServiceTemplateRestClient.class);
        BffEServiceTemplateMapper mapper = mock(BffEServiceTemplateMapper.class);
        TestChain<byte[]> chain = mock(TestChain.class);
        PollingAction<byte[]> action = mock(PollingAction.class);
        when(restClient.getEServiceTemplateDocumentById(templateId, versionId, documentId)).thenReturn(chain);
        when(chain.withPolling(UNTIL_SUCCESS)).thenReturn(action);
        when(action.get()).thenReturn(latestServerBytes);
        BffEServiceTemplateGateway gateway = new BffEServiceTemplateGateway(restClient, mapper);

        byte[] downloaded = gateway.downloadInterfaceBytes(template, documentId);

        assertThat(downloaded).containsExactly(latestServerBytes);
        assertThat(entityStore.getById(documentId, EServiceTemplateInterface.class).orElseThrow().getContent())
                .containsExactly(originalBytes);
    }

    @Test
    void original_interface_model_copies_input_and_output_bytes_defensively() {
        byte[] source = new byte[]{1, 2, 3};
        EServiceTemplateInterface original = new EServiceTemplateInterface(
                UUID.randomUUID(),
                new EServiceTemplateRef(UUID.randomUUID()),
                new EServiceTemplateVersionRef(UUID.randomUUID()),
                "interface.json",
                "application/json",
                source
        );
        source[0] = 9;
        byte[] read = original.getContent();
        read[1] = 9;

        assertThat(original.getContent()).containsExactly(1, 2, 3);
    }
}
