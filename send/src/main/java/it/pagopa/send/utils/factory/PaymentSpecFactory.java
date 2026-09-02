package it.pagopa.send.utils.factory;

import it.pagopa.send.model.F24PaymentSpec;
import it.pagopa.send.model.PagoPaPaymentSpec;
import it.pagopa.send.model.PaymentSpec;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Costruisce {@link PaymentSpec} "vuoti" (nessun side-effect, nessuna chiamata API: solo importo
 * di default per pagoPA / titolo di default per F24), in base a un conteggio. Condiviso tra il
 * percorso legacy type-driven ({@link LegalNotificationRequestFactory}, un solo destinatario, il
 * conteggio dipende dal {@code LegalNotificationType}) e il percorso per-destinatario count-driven
 * del flusso multi-destinatario ({@link RecipientSpecFactory}, il conteggio è esplicito per
 * singolo destinatario).
 */
public final class PaymentSpecFactory {

    private PaymentSpecFactory() {
    }

    public static List<PaymentSpec> defaultPagoPaPayments(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> (PaymentSpec) PagoPaPaymentSpec.withDefaultAmount())
                .toList();
    }

    public static List<PaymentSpec> defaultF24Payments(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> (PaymentSpec) F24PaymentSpec.withDefaultTitle())
                .toList();
    }
}
