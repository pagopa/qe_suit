package it.pagopa.interop.common.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.domain.enums.Channel;
import it.pagopa.interop.common.domain.enums.User;
import it.pagopa.interop.common.domain.enums.UserRole;
import it.pagopa.interop.common.cucumber.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SuiteParameterType {

    private final CurrentUserContext currentUserContext;

    @ParameterType("l'utente|l'utente corrente")
    public User currentUser(String token) {
        return currentUserContext.getUser();
    }

    @ParameterType("admin|ADMIN|api|API|security|SECURITY|support|SUPPORT|API,SECURITY|api,security")
    public UserRole userRole(String name) {
        return UserRole.fromName(name);
    }

    @ParameterType("BFF|bff")
    public Channel channel(String channel) {
        return Channel.valueOf(channel.toUpperCase());
    }
}
