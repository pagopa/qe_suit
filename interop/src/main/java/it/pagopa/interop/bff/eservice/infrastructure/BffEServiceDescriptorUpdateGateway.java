package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.DocumentKind;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangePropertiesInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BffEServiceDescriptorUpdateGateway {
    private final BffEServiceRestClient restClient;
    private final BffEServiceTemplateInstanceDescriptorRestClient instanceClient;
    private final BffEServiceDescriptorInterfaceGateway interfaceGateway;
    private final EntityStore entityStore;

    public void update(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef,
                       BffUpdateEServiceDescriptorCommand command) {
        EService service = entityStore.getById(serviceRef.id(), EService.class)
                .orElseThrow(() -> new IllegalStateException("EService missing from context: " + serviceRef.id()));
        var descriptor = service.findDescriptor(descriptorRef.id());
        if (descriptor == null) {
            throw new IllegalStateException("Descriptor missing from context: " + descriptorRef.id());
        }
        if (descriptor.getTemplateRef() != null) {
            if (command.getInterfaceDocumentPath() != null) {
                throw new IllegalArgumentException("Template instance interfaces must be configured, not replaced");
            }
            var source = command.getBffPayload();
            var payload = new UpdateEServiceDescriptorTemplateInstanceSeed()
                    .audience(source.getAudience())
                    .dailyCallsPerConsumer(source.getDailyCallsPerConsumer())
                    .dailyCallsTotal(source.getDailyCallsTotal())
                    .agreementApprovalPolicy(source.getAgreementApprovalPolicy())
                    .attributes(source.getAttributes());
            if (source.getAsyncExchangeProperties() != null) {
                var async = source.getAsyncExchangeProperties();
                payload.asyncExchangeProperties(new AsyncExchangePropertiesInstanceSeed()
                        .responseTime(async.getResponseTime())
                        .resourceAvailableTime(async.getResourceAvailableTime())
                        .maxResultSet(async.getMaxResultSet()));
            }
            instanceClient.updateDraftDescriptor(serviceRef.id(), descriptorRef.id(), payload)
                    .withPolling(PollingStrategy.UNTIL_SUCCESS).get();
        } else {
            setDefaultAsyncPropertiesIfNeeded(service, descriptorRef, command);
            restClient.updateDescriptor(serviceRef.id(), descriptorRef.id(), command.getBffPayload())
                    .withPolling(PollingStrategy.UNTIL_SUCCESS).get();
        }
    }

    private void setDefaultAsyncPropertiesIfNeeded(EService service, EServiceDescriptorRef descriptorRef,
                                                  BffUpdateEServiceDescriptorCommand command) {
        if (command.getBffPayload().getAsyncExchangeProperties() != null) return;
        if (Boolean.FALSE.equals(service.getAsyncExchange())) return;
        interfaceGateway.linkDocument(service.getRef(), descriptorRef, "assets/origin-interface.yaml",
                DocumentKind.ASYNC_EXCHANGE_CALLBACK_INTERFACE);
        command.getBffPayload().setAsyncExchangeProperties(new AsyncExchangeProperties()
                .responseTime(60).resourceAvailableTime(60).maxResultSet(1).confirmation(false).bulk(true));
    }
}
