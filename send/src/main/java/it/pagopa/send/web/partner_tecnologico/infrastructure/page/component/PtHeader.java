package it.pagopa.send.web.partner_tecnologico.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

/**
 * Header del BackOffice dei Partner Tecnologici. "Assistenza" è un pulsante, non un link: al click il portale apre il
 * client di posta verso l'indirizzo di assistenza configurato, che quindi non si legge dalla pagina.
 */
@XPath("//header")
public interface PtHeader extends Component {

    // il pulsante con il testo: su mobile c'è anche un'icona con la sola etichetta "Assistenza"
    @XPath(".//button[normalize-space()=\"Assistenza\"]")
    Button assistanceButton();

    @Override
    default void assertLoaded() {}
}
