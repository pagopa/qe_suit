package it.pagopa.send.domain.web.pages.destinatario.pf.login;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.domain.web.commons.pages.login.AbstractOneIdPage;

@Url("${url.notifiche.cittadino.base}")
public interface PfLoginPage extends AbstractOneIdPage, Page {
    // Il login PF non richiede passi aggiuntivi oltre a quelli comuni definiti in AbstractOneIdPage
}
