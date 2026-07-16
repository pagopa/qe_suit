package it.pagopa.send.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.send.common.kernel.domain.Recipient;

public class UserType {

    @ParameterType("Grossini|FrancescoPetrarca|Lucrezia")
    public Recipient user(String username) {
       return Recipient.fromUsername(username);
    }

    @ParameterType("la PA|la PG|l'utente")
    public String userType(String userType) {
        if (userType.equals("la PA")) return "PA";
        if (userType.equals("la PG")) return "PG";
        if (userType.equals("l'utente")) return "utente";
        throw new IllegalArgumentException("Tipo utente non valido: " + userType);
    }
}
