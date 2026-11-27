package it.pagopa.send.payment.infrastructure;

import it.pagopa.send.bff.infrastructure.DebtPositionService;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.generated.openapi.clients.externalregistries.payment.model.PaymentInfoRequest;
import it.pagopa.send.generated.openapi.clients.externalregistries.payment.model.PaymentNotice;
import it.pagopa.send.generated.openapi.clients.externalregistries.payment.model.PaymentRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Richieste baseline delle operazioni di {@code pn-payment-info-external-v1.yaml}, valide per contratto: un
 * fallimento del contract test deve essere attribuibile alla mutazione del fuzzer, non al fixture.
 * <p>
 * Ogni richiesta referenzia un avviso pagoPA reale, creato su GPD per l'ente creditore di test
 * {@value #PAGOPA_TEST_CREDITOR_TAX_ID} con lo stesso {@link DebtPositionService} delle notifiche. Gli avvisi
 * sono creati una sola volta per debitore e slot e poi riusati: i supplier del validator sono invocati sia
 * in fase di generazione sia a ogni caso, e le tre operazioni non consumano l'avviso.
 */
@Component
public class PaymentInfoRequestFactory {

    static final String PAGOPA_TEST_CREDITOR_TAX_ID = "77777777777";
    static final int NOTICE_AMOUNT_EURO_CENTS = 1200;
    static final String COMPANY_NAME = "Automation";
    static final String DESCRIPTION = "Test Automation";
    /** Url di ritorno di Checkout: solo ASCII stampabile e al massimo 2048 caratteri, come da spec. */
    static final String CHECKOUT_RETURN_URL = "https://cittadini.notifichedigitali.it/notifiche";

    private final DebtPositionService debtPositionService;
    private final ConcurrentMap<String, Notice> notices = new ConcurrentHashMap<>();

    public PaymentInfoRequestFactory(DebtPositionService debtPositionService) {
        this.debtPositionService = debtPositionService;
    }

    /** Path params di {@code getPaymentInfo} (v1): codice fiscale dell'ente creditore e numero avviso. */
    public Map<String, Object> paymentInfoPathParams(User debtor) {
        Notice notice = notice(debtor, 0);
        return Map.of(
                "paTaxId", notice.creditorTaxId(),
                "noticeNumber", notice.noticeCode()
        );
    }

    /**
     * Body di {@code getPaymentInfoV21}: due avvisi distinti, cosi' il fuzzer muta ogni elemento della lista
     * singolarmente.
     */
    public PaymentInfoRequests paymentInfoRequests(User debtor) {
        PaymentInfoRequests requests = new PaymentInfoRequests();
        for (int slot = 0; slot < 2; slot++) {
            Notice notice = notice(debtor, slot);
            requests.add(new PaymentInfoRequest()
                    .creditorTaxId(notice.creditorTaxId())
                    .noticeCode(notice.noticeCode()));
        }
        return requests;
    }

    /** Body di {@code checkoutCart}: carrello di un solo avviso, con l'importo della posizione debitoria. */
    public PaymentRequest checkoutRequest(User debtor) {
        Notice notice = notice(debtor, 0);
        return new PaymentRequest()
                .paymentNotice(new PaymentNotice()
                        .noticeNumber(notice.noticeCode())
                        .fiscalCode(notice.creditorTaxId())
                        .amount(NOTICE_AMOUNT_EURO_CENTS)
                        .companyName(COMPANY_NAME)
                        .description(DESCRIPTION))
                .returnUrl(CHECKOUT_RETURN_URL);
    }

    private Notice notice(User debtor, int slot) {
        return notices.computeIfAbsent(debtor.getTaxId() + "#" + slot, key -> createNotice(debtor));
    }

    /** Il noticeCode pagoPA e' la cifra ausiliaria "3" seguita dallo IUV di 17 cifre generato da GPD. */
    private Notice createNotice(User debtor) {
        String iuv = debtPositionService.createDebtPosition(
                PAGOPA_TEST_CREDITOR_TAX_ID, debtorName(debtor), debtor.getTaxId(), NOTICE_AMOUNT_EURO_CENTS);
        return new Notice(PAGOPA_TEST_CREDITOR_TAX_ID, "3" + iuv);
    }

    private static String debtorName(User debtor) {
        if (debtor.getDenomination() != null && !debtor.getDenomination().isBlank()) {
            return debtor.getDenomination();
        }
        return (nullToEmpty(debtor.getName()) + " " + nullToEmpty(debtor.getFamilyName())).trim();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    record Notice(String creditorTaxId, String noticeCode) {
    }

    /**
     * Lista tipizzata del body v2.1. Il validator ricava il tipo del payload da {@code getClass()}: con un
     * {@code ArrayList} semplice il tipo degli elementi si perde per erasure e il decomposer non riesce a
     * correlare i campi di {@link PaymentInfoRequest}. La sottoclasse conserva il parametro generico.
     */
    public static final class PaymentInfoRequests extends ArrayList<PaymentInfoRequest> {
    }
}
