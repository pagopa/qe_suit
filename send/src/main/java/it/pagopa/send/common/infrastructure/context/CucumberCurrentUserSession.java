package it.pagopa.send.common.infrastructure.context;

import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;

public class CucumberCurrentUserSession implements CurrentUserSession {
    private User currentUser;

    @Override
    public void setUser(User user) {
        this.currentUser = user;
    }

    @Override
    public User getUser() {
        if (currentUser == null) throw new IllegalStateException("Current user is not set");
        return currentUser;
    }

    @Override
    public boolean hasUser() {
        return currentUser != null;
    }

    public void reset() {
        this.currentUser = null;
    }
}
