package it.pagopa.interop.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonParameterTypes {

    private final CurrentUserSession currentUserSession;

    @ParameterType("l'utente|l'utente corrente")
    public User currentUser(String token) {
        return currentUserSession.getUser();
    }

    @ParameterType("admin|ADMIN|api|API|security|SECURITY|support|SUPPORT|API,SECURITY|api,security|reviewer|REVIEWER|viewer|VIEWER")
    public UserRole userRole(String name) {
        return UserRole.fromName(name);
    }

    @ParameterType("M2M_ADMIN|M2M")
    public M2MRole userM2MRole(String name) {
        return M2MRole.valueOf(name);
    }

    @ParameterType("AgID|Comune di Milano|Comune di Pozzallo|Comune di Comun Nuovo|PagoPA|Kyma|Sogecap|Sogessur")
    public Tenant tenant(String tenant) {
        return Tenant.fromAlias(tenant);
    }

    @ParameterType("GSP|PA|PRIVATE")
    public TenantKind tenantKind(String kind) {
        return TenantKind.valueOf(kind);
    }

    @ParameterType("BFF|bff")
    public Channel channel(String channel) {
        return Channel.valueOf(channel.toUpperCase());
    }

    @ParameterType("CONSUMER|Consumer|consumer|API|Api|api")
    public ClientKind clientType(String type) {
        return ClientKind.valueOf(type.toUpperCase());
    }

    @ParameterType("SUSPENDED")
    public PurposeVersionState purposeState(String name) {
        return PurposeVersionState.valueOf(name.toUpperCase());
    }

    @ParameterType("sincrono|asincrono")
    public Boolean asyncExchange(String name) {
        return "asincrono".equals(name);
    }

    @ParameterType("bozza|pubblicato")
    public EServiceDescriptorState eServiceDescriptorState(String state) {
        switch (state.toLowerCase()) {
            case "bozza":
                return EServiceDescriptorState.DRAFT;
            case "pubblicato":
                return EServiceDescriptorState.PUBLISHED;
            default:
                throw new IllegalArgumentException("Invalid EServiceDescriptorState: " + state);
        }
    }
}
