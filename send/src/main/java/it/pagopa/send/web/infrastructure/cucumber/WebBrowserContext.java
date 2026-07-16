package it.pagopa.send.web.infrastructure.cucumber;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.common.kernel.domain.Recipient;
import it.pagopa.send.common.kernel.domain.Tenant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class WebBrowserContext {
    private Recipient currentUser;
    private Tenant currentTenant;

    @Getter @Setter
    private Page currentPage;

    @Getter @Setter
    private Page previousPage;

    public void set(Recipient user, Tenant tenant) {
        this.currentUser = user;
        this.currentTenant = tenant;
    }

    public Recipient getUser() {
        if (currentUser == null) throw new IllegalStateException("Current user is not set");
        return currentUser;
    }

    public Tenant getTenant() {
        if (currentTenant == null) throw new IllegalStateException("Current tenant is not set");
        return currentTenant;
    }

    public boolean isLoggedIn(Recipient user, Tenant tenant) {
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
