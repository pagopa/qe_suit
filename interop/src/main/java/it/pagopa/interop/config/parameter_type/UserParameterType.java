package it.pagopa.interop.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.domain.enums.UserRole;

public class UserParameterType {
    @ParameterType("admin|ADMIN|api|API|security|SECURITY|support|SUPPORT|API,SECURITY|api,security")
    public UserRole userRole(String name) {
        return UserRole.fromName(name);
    }

    @ParameterType("l'utente corrente|l'utente")
    public User currentUser() {
        return null;
    }
}
