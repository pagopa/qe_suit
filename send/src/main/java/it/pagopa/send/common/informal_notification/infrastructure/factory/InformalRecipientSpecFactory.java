package it.pagopa.send.common.informal_notification.infrastructure.factory;

import it.pagopa.send.common.informal_notification.domain.InformalMessageSpec;
import it.pagopa.send.common.informal_notification.domain.InformalPagoPaPaymentSpec;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientType;
import it.pagopa.send.common.legal_notification.domain.NotificationDefaults;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InformalRecipientSpecFactory {

    private static final String TAX_ID_KEY = "taxId";
    private static final String DENOMINATION_KEY = "denomination";
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_NUMBER_KEY = "phoneNumber";
    private static final String RECIPIENT_TYPE_KEY = "recipientType";
    private static final String ADDITIONAL_LANGUAGE_KEY = "additionalLanguage";
    private static final String MSG_SUBJECT_KEY = "msg_subject";
    private static final String MSG_LONG_BODY_KEY = "msg_longBody";
    private static final String MSG_SHORT_BODY_KEY = "msg_shortBody";
    private static final String MSG_LANGUAGE_KEY = "msg_language";
    private static final String PHYSICAL_ADDRESS_KEY_PREFIX = "physicalAddress_";
    private static final String PAGOPA_PAYMENTS_COUNT_KEY = "pagoPA_number";
    private static final String PAGOPA_AMOUNT_KEY = "pagoPA_amount";
    private static final String AMOUNT_KEY = "amount";
    private static final String PAGOPA_DUE_DATE_KEY = "pagoPA_dueDate";
    private static final String DUE_DATE_KEY = "dueDate";
    private static final int DEFAULT_PAYMENTS_COUNT = 1;
    private static final long DEFAULT_PAYMENT_AMOUNT = 3000L;

    private final InformalNotificationDefaultsLoader defaultsLoader;

    public InformalRecipientSpec build(Recipient recipient, Map<String, String> data) {
        Map<String, String> remaining = new HashMap<>(data);
        Map<String, Object> defaults = defaultsLoader.load();

        String taxIdOverride = remaining.remove(TAX_ID_KEY);
        String denominationOverride = remaining.remove(DENOMINATION_KEY);
        String email = remaining.remove(EMAIL_KEY);
        if (email == null) {
            email = "complaint@simulator.amazonses.com";
        }
        String phoneNumber = remaining.remove(PHONE_NUMBER_KEY);
        if (phoneNumber == null) {
            phoneNumber = "+390000032181";
        }

        String recipientTypeStr = remaining.remove(RECIPIENT_TYPE_KEY);
        InformalRecipientType recipientType = recipientTypeStr != null
                ? InformalRecipientType.valueOf(recipientTypeStr)
                : (recipient.getType() == UserType.PG ? InformalRecipientType.PG : InformalRecipientType.PF);

        String additionalLanguage = remaining.remove(ADDITIONAL_LANGUAGE_KEY);

        NotificationDefaults.PhysicalAddressDefaults physicalAddress = resolvePhysicalAddress(remaining, defaults);
        InformalMessageSpec messageSpec = resolveMessageSpec(remaining, defaults);
        List<InformalPagoPaPaymentSpec> payments = resolvePayments(remaining);

        if (!remaining.isEmpty()) {
            throw new IllegalArgumentException("Chiavi di destinatario non riconosciute per informal notification: " + remaining.keySet());
        }

        return new InformalRecipientSpec(
                recipient,
                taxIdOverride,
                denominationOverride,
                recipientType,
                email,
                phoneNumber,
                additionalLanguage,
                physicalAddress,
                messageSpec,
                payments
        );
    }

    @SuppressWarnings("unchecked")
    private InformalMessageSpec resolveMessageSpec(Map<String, String> remaining, Map<String, Object> defaults) {
        Map<String, Object> defaultMsg = (Map<String, Object>) defaults.get("primaryMessage");
        String subject = Optional.ofNullable(remaining.remove(MSG_SUBJECT_KEY))
                .orElse((String) defaultMsg.get("subject"));
        String longBody = Optional.ofNullable(remaining.remove(MSG_LONG_BODY_KEY))
                .orElse((String) defaultMsg.get("longBody"));
        String shortBody = Optional.ofNullable(remaining.remove(MSG_SHORT_BODY_KEY))
                .orElse((String) defaultMsg.get("shortBody"));
        String language = Optional.ofNullable(remaining.remove(MSG_LANGUAGE_KEY))
                .orElse((String) defaultMsg.get("language"));

        return new InformalMessageSpec(subject, longBody, shortBody, language);
    }

    private List<InformalPagoPaPaymentSpec> resolvePayments(Map<String, String> remaining) {
        int pagoPaCount = Optional.ofNullable(remaining.remove(PAGOPA_PAYMENTS_COUNT_KEY))
                .map(Integer::parseInt)
                .orElse(DEFAULT_PAYMENTS_COUNT);

        String amountStr = remaining.remove(PAGOPA_AMOUNT_KEY);
        if (amountStr == null) {
            amountStr = remaining.remove(AMOUNT_KEY);
        }
        long amount = amountStr != null ? Long.parseLong(amountStr) : DEFAULT_PAYMENT_AMOUNT;

        String dueDate = remaining.remove(PAGOPA_DUE_DATE_KEY);
        if (dueDate == null) {
            dueDate = remaining.remove(DUE_DATE_KEY);
        }

        List<InformalPagoPaPaymentSpec> list = new ArrayList<>();
        for (int i = 0; i < pagoPaCount; i++) {
            list.add(new InformalPagoPaPaymentSpec(amount, dueDate));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private NotificationDefaults.PhysicalAddressDefaults resolvePhysicalAddress(
            Map<String, String> remaining,
            Map<String, Object> defaults
    ) {
        Map<String, Object> defAddress = (Map<String, Object>) defaults.get("physicalAddress");
        Map<String, String> addressOverrides = new HashMap<>();

        remaining.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(PHYSICAL_ADDRESS_KEY_PREFIX)) {
                addressOverrides.put(entry.getKey().substring(PHYSICAL_ADDRESS_KEY_PREFIX.length()), entry.getValue());
                return true;
            }
            return false;
        });

        return new NotificationDefaults.PhysicalAddressDefaults(
                addressOverrides.getOrDefault("at", (String) defAddress.get("at")),
                addressOverrides.getOrDefault("address", (String) defAddress.get("address")),
                addressOverrides.getOrDefault("addressDetails", (String) defAddress.get("addressDetails")),
                addressOverrides.getOrDefault("zip", (String) defAddress.get("zip")),
                addressOverrides.getOrDefault("municipality", (String) defAddress.get("municipality")),
                addressOverrides.getOrDefault("municipalityDetails", (String) defAddress.get("municipalityDetails")),
                addressOverrides.getOrDefault("province", (String) defAddress.get("province")),
                addressOverrides.getOrDefault("foreignState", (String) defAddress.get("foreignState"))
        );
    }
}
