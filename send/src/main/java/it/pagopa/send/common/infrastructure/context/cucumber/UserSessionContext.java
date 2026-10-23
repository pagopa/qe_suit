package it.pagopa.send.common.infrastructure.context.cucumber;

import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSessionContext implements CurrentUserSession {
    private User sender;
    private List<User> recipients;

    @Override
    public User getCurrentActor() {
        if (sender != null) return sender;
        if (recipients != null && !recipients.isEmpty()) return recipients.get(0);
        return null;
    }
}
