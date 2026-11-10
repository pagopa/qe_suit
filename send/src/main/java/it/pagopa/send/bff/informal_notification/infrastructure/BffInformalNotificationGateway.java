package it.pagopa.send.bff.informal_notification.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.send.bff.delivery.infrastructure.BffComboRestClient;
import it.pagopa.send.common.informal_notification.application.InformalNotificationGateway;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationCreationRequest;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationTimeline;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.domain.ResolvedInformalRecipient;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalNotificationRequestFactory;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.User;
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
    private final BffComboRestClient bffComboRestClient;
    private final BffInformalNotificationTimelineMapper timelineMapper;

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }

    @Override
    public void prepareNotification(Map<String, String> data) {
        requestFactory.applyPreliminaryData(request, data);
    }

    @Override
    public void addRecipient(User user, InformalRecipientSpec recipient) {
        ResolvedInformalRecipient resolved = requestFactory.resolveRecipient(user, recipient);
        request.addRecipient(resolved);
    }

    @Override
    public InformalNotificationDomain sendNotification(User sender) {
        requestFactory.applySender(request, sender);

        InformalNotificationRequestV1 payload = mapper.toRequestV1(request);
        log.info("Payload invio informal request: {}", payload);

        NewNotificationResponse response = informalNotificationRestClient
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
        request.recipients().clear();
        request.documents().clear();
        return domain;
    }

    public InformalNotificationDomain sendNotification(User sender, int interval) {
        requestFactory.applySender(request, sender);

        InformalNotificationRequestV1 payload = mapper.toRequestV1(request);
        log.info("Payload invio informal request: {}", payload);

        NewNotificationResponse response = informalNotificationRestClient
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
        request.recipients().clear();
        request.documents().clear();
        return domain;
    }

    public FullSentInformalNotificationV1 getInformalNotification(String iun) {
        return informalNotificationRestClient
                .getInformalRequest(iun)
                .withPolling(PollingStrategy.UNTIL_SUCCESS, Duration.of(90, ChronoUnit.SECONDS),Duration.of(7, ChronoUnit.SECONDS))
                .get();
    }

    public FullSentInformalNotificationV1 getInformalNotification(String iun, int interval) {
        return informalNotificationRestClient
                .getInformalRequest(iun)
                .withPolling(PollingStrategy.UNTIL_SUCCESS, Duration.of(90, ChronoUnit.SECONDS),Duration.of(interval, ChronoUnit.SECONDS))
                .get();
    }

    public FullSentInformalNotificationV1 getInformalNotification() {
       String iun = entityStore.getLastOrThrow(InformalNotificationDomain.class).getIun();
       return  getInformalNotification(iun);
    }

    @Override
    public int getSenderDetailStatus(String iun) {
        // l'API del dettaglio usa solo lo IUN
        return ((ApiResponse) bffComboRestClient.getComboDetails(null, iun).withoutPolling().getRaw()).getStatusCode();
    }

    @Override
    public InformalNotificationTimeline getSenderTimeline() {
        String iun = entityStore.getLastOrThrow(InformalNotificationDomain.class).getIun();
        InformalNotificationTimeline timeline = timelineMapper.toDomain(bffComboRestClient.getComboTimeline(null, iun)
                .withPolling(PollingStrategy.UNTIL_SUCCESS, Duration.ofSeconds(90), Duration.ofSeconds(3))
                .get());
        // gli step dei canali arrivano dopo l'invio: si attendono per al massimo 60 secondi
        long deadline = System.currentTimeMillis() + Duration.ofSeconds(60).toMillis();
        while (timeline.statusHistory().stream().noneMatch(status -> status.channelSteps() > 0)
                && System.currentTimeMillis() < deadline) {
            try {
                Thread.sleep(Duration.ofSeconds(3).toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            timeline = timelineMapper.toDomain(bffComboRestClient.getComboTimeline(null, iun).withoutPolling().get());
        }
        return timeline;
    }
}
