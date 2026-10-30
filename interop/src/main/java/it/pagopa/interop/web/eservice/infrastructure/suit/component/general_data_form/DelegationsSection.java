package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Checkbox;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.Switch;

@XPath(".//input[@name='isConsumerDelegable']/ancestor::*[contains(@class, 'MuiPaper-root')][1]")
public interface DelegationsSection extends Component {
    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//p")
    Readable<String> description();

    @XPath(".//a[contains(., 'documentazione')]")
    Link documentationLink();

    @XPath(".//h3[contains(., 'L’e-service può essere fruito tramite delega?')]")
    Label consumerDelegableTitle();

    @XPath(".//input[@name='isConsumerDelegable']/ancestor::span[contains(@class, 'MuiSwitch-root')][1]")
    Switch consumerDelegableSwitch();

    @XPath(".//*[@id='isConsumerDelegable-label']")
    Label consumerDelegableLabel();

    @XPath(".//h3[contains(., 'Autorizzo l’accesso ai dati in delega')]")
    Label clientAccessDelegableTitle();

    @XPath(".//input[@name='isClientAccessDelegable']/ancestor::span[contains(@class, 'MuiCheckbox-root')][1]")
    Checkbox clientAccessDelegableCheckbox();

    @XPath(".//*[@id='isClientAccessDelegable-label']")
    Label clientAccessDelegableLabel();

    @XPath(".//*[@id='isClientAccessDelegable-label']//a[contains(., 'documentazione')]")
    Link clientAccessDocLink();
}
