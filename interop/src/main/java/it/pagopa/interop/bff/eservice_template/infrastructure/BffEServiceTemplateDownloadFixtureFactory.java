package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.contract.DocumentDownloadFixture;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BffEServiceTemplateDownloadFixtureFactory {
    private final InteropJourney journey;
    private final EServiceTemplateVersionUseCase versionUseCase;
    private final BffEServiceTemplateRestClient restClient;
    private final BffEServiceTemplateRequestFactory requestFactory;

    public DocumentDownloadFixture create(Tenant producer, boolean async, boolean published) {
        EServiceTemplate template = journey.switchChannel(Channel.BFF)
                .withProducer(producer, UserRole.ADMIN)
                .createEServiceTemplate(command -> command.isAsync(async).technology(EServiceTechnology.REST)
                        .mode(EServiceMode.DELIVER).handlePersonalData(false), EServiceTemplateVersionState.DRAFT)
                .get(EServiceTemplate.class);
        var version = template.getLastDraftVersion();
        var payload = requestFactory.updateDraftVersionRequest();
        if (async) {
            payload.setAsyncExchangeProperties(new AsyncExchangeProperties().responseTime(60)
                    .resourceAvailableTime(60).maxResultSet(1).confirmation(false).bulk(true));
        }
        versionUseCase.updateDraftVersion(template, version,
                BffUpdateEServiceTemplateVersionCommand.from(payload));
        UUID templateId = template.getId();
        UUID versionId = version.getId();
        File document = FileUtils.loadClasspathResourceAsTempFile("assets/origin-interface.yaml");
        try {
            UUID interfaceId = upload(templateId, versionId, "INTERFACE",
                    RandomUtils.randomAlphanumericName("Specifica API"), document);
            if (async) {
                upload(templateId, versionId, "ASYNC_EXCHANGE_CALLBACK_INTERFACE",
                        RandomUtils.randomAlphanumericName("Interfaccia callback"), document);
            }
            List<UUID> additionalIds = List.of(
                    upload(templateId, versionId, "DOCUMENT", "Guida all'integrazione", document),
                    upload(templateId, versionId, "DOCUMENT", "Informazioni aggiuntive", document));
            var metadata = PollingUtils.pollUntil(
                    () -> restClient.getEServiceTemplateVersion(templateId, versionId)
                            .withPolling(PollingStrategy.UNTIL_SUCCESS).get(),
                    current -> current.getInterface() != null
                            && interfaceId.equals(current.getInterface().getId())
                            && current.getDocs().stream().map(EServiceDoc::getId).toList().containsAll(additionalIds)
                            && (!async || current.getAsyncExchangeCallbackInterface() != null),
                    "Documenti della fixture template non ancora disponibili");
            if (published) {
                versionUseCase.publishVersion(template, version);
                metadata = restClient.getEServiceTemplateVersion(templateId, versionId)
                        .withPolling(PollingStrategy.UNTIL_SUCCESS).get();
            }
            return new DocumentDownloadFixture(templateId, versionId,
                    metadata.getInterface().getPrettyName(),
                    metadata.getDocs().stream().filter(doc -> additionalIds.contains(doc.getId()))
                            .map(EServiceDoc::getPrettyName).toList(),
                    async ? metadata.getAsyncExchangeCallbackInterface().getPrettyName() : null);
        } finally {
            if (!document.delete()) document.deleteOnExit();
        }
    }

    private UUID upload(UUID templateId, UUID versionId, String kind, String name, File document) {
        return restClient.createEServiceTemplateDocument(templateId, versionId, kind, name, document)
                .withPolling(PollingStrategy.UNTIL_SUCCESS).get().getId();
    }
}
