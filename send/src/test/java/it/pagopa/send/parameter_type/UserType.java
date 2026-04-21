package it.pagopa.send.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.User;

public class UserType {

    @ParameterType("Grossini|FrancescoPetrarca|Lucrezia")
    public User user(String username) {
       return User.fromUsername(username);
    }

    @ParameterType("la PA|la PG|l'utente")
    public String userType(String userType) {
        if (userType.equals("la PA")) return "PA";
        if (userType.equals("la PG")) return "PG";
        if (userType.equals("l'utente")) return "utente";
        throw new IllegalArgumentException("Tipo utente non valido: " + userType);
    }
}
