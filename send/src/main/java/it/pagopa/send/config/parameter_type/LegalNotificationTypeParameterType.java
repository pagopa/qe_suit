package it.pagopa.send.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.send.model.LegalNotificationType;

public class LegalNotificationTypeParameterType {

    @ParameterType("semplice|singolo destinatario con bollettino pagoPA|singolo destinatario con bollettino F24")
    public LegalNotificationType legalNotificationType(String raw) {
        return switch (raw) {
            case "semplice" -> LegalNotificationType.SIMPLE;
            case "singolo destinatario con bollettino pagoPA" -> LegalNotificationType.SINGLE_RECIPIENT_WITH_PAGOPA_PAYMENT;
            case "singolo destinatario con bollettino F24" -> LegalNotificationType.SINGLE_RECIPIENT_WITH_PAGOPA_PAYMENT;
            default -> throw new IllegalArgumentException("Tipo notifica non valido: " + raw);
        };
    }
}
