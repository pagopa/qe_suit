package it.pagopa.send.bff.informal_notification.infrastructure;

import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.informal_notification.application.InformalNotificationGateway;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationCreationRequest;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.domain.ResolvedInformalRecipient;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalNotificationRequestFactory;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BffInformalNotificationGateway implements InformalNotificationGateway {

    private final InformalDeliveryRestClient informalDeliveryRestClient;
    private final InformalNotificationRequestFactory requestFactory;
    private final InformalNotificationCreationRequest request;
    private final BffInformalNotificationMapper mapper;
    private final EntityStore entityStore;
    private final Map<String, String> paDeliveryApiKeys;

    @Value("${informal.delivery.api-key:68c854bc-a234-4f08-b738-d632814c84cc}")
    private String defaultApiKey;

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

        Map<String, Object> payload = mapper.toPayload(request);
        log.info("Payload invio informal request: {}", payload);
        String apiKey = paDeliveryApiKeys.getOrDefault(sender.name(), defaultApiKey);

        Response response = informalDeliveryRestClient.createInformalRequest(apiKey, payload);
        if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
            throw new RuntimeException("Creazione comunicazione informale fallita: status=" + response.getStatusCode() + " body=" + response.getBody().asString());
        }

        String iun = response.jsonPath().getString("iun");
        if (iun == null || iun.isBlank()) {
            iun = response.jsonPath().getString("notificationRequestId");
        }
        if (iun != null && !iun.contains("-") && iun.length() % 4 == 0) {
            try {
                String decoded = new String(java.util.Base64.getDecoder().decode(iun));
                if (decoded.contains("-")) {
                    log.info("Decodificato notificationRequestId base64 '{}' in IUN: '{}'", iun, decoded);
                    iun = decoded;
                }
            } catch (Exception ignored) {
            }
        }

        if (iun != null && !iun.isBlank()) {
            final String finalIun = iun;
            log.info("Attesa disponibilita comunicazione bonaria su BE per IUN {}...", finalIun);
            try {
                org.awaitility.Awaitility.await()
                        .atMost(java.time.Duration.ofSeconds(90))
                        .pollInterval(java.time.Duration.ofSeconds(1))
                        .ignoreExceptions()
                        .until(() -> {
                            try {
                                Response checkResp = informalDeliveryRestClient.getSentInformalNotification(apiKey, finalIun);
                                return checkResp.getStatusCode() == 200;
                            } catch (Exception e) {
                                return false;
                            }
                        });
                log.info("Comunicazione bonaria IUN {} confermata disponibile su backend (200 OK)!", finalIun);
            } catch (Exception e) {
                log.warn("Timeout o errore durante il polling BE per IUN {}: {}", finalIun, e.getMessage());
            }
        }

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
}
