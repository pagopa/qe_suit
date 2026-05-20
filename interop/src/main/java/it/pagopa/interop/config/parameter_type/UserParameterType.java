package it.pagopa.interop.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.domain.enums.UserRole;
import it.pagopa.interop.domain.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserParameterType {

    private final CurrentUserContext currentUserContext;

    @ParameterType("l'utente|l'utente corrente")
    public User currentUser(String token) {
        return currentUserContext.getUser();
    }

    @ParameterType("admin|ADMIN|api|API|security|SECURITY|support|SUPPORT|API,SECURITY|api,security")
    public UserRole userRole(String name) {
        return UserRole.fromName(name);
    }
}
