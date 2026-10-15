package it.pagopa.send.common.legal_notification.infrastructure.factory;

import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RequestOverrideApplier;
import it.pagopa.send.common.legal_notification.domain.NotificationDefaults;
import it.pagopa.send.common.legal_notification.domain.PaymentSpec;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Costruisce un {@link RecipientSpec} a partire da un destinatario censito ({@link Recipient}) e
 * da una DataTable Cucumber di override puntuali: default statici sensati per i campi non
 * specificati (indirizzo fisico del template, taxId/denominazione del {@link Recipient} stesso),
 * sovrascrivibili singolarmente.
 * <p>
 * Di default ogni destinatario riceve 1 pagoPA + 1 F24 (sovrascrivibili anche a 0 tramite
 * {@value #PAGOPA_PAYMENTS_COUNT_KEY}/{@value #F24_PAYMENTS_COUNT_KEY}); la risoluzione reale
 * (posizione debitoria, preload allegato) resta interamente in
 * {@link LegalNotificationRequestFactory}, invariata: qui si decide solo quanti pagoPA/F24 il
 * destinatario vuole.
 */
@Component
@RequiredArgsConstructor
public class RecipientSpecFactory {

    private static final String TAX_ID_KEY = "taxId";
    private static final String DENOMINATION_KEY = "denomination";
    private static final String DIGITAL_DOMICILE_KEY = "digitalDomicile";
    private static final String PHYSICAL_ADDRESS_KEY_PREFIX = "physicalAddress_";
    private static final String PAGOPA_PAYMENTS_COUNT_KEY = "pagoPA_number";
    private static final String F24_PAYMENTS_COUNT_KEY = "F24_number";
    private static final int DEFAULT_PAYMENTS_COUNT = 1;

    private final NotificationDefaultsLoader defaultsLoader;

    public RecipientSpec build(Recipient recipient, Map<String, String> data) {
        Map<String, String> remaining = new HashMap<>(data);

        String taxIdOverride = remaining.remove(TAX_ID_KEY);
        String denominationOverride = remaining.remove(DENOMINATION_KEY);
        NotificationDefaults.PhysicalAddressDefaults physicalAddress = resolvePhysicalAddress(remaining);
        String digitalDomicile = resolveDigitalDomicile(remaining);
        List<PaymentSpec> payments = resolvePayments(remaining);

        if (!remaining.isEmpty()) {
            throw new IllegalArgumentException("Chiavi di destinatario non riconosciute: " + remaining.keySet());
        }

        return new RecipientSpec(recipient, taxIdOverride, denominationOverride, physicalAddress, digitalDomicile, payments);
    }

    private String resolveDigitalDomicile(Map<String, String> remaining) {
        String digitalDomicile = remaining.remove(DIGITAL_DOMICILE_KEY);
        return RequestOverrideApplier.NULL_TOKEN.equals(digitalDomicile) ? null : digitalDomicile;
    }

    private List<PaymentSpec> resolvePayments(Map<String, String> remaining) {
        int pagoPaCount = Optional.ofNullable(remaining.remove(PAGOPA_PAYMENTS_COUNT_KEY))
                .map(Integer::parseInt)
                .orElse(DEFAULT_PAYMENTS_COUNT);
        int f24Count = Optional.ofNullable(remaining.remove(F24_PAYMENTS_COUNT_KEY))
                .map(Integer::parseInt)
                .orElse(DEFAULT_PAYMENTS_COUNT);

        List<PaymentSpec> payments = new ArrayList<>(PaymentSpecFactory.defaultPagoPaPayments(pagoPaCount));
        payments.addAll(PaymentSpecFactory.defaultF24Payments(f24Count));
        return payments;
    }

    /**
     * Parte dal template di indirizzo fisico di default (lo stesso usato a livello di notifica) e
     * sovrascrive solo i sotto-campi esplicitati come {@code physicalAddress_*}.
     */
    private NotificationDefaults.PhysicalAddressDefaults resolvePhysicalAddress(Map<String, String> remaining) {
        NotificationDefaults.PhysicalAddressDefaults template = defaultsLoader.load().physicalAddress();

        return new NotificationDefaults.PhysicalAddressDefaults(
                overrideOrDefault(remaining, "at", template.at()),
                overrideOrDefault(remaining, "address", template.address()),
                overrideOrDefault(remaining, "addressDetails", template.addressDetails()),
                overrideOrDefault(remaining, "zip", template.zip()),
                overrideOrDefault(remaining, "municipality", template.municipality()),
                overrideOrDefault(remaining, "municipalityDetails", template.municipalityDetails()),
                overrideOrDefault(remaining, "province", template.province()),
                overrideOrDefault(remaining, "foreignState", template.foreignState())
        );
    }

    private String overrideOrDefault(Map<String, String> remaining, String suffix, String defaultValue) {
        String value = remaining.remove(PHYSICAL_ADDRESS_KEY_PREFIX + suffix);
        return value != null ? value : defaultValue;
    }
}
