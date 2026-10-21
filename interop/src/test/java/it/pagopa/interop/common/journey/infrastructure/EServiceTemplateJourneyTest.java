package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.*;
import it.pagopa.interop.common.eservice_template.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceTemplateJourneyTest {
    private final EServiceTemplateUseCase templates = mock(EServiceTemplateUseCase.class);
    private final EServiceTemplateVersionGateway gateway = mock(EServiceTemplateVersionGateway.class);
    private final BffEServiceTemplateRequestFactory factory = new BffEServiceTemplateRequestFactory();
    private final EServiceTemplateVersionUseCase versions = new EServiceTemplateVersionUseCase(gateway, factory);
    private final InMemoryEntityStore store = new InMemoryEntityStore();
    private final EServiceTemplateJourneyImpl journey = new EServiceTemplateJourneyImpl(templates, versions, factory, store);
    private final EServiceTemplateVersion draft = EServiceTemplateVersion.builder()
            .id(UUID.randomUUID()).version("1").state(DRAFT).build();
    private final EServiceTemplate template = EServiceTemplate.builder().id(UUID.randomUUID())
            .technology(EServiceTechnology.SOAP).version(draft).build();

    @ParameterizedTest
    @ValueSource(strings = {"soap12-un-indirizzo.wsdl", "soap11-indirizzo-commentato-dopo.wsdl",
            "openapi3-riferimenti.yaml", "openapi3-riferimenti.json"})
    void explicitFixtureIsUploadedBeforePublishingAndSnapshotDownload(String fixture) {
        var command = factory.defaultCreationEServiceTemplateCommand()
                .interfaceDocumentPath("assets/eservice-template/" + fixture);
        when(templates.createEServiceTemplate(command)).thenReturn(template);
        var uploaded = draft.toBuilder().interfaceDocument(new EServiceTemplateInterface(
                UUID.randomUUID(), template.getRef(), draft.getRef(), fixture, "application/xml", null)).build();
        var published = uploaded.toBuilder().state(PUBLISHED).build();
        when(gateway.getEServiceTemplateVersion(template.getRef(), draft.getRef())).thenReturn(uploaded);
        when(gateway.publishEServiceTemplateVersion(template.getRef(), draft.getRef())).thenReturn(published);

        journey.createEServiceTemplate(command, PUBLISHED);

        var order = inOrder(gateway);
        order.verify(gateway).uploadInterface(template.getRef(), draft.getRef(), "assets/eservice-template/" + fixture);
        order.verify(gateway).getEServiceTemplateVersion(template.getRef(), draft.getRef());
        order.verify(gateway).publishEServiceTemplateVersion(template.getRef(), draft.getRef());
        order.verify(gateway).downloadInterface(template.getRef(), draft.getRef());
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void draftCreationDoesNotUploadOrPublish() {
        var command = factory.defaultCreationEServiceTemplateCommand();
        var gatewaySnapshot = template.toBuilder().name("gateway snapshot").build();
        store.upsert(gatewaySnapshot);
        when(templates.createEServiceTemplate(command)).thenReturn(template);

        journey.createEServiceTemplate(command, DRAFT);

        assertThat(store.getLastOrThrow(EServiceTemplate.class)).isSameAs(gatewaySnapshot);
        verifyNoInteractions(gateway);
    }

    @Test
    void defaultsSelectExistingSoapFixtureAndDoNotOverwriteAlreadyUploadedInterface() {
        when(gateway.getEServiceTemplateVersion(template.getRef(), draft.getRef())).thenReturn(draft);
        versions.prepareVersionForPublishing(template, draft);
        verify(gateway).uploadInterface(template.getRef(), draft.getRef(),
                "assets/eservice-template/soap11-due-indirizzi.wsdl");
        clearInvocations(gateway);
        var uploaded = draft.toBuilder().interfaceDocument(new EServiceTemplateInterface(
                UUID.randomUUID(), template.getRef(), draft.getRef(), "original", "application/xml", new byte[]{1})).build();
        assertThat(versions.prepareVersionForPublishing(template, uploaded, "another-fixture")).isSameAs(uploaded);
        verifyNoInteractions(gateway);
    }

    @Test
    void refreshUpsertsPreserveTemplateAlias() {
        store.upsert("TPL-1", template);
        var refreshed = template.toBuilder().name("refreshed").build();
        store.upsert(refreshed);
        assertThat(store.getByAliasOrThrow("TPL-1", EServiceTemplate.class)).isSameAs(refreshed);
    }
}
