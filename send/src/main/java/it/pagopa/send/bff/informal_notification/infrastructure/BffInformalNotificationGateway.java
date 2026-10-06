package it.pagopa.send.bff.informal_notification.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.send.common.informal_notification.application.InformalNotificationGateway;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationCreationRequest;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.domain.ResolvedInformalRecipient;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalNotificationRequestFactory;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.informal.model.FullSentInformalNotificationV1;
import it.pagopa.send.generated.openapi.clients.informal.model.InformalNotificationRequestV1;
import it.pagopa.send.generated.openapi.clients.informal.model.NewNotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BffInformalNotificationGateway implements InformalNotificationGateway {

    private final InformalNotificationRestClient informalNotificationRestClient;
    private final InformalNotificationRequestFactory requestFactory;
    private final InformalNotificationCreationRequest request;
    private final BffInformalNotificationMapper mapper;
    private final EntityStore entityStore;

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }

    @Override
    public void prepareNotification(Map<String, String> data) {
        requestFactory.applyPreliminaryData(request, data);
    }

    @Override
    public void addRecipient(Tenant sender, InformalRecipientSpec recipient) {
        ResolvedInformalRecipient resolved = requestFactory.resolveRecipient(sender, recipient);
        request.addRecipient(resolved);
    }

    @Override
    public InformalNotificationDomain sendNotification(Tenant sender) {
        requestFactory.applySender(request, sender);

        InformalNotificationRequestV1 payload = mapper.toRequestV1(request);
        log.info("Payload invio informal request: {}", payload);


        NewNotificationResponse response= informalNotificationRestClient
                .createInformalRequest(payload)
                .withoutPolling()
                .get();

        assert response != null;
        String iun = new String(java.util.Base64.getDecoder().decode(response.getNotificationRequestId()));

        List<String> messageIds = request.recipients().stream()
                .map(ResolvedInformalRecipient::messageId)
                .toList();

        InformalNotificationDomain domain = InformalNotificationDomain.builder()
                .paProtocolNumber(request.paProtocolNumber())
                .campaignId(request.campaignId())
                .senderDenomination(request.senderDenomination())
                .subject(request.subject())
                .iun(iun)
                .messageIds(messageIds)
                .build();

        entityStore.upsert(domain);
        log.info("Comunicazione bonaria salvata in EntityStore: {}", domain);
        return domain;
    }

    public FullSentInformalNotificationV1 getInformalNotification(String iun) {
        return informalNotificationRestClient
                .getInformalRequest(iun)
                .withPolling(PollingStrategy.UNTIL_SUCCESS, Duration.of(120, ChronoUnit.SECONDS),Duration.of(30, ChronoUnit.SECONDS))
                .get();
    }

    public FullSentInformalNotificationV1 getInformalNotification() {
       String iun = entityStore.getLastOrThrow(InformalNotificationDomain.class).getIun();
       return  getInformalNotification(iun);
    }
}
