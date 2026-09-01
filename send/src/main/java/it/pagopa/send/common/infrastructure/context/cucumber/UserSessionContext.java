package it.pagopa.send.common.infrastructure.context.cucumber;

import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSessionContext implements CurrentUserSession {
    private Tenant sender;
    private List<Recipient> recipients;
}
