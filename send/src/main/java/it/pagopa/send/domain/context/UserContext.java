package it.pagopa.send.domain.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.send.domain.User;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class UserContext {
    private User currentUser;

    public void set(User user) {
        this.currentUser = user;
    }

    public User getUser() {
        if (currentUser == null) throw new IllegalStateException("Current user is not set");
        return currentUser;
    }

}
