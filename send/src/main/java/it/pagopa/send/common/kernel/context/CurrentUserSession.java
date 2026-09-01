package it.pagopa.send.common.kernel.context;

import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.domain.Tenant;

import java.util.List;

public interface CurrentUserSession {
    void setSender(Tenant sender);

    Tenant getSender();

    void setRecipients(List<Recipient> recipients);

    List<Recipient> getRecipients();
}
