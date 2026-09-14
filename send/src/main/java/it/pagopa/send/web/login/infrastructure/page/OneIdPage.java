package it.pagopa.send.web.login.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;

/**
 * Questa pagina rappresenta la pagina di login di OneID per il mittente. Estende l'interfaccia AbstractOneIdPage e l'interfaccia Page.
 */
@Url("${url.notifiche.mittente.base}")
public interface OneIdPage extends AbstractOneIdPage, Page {
}
