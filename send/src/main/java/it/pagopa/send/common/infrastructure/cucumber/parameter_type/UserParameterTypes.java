package it.pagopa.send.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;

public class UserParameterTypes {

    @ParameterType("FrancescoPetrarca|Lucrezia")
    public Recipient recipient(String username) {
       return Recipient.fromUsername(username);
    }

    @ParameterType("Grossini")
    public Tenant tenant(String username) {
        return Tenant.fromUsername(username);
    }

    @ParameterType("la PG|l'utente")
    public String recipientType(String recipientType) {
        if (recipientType.equals("la PG")) return "PG";
        if (recipientType.equals("l'utente")) return "utente";
        throw new IllegalArgumentException("Tipo utente non valido: " + recipientType);
    }
}
