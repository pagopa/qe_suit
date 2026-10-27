package it.pagopa.send.common.infrastructure.context;

import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;

public class InMemoryCurrentUserSession implements CurrentUserSession {
    private final ThreadLocal<User> currentUser = new ThreadLocal<>();

    @Override
    public void setUser(User user) {
        currentUser.set(user);
    }

    @Override
    public User getUser() {
        User user = currentUser.get();
        if (user == null) throw new IllegalStateException("Current user is not set");
        return user;
    }

    @Override
    public boolean hasUser() {
        return currentUser.get() != null;
    }
}
