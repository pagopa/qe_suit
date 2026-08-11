package it.pagopa.interop.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.infrastructure.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonParameterTypes {

    private final CurrentUserSession currentUserSession;

    @ParameterType("l'utente|l'utente corrente")
    public User currentUser(String token) {
        return currentUserSession.getUser();
    }

    @ParameterType("admin|ADMIN|api|API|security|SECURITY|support|SUPPORT|API,SECURITY|api,security")
    public UserRole userRole(String name) {
        return UserRole.fromName(name);
    }

    @ParameterType("AgID|Comune di Milano|Comune di Pozzallo|Comune di Comun Nuovo|PagoPA|Kyma|Sogecap|Sogessur")
    public Tenant tenant(String tenant) {
        return Tenant.fromAlias(tenant);
    }

    @ParameterType("BFF|bff")
    public Channel channel(String channel) {
        return Channel.valueOf(channel.toUpperCase());
    }

    @ParameterType("CONSUMER|Consumer|consumer|API|Api|api")
    public ClientKind clientType(String type) {
        return ClientKind.valueOf(type);
    }

    @ParameterType("SUSPENDED")
    public PurposeVersionState purposeState(String name) {
        return PurposeVersionState.fromValue(name);
    }
}
