package it.pagopa.interop.infrastructure.client.auth.context.user;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class CurrentUserContext {
    private CurrentUser currentUser;

    public void set(CurrentUser user) { this.currentUser = user; }

    public CurrentUser getUser() {
        if (currentUser == null) throw new IllegalStateException("Current user is not set");
        return currentUser;
    }
}