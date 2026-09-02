package it.pagopa.send.utils.factory;

import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.notification.domain.DocumentRef;
import it.pagopa.send.common.notification.domain.FeePolicy;
import it.pagopa.send.common.notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.notification.domain.PagoPaIntMode;
import it.pagopa.send.common.notification.domain.PhysicalCommunicationType;
import it.pagopa.send.common.notification.domain.ResolvedF24Payment;
import it.pagopa.send.common.notification.domain.ResolvedPagoPaPayment;
import it.pagopa.send.common.notification.domain.ResolvedPayment;
import it.pagopa.send.common.notification.domain.ResolvedRecipient;
import it.pagopa.send.controller.notifica_legale.RequestOverrideApplier;
import it.pagopa.send.model.F24PaymentSpec;
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
import java.util.UUID;

/**
 * Popola progressivamente una {@link LegalNotificationCreationRequest} (dominio interno, agnostico
 * dal DTO OpenAPI del canale), un pezzo per volta secondo lo step Cucumber corrente:
 * {@link #applyPreliminaryData} (dati base), {@link #resolveRecipient} (un destinatario alla
 * volta) e {@link #applySender} (mittente, noto solo all'invio). Chi orchestra le tre chiamate
 * sullo stesso oggetto è il gateway del canale scelto (es. {@code B2BLegalNotificationGateway}),
 * su cui vive la {@code LegalNotificationCreationRequest} in preparazione per lo scenario
 * corrente.
 * <p>
 * Ogni chiamata API reale (posizione debitoria, preload documenti) resta qui, non nel gateway:
 * questa classe non fa mai riferimento a un DTO OpenAPI.
 */
@Component
@RequiredArgsConstructor
public class LegalNotificationRequestFactory {

    private static final int PA_PROTOCOL_NUMBER_LENGTH = 13;
    private static final String PA_PROTOCOL_NUMBER_PREFIX = "9";

    // "77777777777" è il creditore di test condiviso usato in pn-b2b-client (PAYMENT_CREDITOR_TAX_ID).
    private static final String PAGOPA_TEST_CREDITOR_TAX_ID = "77777777777";
    private static final String ATTACHMENT_CONTENT_TYPE = "application/pdf";

    private final NotificationDefaultsLoader defaultsLoader;
    private final DebtPositionService debtPositionService;
    private final DocumentPreloadService documentPreloadService;
    private final PaGroupService paGroupService;

    /**
     * Compila i campi base della notifica (indipendenti dal mittente e dai destinatari) con i
     * default statici del template, sovrascritti puntualmente dalle chiavi riconosciute da
     * {@link RequestOverrideApplier} presenti in {@code data}.
     */
    public void applyPreliminaryData(LegalNotificationCreationRequest request, Map<String, String> data) {
        NotificationDefaults defaults = defaultsLoader.load();

        request.paProtocolNumber(generateParProtocolNumber())
                .subject(defaults.subject())
                .abstractText(defaults.abstractText())
                .document(buildAttachment())
                .feePolicy(FeePolicy.valueOf(defaults.notificationFeePolicy()))
                .physicalCommunication(PhysicalCommunicationType.valueOf(defaults.physicalCommunicationType()))
                .taxonomyCode(defaults.taxonomyCode())
                .paFee(defaults.paFee())
                .vat(defaults.vat())
                .pagoPaIntMode(PagoPaIntMode.valueOf(defaults.pagoPaIntMode()))
                .physicalCommunicationPriority(defaults.physicalCommunicationPriority())
                .additionalLanguages(null)
                .cancelledIun(null)
                .amount(null)
                .paymentExpirationDate(null);

        RequestOverrideApplier.apply(request, new HashMap<>(data));
    }

    /**
     * Risolve un destinatario (identità, indirizzo/domicilio già decisi da {@link RecipientSpec},
     * pagamenti con posizione debitoria e allegato reali) pronto per essere aggiunto alla request
     * in preparazione.
     */
    public ResolvedRecipient resolveRecipient(RecipientSpec spec) {
        Recipient recipient = spec.recipient();
        String taxId = spec.taxIdOverride() != null ? spec.taxIdOverride() : recipient.getTaxId();
        String denomination = spec.denominationOverride() != null ? spec.denominationOverride() : recipient.getName();

        List<ResolvedPayment> resolvedPayments = spec.payments().stream()
                .map(payment -> buildPaymentItem(payment, denomination, taxId))
                .toList();

        return new ResolvedRecipient(recipient.getType(), taxId, denomination, spec.physicalAddress(), spec.digitalDomicile(), resolvedPayments);
    }

    /**
     * Compila i campi che dipendono dal mittente, noto solo all'invio della notifica.
     */
    public void applySender(LegalNotificationCreationRequest request, Tenant sender) {
        request.senderDenomination(sender.getOrganization())
                .senderTaxId(sender.getTaxId())
                .group(paGroupService.findActiveGroupId(sender).orElse(null));
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

    private ResolvedPayment buildPaymentItem(PaymentSpec paymentSpec, String denomination, String taxId) {
        if (paymentSpec instanceof PagoPaPaymentSpec pagoPa) {
            return buildPagoPaPayment(pagoPa, denomination, taxId);
        }
        if (paymentSpec instanceof F24PaymentSpec f24) {
            return buildF24Payment(f24);
        }
        throw new IllegalArgumentException("Tipo di pagamento non gestito: " + paymentSpec.getClass());
    }

    private ResolvedPagoPaPayment buildPagoPaPayment(PagoPaPaymentSpec spec, String denomination, String taxId) {
        String noticeCode = "3" + debtPositionService.createDebtPosition(PAGOPA_TEST_CREDITOR_TAX_ID, denomination, taxId, spec.amount());
        return new ResolvedPagoPaPayment(noticeCode, PAGOPA_TEST_CREDITOR_TAX_ID, buildAttachment());
    }

    private ResolvedF24Payment buildF24Payment(F24PaymentSpec spec) {
        return new ResolvedF24Payment(spec.title(), buildAttachment());
    }

    private DocumentRef buildAttachment() {
        PreloadedDocument document = documentPreloadService.preloadTestPdf("doc-" + UUID.randomUUID());
        return new DocumentRef(document.key(), document.versionToken(), document.sha256(), ATTACHMENT_CONTENT_TYPE);
    }
}
