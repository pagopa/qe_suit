package it.pagopa.send.common.informal_notification.infrastructure.factory;

import it.pagopa.send.bff.infrastructure.DebtPositionService;
import it.pagopa.send.bff.infrastructure.DocumentPreloadService;
import it.pagopa.send.bff.infrastructure.PaGroupService;
import it.pagopa.send.bff.informal_notification.infrastructure.DeliveryPushMessagesRestClient;
import it.pagopa.send.common.infrastructure.RandomNumericGenerator;
import it.pagopa.send.common.informal_notification.domain.InformalMessageSpec;
import it.pagopa.send.common.informal_notification.domain.InformalMockSequences;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationCreationRequest;
import it.pagopa.send.common.informal_notification.domain.InformalPagoPaPaymentSpec;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.domain.ResolvedInformalPagoPaPayment;
import it.pagopa.send.common.informal_notification.domain.ResolvedInformalRecipient;
import it.pagopa.send.common.legal_notification.domain.DocumentRef;
import it.pagopa.send.common.legal_notification.domain.PreloadedDocument;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RequestOverrideApplier;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InformalNotificationRequestFactory {

    private static final int PA_PROTOCOL_NUMBER_LENGTH = 17;
    private static final String PA_PROTOCOL_NUMBER_PREFIX = "2";
    private static final String PAGOPA_TEST_CREDITOR_TAX_ID = "77777777777";
    private static final String ATTACHMENT_CONTENT_TYPE = "application/pdf";

    private final InformalNotificationDefaultsLoader defaultsLoader;
    private final DebtPositionService debtPositionService;
    private final DocumentPreloadService documentPreloadService;
    private final PaGroupService paGroupService;
    private final DeliveryPushMessagesRestClient deliveryPushMessagesRestClient;

    public void applyPreliminaryData(InformalNotificationCreationRequest request, Map<String, String> data) {
        Map<String, Object> defaults = defaultsLoader.load();
        Map<String, String> overrides = new HashMap<>(data);

        String campaignId = overrides.remove("campaignId");
        if (campaignId == null) {
            campaignId = (String) defaults.get("campaignId");
        }

        String subject = overrides.remove("subject");
        if (subject == null) {
            subject = (String) defaults.get("subject");
        }

        request.paProtocolNumber(generatePaProtocolNumber())
                .subject(subject)
                .campaignId(campaignId)
                .senderDenomination((String) defaults.get("senderDenomination"));

        boolean includeAttachment = true;
        if (overrides.containsKey("includeAttachment")) {
            includeAttachment = Boolean.parseBoolean(overrides.remove("includeAttachment"));
        } else if (InformalMockSequences.CAMPAIGN_REMINDER.equalsIgnoreCase(campaignId)) {
            // Reminder campaign specifies no attached documents
            includeAttachment = false;
        }

        if (includeAttachment) {
            int docsCount = 1;
            if (overrides.containsKey("documentsCount")) {
                docsCount = Integer.parseInt(overrides.remove("documentsCount"));
            }
            for (int i = 0; i < docsCount; i++) {
                request.addDocument(buildCommunicationAttachment("comm-attachment-" + UUID.randomUUID()));
            }
        }

        RequestOverrideApplier.apply(request, overrides);
    }

    public ResolvedInformalRecipient resolveRecipient(Tenant sender, InformalRecipientSpec spec) {
        Recipient recipient = spec.recipient();
        String taxId = spec.taxIdOverride() != null ? spec.taxIdOverride() : recipient.getTaxId();
        String denomination = spec.denominationOverride() != null ? spec.denominationOverride() : (recipient.getDenomination() != null ? recipient.getDenomination() : recipient.getName());

        InformalMessageSpec msg = spec.messageSpec();
        String messageId = deliveryPushMessagesRestClient.createMessage(
                sender,
                msg.subject(),
                msg.longBody(),
                msg.shortBody(),
                msg.language()
        );

        List<ResolvedInformalPagoPaPayment> resolvedPayments = spec.payments().stream()
                .map(p -> buildPagoPaPayment(p, denomination, taxId))
                .toList();

        return new ResolvedInformalRecipient(
                spec.recipientType(),
                taxId,
                denomination,
                messageId,
                spec.email(),
                spec.phoneNumber(),
                spec.additionalLanguage(),
                spec.physicalAddress(),
                resolvedPayments
        );
    }

    public void applySender(InformalNotificationCreationRequest request, Tenant sender) {
        request.senderDenomination(sender.getOrganization())
                .group(paGroupService.findActiveGroupId(sender).orElse(null));
    }

    private String generatePaProtocolNumber() {
        return PA_PROTOCOL_NUMBER_PREFIX + RandomNumericGenerator.generate(PA_PROTOCOL_NUMBER_LENGTH);
    }

    private ResolvedInformalPagoPaPayment buildPagoPaPayment(InformalPagoPaPaymentSpec spec, String denomination, String taxId) {
        String noticeCode = "3" + debtPositionService.createDebtPosition(PAGOPA_TEST_CREDITOR_TAX_ID, denomination, taxId, spec.amount());
        return new ResolvedInformalPagoPaPayment(
                noticeCode,
                PAGOPA_TEST_CREDITOR_TAX_ID,
                spec.amount(),
                spec.dueDate(),
                buildAttachment("notif-attachment-" + UUID.randomUUID())
        );
    }

    private DocumentRef buildAttachment(String preloadIdx) {
        PreloadedDocument document = documentPreloadService.preloadTestPdf(preloadIdx);
        return new DocumentRef(document.key(), document.versionToken(), document.sha256(), ATTACHMENT_CONTENT_TYPE);
    }

    private DocumentRef buildCommunicationAttachment(String preloadIdx) {
        PreloadedDocument document = documentPreloadService.preloadTestPdf(preloadIdx);
        String key = document.key();
        if (key != null && key.startsWith("PN_NOTIFICATION_ATTACHMENTS")) {
            key = key.replaceFirst("PN_NOTIFICATION_ATTACHMENTS", "PN_COMMUNICATIONS_ATTACHMENT");
        }
        return new DocumentRef(key, document.versionToken(), document.sha256(), ATTACHMENT_CONTENT_TYPE);
    }
}
