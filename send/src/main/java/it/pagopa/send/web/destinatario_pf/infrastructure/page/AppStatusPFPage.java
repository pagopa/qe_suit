package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.pagopa.send.web.app_status.infrastructure.suit.AppStatusPage;

/**
 * {@code {baseUrl}/app-status}
 * Pagina "Stato della piattaforma" del cittadino.
 * Si apre dalla voce "Stato della piattaforma" del menu laterale.
 * La pagina è uguale a quella dell'impresa ({@link AppStatusPage}); testi, formato dello stato e dello storico sono
 * verificati da {@code WebAppStatusPFContractTest}.
 */
@Url("${url.notifiche.cittadino.app-status}")
public interface AppStatusPFPage extends AppStatusPage {
}
