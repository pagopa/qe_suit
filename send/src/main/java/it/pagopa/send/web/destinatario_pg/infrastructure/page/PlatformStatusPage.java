package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.pagopa.send.web.app_status.infrastructure.suit.AppStatusPage;

/**
 * {@code {baseUrl}/app-status}
 * Pagina "Stato della piattaforma" dell'impresa.
 * Si apre dalla voce "Stato della piattaforma" del menu laterale.
 * La pagina è uguale a quella del cittadino ({@link AppStatusPage}); testi, formato dello stato e dello storico sono
 * verificati da {@code WebPlatformStatusPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.app-status}")
public interface PlatformStatusPage extends AppStatusPage {
}
