package it.pagopa.send.common.partner_tecnologico.application;

import it.pagopa.send.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

/**
 * Ricerca tra gli enti deleganti del Partner Tecnologico che ha effettuato l'accesso: ogni implementazione sa come
 * cercare gli enti sul proprio canale e come verificare che il filtro di ricerca sia vuoto.
 */
public interface PtInstitutionsGateway extends Plugin<Channel> {

    void searchInstitutions(String text);

    void verifySearchFilterIsEmpty();
}
