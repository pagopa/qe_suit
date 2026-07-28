package it.pagopa.send.utils.factory;

import it.pagopa.send.common.kernel.domain.Recipient;
import it.pagopa.send.common.kernel.domain.Tenant;
import it.pagopa.send.controller.notifica_legale.RequestOverrideApplier;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.F24Payment;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationAttachmentBodyRef;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationAttachmentDigests;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationDigitalAddress;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationDocument;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationFeePolicy;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationMetadataAttachment;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationPaymentAttachment;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationPaymentItem;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationPhysicalAddress;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationRecipientV24;
import it.pagopa.send.generated.openapi.clients.bff.model.PagoPaPayment;
import it.pagopa.send.model.F24PaymentSpec;
import it.pagopa.send.model.LegalNotificationType;
import it.pagopa.send.model.NotificationDefaults;
import it.pagopa.send.model.PagoPaPaymentSpec;
import it.pagopa.send.model.PaymentSpec;
import it.pagopa.send.model.PreloadedDocument;
import it.pagopa.send.model.RecipientSpec;
import it.pagopa.send.service.DebtPositionService;
import it.pagopa.send.service.DocumentPreloadService;
import it.pagopa.send.service.PaGroupService;
import it.pagopa.send.utils.RandomNumericGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Costruisce la {@code BffNewNotificationRequest} a partire da un {@link LegalNotificationType},
 * un {@link Tenant} mittente e uno o più {@link RecipientSpec}. I default statici (senza side
 * effect) vengono da un template YAML per tipo (vedi {@link NotificationDefaultsLoader}); tutto
 * ciò che richiede una chiamata API o è calcolato a runtime (posizioni debitorie, preload
 * documenti, paProtocolNumber, group) resta qui. A meno di override espliciti (una {@code Map}
 * chiave/valore, tipicamente proveniente da una Cucumber DataTable).
 * <p>
 * Alcune chiavi di override sono direttive riconosciute esplicitamente prima della costruzione,
 * perché comportano side effect reali (creare N posizioni debitorie) o la sostituzione di un intero
 * nodo annidato (indirizzo fisico) che non è raggiungibile dai setter fluenti di primo livello di
 * {@code BffNewNotificationRequest}: {@link #PHYSICAL_ADDRESS_OVERRIDE_KEY},
 * {@link #DIGITAL_DOMICILE_OVERRIDE_KEY}, {@link #PAGOPA_PAYMENTS_COUNT_OVERRIDE_KEY}. Tutte le
 * altre chiavi passano invece a {@link RequestOverrideApplier}, che le applica per riflessione sui
 * setter fluenti di primo livello della request già costruita.
 */
@Component
@RequiredArgsConstructor
public class LegalNotificationRequestFactory {

    private static final int PA_PROTOCOL_NUMBER_LENGTH = 13;
    private static final String PA_PROTOCOL_NUMBER_PREFIX = "9";

    // "77777777777" è il creditore di test condiviso usato in pn-b2b-client (PAYMENT_CREDITOR_TAX_ID).
    private static final String PAGOPA_TEST_CREDITOR_TAX_ID = "77777777777";
    private static final int DEFAULT_PAGOPA_PAYMENTS_COUNT = 1;

    // Direttive di override riconosciute prima della costruzione: non sono setter fluenti di
    // BffNewNotificationRequest, quindi vanno rimosse dalla mappa prima di passarla a
    // RequestOverrideApplier, altrimenti fallirebbe non trovando un setter corrispondente.
    private static final String PHYSICAL_ADDRESS_OVERRIDE_KEY = "physicalAddress";
    private static final String DIGITAL_DOMICILE_OVERRIDE_KEY = "digitalDomicile";
    private static final String PAGOPA_PAYMENTS_COUNT_OVERRIDE_KEY = "pagoPA_number";

    private final NotificationDefaultsLoader defaultsLoader;
    private final DebtPositionService debtPositionService;
    private final DocumentPreloadService documentPreloadService;
    private final PaGroupService paGroupService;

    public BffNewNotificationRequest build(LegalNotificationType type, Tenant sender, List<RecipientSpec> recipientSpecs, Map<String, String> overrides) {
        Map<String, String> remainingOverrides = new HashMap<>(overrides);
        NotificationDefaults defaults = defaultsLoader.load(type);

        List<RecipientSpec> resolvedRecipients = resolvePagoPaDefaults(type, recipientSpecs, remainingOverrides);
        boolean hasAnyPayment = resolvedRecipients.stream().anyMatch(spec -> !spec.payments().isEmpty());
        PreloadedDocument paymentAttachment = hasAnyPayment
                ? documentPreloadService.preloadTestPdf("pay-" + UUID.randomUUID())
                : null;

        BffNewNotificationRequest request = new BffNewNotificationRequest()
                .paProtocolNumber(generateParProtocolNumber())
                .subject(defaults.subject())
                ._abstract(defaults.abstractText())
                .addDocumentsItem(buildMainDocument())
                .notificationFeePolicy(NotificationFeePolicy.fromValue(defaults.notificationFeePolicy()))
                .physicalCommunicationType(BffNewNotificationRequest.PhysicalCommunicationTypeEnum.fromValue(defaults.physicalCommunicationType()))
                .senderDenomination(sender.getOrganization())
                .senderTaxId(sender.getTaxId())
                .group(paGroupService.findActiveGroupId(sender).orElse(null))
                .taxonomyCode(defaults.taxonomyCode())
                .paFee(defaults.paFee())
                .vat(defaults.vat())
                .pagoPaIntMode(BffNewNotificationRequest.PagoPaIntModeEnum.fromValue(defaults.pagoPaIntMode()))
                .physicalCommunicationPriority(defaults.physicalCommunicationPriority())
                .additionalLanguages(null)
                .cancelledIun(null)
                .amount(null)
                .paymentExpirationDate(null);

        resolvedRecipients.forEach(spec -> request.addRecipientsItem(buildRecipient(spec, defaults.physicalAddress(), paymentAttachment, remainingOverrides)));

        RequestOverrideApplier.apply(request, remainingOverrides);
        return request;
    }

    /**
     * Per il tipo che prevede il bollettino pagoPA, ogni destinatario a cui non è già stato
     * associato esplicitamente un {@link PaymentSpec} riceve di default un solo pagoPA, salvo
     * override tramite {@value #PAGOPA_PAYMENTS_COUNT_OVERRIDE_KEY}. Per il tipo semplice i
     * destinatari non vengono toccati: restano senza pagamenti.
     */
    private List<RecipientSpec> resolvePagoPaDefaults(LegalNotificationType type, List<RecipientSpec> recipientSpecs, Map<String, String> overrides) {
        if (!type.requiresPagoPaPayment()) {
            return recipientSpecs;
        }

        int pagoPaCount = Optional.ofNullable(overrides.remove(PAGOPA_PAYMENTS_COUNT_OVERRIDE_KEY))
                .map(Integer::parseInt)
                .orElse(DEFAULT_PAGOPA_PAYMENTS_COUNT);

        return recipientSpecs.stream()
                .map(spec -> spec.payments().isEmpty() ? RecipientSpec.of(spec.recipient(), defaultPagoPaPayments(pagoPaCount)) : spec)
                .toList();
    }

    private List<PaymentSpec> defaultPagoPaPayments(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> (PaymentSpec) PagoPaPaymentSpec.withDefaultAmount())
                .toList();
    }

    /**
     * Lunghezza fissa e formato numerico come un notice code pagoPA, versione semplificata del
     * generatore usato in pn-b2b-client: prefisso {@value #PA_PROTOCOL_NUMBER_PREFIX} (per
     * distinguerlo a colpo d'occhio da un notice code reale, che inizia sempre per "3") seguito da
     * {@value #PA_PROTOCOL_NUMBER_LENGTH} cifre casuali.
     */
    private String generateParProtocolNumber() {
        return PA_PROTOCOL_NUMBER_PREFIX + RandomNumericGenerator.generate(PA_PROTOCOL_NUMBER_LENGTH);
    }

    private NotificationDocument buildMainDocument() {
        PreloadedDocument mainDocument = documentPreloadService.preloadTestPdf("doc-" + UUID.randomUUID());
        return new NotificationDocument()
                .digests(new NotificationAttachmentDigests().sha256(mainDocument.sha256()))
                .contentType("application/pdf")
                .ref(new NotificationAttachmentBodyRef().key(mainDocument.key()).versionToken(mainDocument.versionToken()));
    }

    private NotificationRecipientV24 buildRecipient(RecipientSpec spec, NotificationDefaults.PhysicalAddressDefaults addressDefaults, PreloadedDocument paymentAttachment, Map<String, String> overrides) {
        Recipient recipient = spec.recipient();

        List<NotificationPaymentItem> paymentItems = spec.payments().stream()
                .map(payment -> buildPaymentItem(payment, recipient, paymentAttachment))
                .toList();

        return new NotificationRecipientV24()
                .recipientType(recipient.recipientType())
                .taxId(recipient.getTaxId())
                .denomination(recipient.getDenomination())
                .physicalAddress(resolvePhysicalAddress(addressDefaults, overrides))
                .digitalDomicile(resolveDigitalDomicile(overrides))
                .payments(paymentItems.isEmpty() ? null : paymentItems);
    }

    private NotificationPhysicalAddress resolvePhysicalAddress(NotificationDefaults.PhysicalAddressDefaults addressDefaults, Map<String, String> overrides) {
        if (RequestOverrideApplier.NULL_TOKEN.equals(overrides.remove(PHYSICAL_ADDRESS_OVERRIDE_KEY))) {
            return null;
        }
        return new NotificationPhysicalAddress()
                .at(addressDefaults.at())
                .address(addressDefaults.address())
                .addressDetails(addressDefaults.addressDetails())
                .zip(addressDefaults.zip())
                .municipality(addressDefaults.municipality())
                .municipalityDetails(addressDefaults.municipalityDetails())
                .province(addressDefaults.province())
                .foreignState(addressDefaults.foreignState());
    }

    /**
     * A differenza dell'indirizzo fisico, il domicilio digitale non ha un default: viene popolato
     * solo se esplicitato in override (valore = indirizzo PEC).
     */
    private NotificationDigitalAddress resolveDigitalDomicile(Map<String, String> overrides) {
        String address = overrides.remove(DIGITAL_DOMICILE_OVERRIDE_KEY);
        if (address == null) {
            return null;
        }
        return new NotificationDigitalAddress()
                .type(NotificationDigitalAddress.TypeEnum.PEC)
                .address(address);
    }

    private NotificationPaymentItem buildPaymentItem(PaymentSpec paymentSpec, Recipient recipient, PreloadedDocument attachment) {
        if (paymentSpec instanceof PagoPaPaymentSpec pagoPa) {
            return new NotificationPaymentItem().pagoPa(buildPagoPaPayment(pagoPa, recipient, attachment));
        }
        if (paymentSpec instanceof F24PaymentSpec f24) {
            return new NotificationPaymentItem().f24(buildF24Payment(f24, attachment));
        }
        throw new IllegalArgumentException("Tipo di pagamento non gestito: " + paymentSpec.getClass());
    }

    private PagoPaPayment buildPagoPaPayment(PagoPaPaymentSpec spec, Recipient recipient, PreloadedDocument attachment) {
        String noticeCode = "3" + debtPositionService.createDebtPosition(
                PAGOPA_TEST_CREDITOR_TAX_ID, recipient.getDenomination(), recipient.getTaxId(), spec.amount());

        return new PagoPaPayment()
                .noticeCode(noticeCode)
                .creditorTaxId(PAGOPA_TEST_CREDITOR_TAX_ID)
                .applyCost(false)
                .attachment(new NotificationPaymentAttachment()
                        .digests(new NotificationAttachmentDigests().sha256(attachment.sha256()))
                        .contentType("application/pdf")
                        .ref(new NotificationAttachmentBodyRef().key(attachment.key()).versionToken(attachment.versionToken())));
    }

    private F24Payment buildF24Payment(F24PaymentSpec spec, PreloadedDocument attachment) {
        return new F24Payment()
                .title(spec.title())
                .applyCost(false)
                .metadataAttachment(new NotificationMetadataAttachment()
                        .digests(new NotificationAttachmentDigests().sha256(attachment.sha256()))
                        .contentType("application/pdf")
                        .ref(new NotificationAttachmentBodyRef().key(attachment.key()).versionToken(attachment.versionToken())));
    }
}
