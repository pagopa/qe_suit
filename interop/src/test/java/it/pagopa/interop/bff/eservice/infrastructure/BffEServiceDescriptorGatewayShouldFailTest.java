package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.PollingAction;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceTemplateInterfaceFailureReason;
import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRestClient;
import it.pagopa.utils.async.DelayUtils;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BffEServiceDescriptorGatewayShouldFailTest {
    @Test
    void negative_configuration_sends_once_without_polling_and_waits_before_final_absent_snapshot() {
        List<String> events = new ArrayList<>();
        var eServiceRef = EServiceRef.of(UUID.randomUUID());
        var descriptorRef = EServiceDescriptorRef.of(UUID.randomUUID());
        EServiceDescriptor absent = EServiceDescriptor.builder()
                .id(descriptorRef.id())
                .interfaceDocument(null)
                .build();
        TestHarness harness = harness(eServiceRef, descriptorRef, List.of(absent, absent), events);
        List<SoapAddressConfiguration> addresses = List.of(
                new SoapAddressConfiguration("Svc", "P1", "https://host.example/x<elemento/>"),
                new SoapAddressConfiguration("Svc", "P2", "https://host.example/x\""),
                new SoapAddressConfiguration("Svc", "P2", "https://host.example/x\"")
        );

        try (MockedStatic<DelayUtils> delay = mockStatic(DelayUtils.class)) {
            delay.when(() -> DelayUtils.waitForSeconds(15))
                    .thenAnswer(invocation -> {
                        events.add("delay-15");
                        return null;
                    });
            harness.gateway.shouldFailToConfigureSOAPInterface(
                    eServiceRef,
                    descriptorRef,
                    addresses,
                    EServiceTemplateInterfaceFailureReason.URL_NOT_ALLOWED
            );
            delay.verify(() -> DelayUtils.waitForSeconds(15), times(1));
        }

        assertThat(events).containsExactly(
                "descriptor-read",
                "post",
                "without-polling",
                "status-400",
                "delay-15",
                "descriptor-read"
        );
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(harness.restClient, times(1)).addEServiceTemplateInstanceInterfaceSoap(
                eq(eServiceRef.id()), eq(descriptorRef.id()), payloadCaptor.capture()
        );
        Map<?, ?> payload = (Map<?, ?>) payloadCaptor.getValue();
        List<Map<String, String>> serverUrls = (List<Map<String, String>>) payload.get("serverUrls");
        assertThat(serverUrls).extracting(url -> url.get("url")).containsExactly(
                "https://host.example/x<elemento/>",
                "https://host.example/x\"",
                "https://host.example/x\""
        );
    }

    @Test
    void negative_configuration_keeps_existing_document_metadata_and_bytes_identical() {
        List<String> events = new ArrayList<>();
        var eServiceRef = EServiceRef.of(UUID.randomUUID());
        var descriptorRef = EServiceDescriptorRef.of(UUID.randomUUID());
        byte[] originalBytes = new byte[]{0, (byte) 0xff, 13, 10, 1};
        Document original = Document.builder()
                .id(UUID.randomUUID())
                .name("interface.wsdl")
                .prettyName("interface.wsdl")
                .contentType("application/xml")
                .checksum("sha256:abc")
                .build();
        EServiceDescriptor before = EServiceDescriptor.builder()
                .id(descriptorRef.id())
                .interfaceDocument(original)
                .build();
        Document sameMetadata = Document.builder()
                .id(original.getId())
                .name(original.getName())
                .prettyName(original.getPrettyName())
                .contentType(original.getContentType())
                .checksum(original.getChecksum())
                .build();
        EServiceDescriptor after = EServiceDescriptor.builder()
                .id(descriptorRef.id())
                .interfaceDocument(sameMetadata)
                .build();
        TestHarness harness = harness(eServiceRef, descriptorRef, List.of(before, after), events);

        TestChain<byte[]> downloadChain = mock(TestChain.class);
        PollingAction<byte[]> downloadAction = mock(PollingAction.class);
        when(harness.restClient.getEServiceDocumentById(
                eServiceRef.id(), descriptorRef.id(), original.getId()
        )).thenAnswer(invocation -> {
            events.add("document-read");
            return downloadChain;
        });
        when(downloadChain.withPolling(PollingStrategy.UNTIL_SUCCESS)).thenReturn(downloadAction);
        when(downloadAction.get()).thenAnswer(invocation -> {
            events.add("document-bytes");
            return originalBytes.clone();
        });

        try (MockedStatic<DelayUtils> delay = mockStatic(DelayUtils.class)) {
            delay.when(() -> DelayUtils.waitForSeconds(15))
                    .thenAnswer(invocation -> {
                        events.add("delay-15");
                        return null;
                    });
            harness.gateway.shouldFailToConfigureSOAPInterface(
                    eServiceRef,
                    descriptorRef,
                    List.of(new SoapAddressConfiguration("Svc", "PortaUno", "https://erogatore.example")),
                    EServiceTemplateInterfaceFailureReason.URL_COUNT_MISMATCH
            );
        }

        assertThat(events).containsExactly(
                "descriptor-read", "document-read", "document-bytes",
                "post", "without-polling", "status-400", "delay-15",
                "descriptor-read", "document-read", "document-bytes"
        );
        verify(harness.restClient, times(2)).getEServiceDocumentById(
                eServiceRef.id(), descriptorRef.id(), original.getId()
        );
    }

    @Test
    void soap_payload_mapping_preserves_empty_lists_order_duplicates_and_original_strings() {
        var payload = BffEServiceDescriptorGateway.soapPayload(List.of(
                new SoapAddressConfiguration("Svc", "P2", "https://host.example/x'"),
                new SoapAddressConfiguration("Svc", "P1", "https://host.example/x<node/>"),
                new SoapAddressConfiguration("Svc", "P2", "https://host.example/x'")
        ));
        var urls = (List<Map<String, String>>) payload.get("serverUrls");

        assertThat(urls).extracting(url -> url.get("url")).containsExactly(
                "https://host.example/x'",
                "https://host.example/x<node/>",
                "https://host.example/x'"
        );
        assertThat(BffEServiceDescriptorGateway.soapPayload(List.of()).get("serverUrls")).isEqualTo(List.of());
    }

    private static TestHarness harness(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            List<EServiceDescriptor> descriptors,
            List<String> events
    ) {
        BffEServiceRestClient restClient = mock(BffEServiceRestClient.class);
        EntityStore entityStore = mock(EntityStore.class);
        BffEServiceDescriptorMapper mapper = mock(BffEServiceDescriptorMapper.class);
        TestChain postChain = mock(TestChain.class);
        PollingAction postAction = mock(PollingAction.class);
        when(restClient.addEServiceTemplateInstanceInterfaceSoap(
                eq(eServiceRef.id()), eq(descriptorRef.id()), any()
        )).thenAnswer(invocation -> {
            events.add("post");
            return postChain;
        });
        when(postChain.withoutPolling()).thenAnswer(invocation -> {
            events.add("without-polling");
            return postAction;
        });
        when(postAction.assertStatusCode(400)).thenAnswer(invocation -> {
            events.add("status-400");
            return postAction;
        });

        Queue<EServiceDescriptor> reads = new ArrayDeque<>(descriptors);
        BffEServiceDescriptorGateway gateway = spy(new BffEServiceDescriptorGateway(restClient, entityStore, mapper));
        doAnswer(invocation -> {
            events.add("descriptor-read");
            return reads.remove();
        }).when(gateway).getEServiceDescriptor(eServiceRef, descriptorRef);

        return new TestHarness(gateway, restClient);
    }

    private record TestHarness(BffEServiceDescriptorGateway gateway, BffEServiceRestClient restClient) {}
}
