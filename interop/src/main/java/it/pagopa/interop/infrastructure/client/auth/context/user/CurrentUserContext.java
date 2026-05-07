package it.pagopa.interop.infrastructure.client.auth.context.user;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.User;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class CurrentUserContext {
    private User currentUser;
    private Tenant currentTenant;

    public void set(User user, Tenant tenant) {
        this.currentUser = user;
        this.currentTenant = tenant;
    }

    public User getUser() {
        if (currentUser == null) throw new IllegalStateException("Current user is not set");
        return currentUser;
    }

    public Tenant getTenant() {
        if (currentTenant == null) throw new IllegalStateException("Current tenant is not set");
        return currentTenant;
    }
}