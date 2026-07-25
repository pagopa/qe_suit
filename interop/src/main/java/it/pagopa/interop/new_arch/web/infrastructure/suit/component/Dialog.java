package it.pagopa.interop.new_arch.web.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class, 'MuiDialog-paper')]")
public interface Dialog extends Component {
    @XPath(".//button[normalize-space()='Conferma']")
    Button confirmBtn();
}
