package it.pagopa.interop.web.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.common.enums.Tenant;
import it.pagopa.interop.common.enums.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class BrowserContext {
    private User currentUser;
    private Tenant currentTenant;

    @Getter @Setter
    private Page currentPage;

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

    public boolean isLoggedIn(User user, Tenant tenant) {
        return this.currentUser != null
                && this.currentTenant != null
                && this.currentUser == user
                && this.currentTenant == tenant;
    }

    public void logout() {
        this.currentUser = null;
        this.currentTenant = null;
    }
}
