package it.pagopa.send.bff.informal_notification.infrastructure;

import it.pagopa.send.common.informal_notification.domain.InformalNotificationCreationRequest;
import it.pagopa.send.common.informal_notification.domain.ResolvedInformalPagoPaPayment;
import it.pagopa.send.common.informal_notification.domain.ResolvedInformalRecipient;
import it.pagopa.send.common.legal_notification.domain.DocumentRef;
import it.pagopa.send.common.legal_notification.domain.NotificationDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BffInformalNotificationMapper {

    public Map<String, Object> toPayload(InformalNotificationCreationRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paProtocolNumber", request.paProtocolNumber());
        payload.put("subject", request.subject());
        payload.put("campaignId", request.campaignId());
        payload.put("senderDenomination", request.senderDenomination());
        if (request.group() != null) {
            payload.put("group", request.group());
        }

        List<Map<String, Object>> documents = request.documents().stream()
                .map(this::toDocumentMap)
                .toList();
        payload.put("documents", documents);

        List<Map<String, Object>> recipients = request.recipients().stream()
                .map(this::toRecipientMap)
                .toList();
        payload.put("recipients", recipients);

        return payload;
    }

    private Map<String, Object> toDocumentMap(DocumentRef doc) {
        return Map.of(
                "digests", Map.of("sha256", doc.sha256()),
                "contentType", doc.contentType(),
                "ref", Map.of(
                        "key", doc.key(),
                        "versionToken", doc.versionToken()
                )
        );
    }

    private Map<String, Object> toRecipientMap(ResolvedInformalRecipient recipient) {
        Map<String, Object> map = new HashMap<>();
        map.put("recipientType", recipient.recipientType().name());
        map.put("taxId", recipient.taxId());
        map.put("denomination", recipient.denomination());
        map.put("messageId", recipient.messageId());
        if (recipient.email() != null) {
            map.put("email", recipient.email());
        }
        if (recipient.phoneNumber() != null) {
            map.put("phoneNumber", recipient.phoneNumber());
        }
        if (recipient.additionalLanguage() != null) {
            map.put("additionalLanguage", recipient.additionalLanguage());
        }

        if (recipient.physicalAddress() != null) {
            map.put("physicalAddress", toPhysicalAddressMap(recipient.physicalAddress()));
        }

        if (!recipient.payments().isEmpty()) {
            List<Map<String, Object>> paymentsList = new ArrayList<>();
            for (ResolvedInformalPagoPaPayment p : recipient.payments()) {
                Map<String, Object> pagoPaDetails = new HashMap<>();
                pagoPaDetails.put("noticeCode", p.noticeCode());
                pagoPaDetails.put("creditorTaxId", p.creditorTaxId());
                pagoPaDetails.put("amount", p.amount());
                if (p.dueDate() != null && !p.dueDate().isBlank()) {
                    pagoPaDetails.put("dueDate", p.dueDate());
                }
                pagoPaDetails.put("attachment", toDocumentMap(p.attachment()));
                paymentsList.add(Map.of("pagoPa", pagoPaDetails));
            }
            map.put("payments", paymentsList);
        }

        return map;
    }

    private Map<String, Object> toPhysicalAddressMap(NotificationDefaults.PhysicalAddressDefaults addr) {
        Map<String, Object> map = new HashMap<>();
        if (addr.at() != null) map.put("at", addr.at());
        if (addr.address() != null) map.put("address", addr.address());
        if (addr.addressDetails() != null) map.put("addressDetails", addr.addressDetails());
        if (addr.zip() != null) map.put("zip", addr.zip());
        if (addr.municipality() != null) map.put("municipality", addr.municipality());
        if (addr.municipalityDetails() != null) map.put("municipalityDetails", addr.municipalityDetails());
        if (addr.province() != null) map.put("province", addr.province());
        if (addr.foreignState() != null) map.put("foreignState", addr.foreignState());
        return map;
    }
}
