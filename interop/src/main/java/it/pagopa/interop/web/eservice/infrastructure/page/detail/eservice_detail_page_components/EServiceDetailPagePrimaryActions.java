package it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

public interface EServiceDetailPagePrimaryActions extends Component {
    @XPath(".//button[normalize-space()='Richiedi fruizione']")
    Button agreementButton();
    @XPath(".//button[normalize-space()='Visualizza richiesta']")
    Button inspectAgreementButton();
    @XPath(".//button[normalize-space()='Completa richiesta']")
    Button completeAgreementButton();
    @XPath(".//button[normalize-space()='Aggiorna a nuova versione']")
    Button upgradeToNewVersionButton();
}
