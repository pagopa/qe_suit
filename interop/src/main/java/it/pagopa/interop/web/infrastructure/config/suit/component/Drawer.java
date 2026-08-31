package it.pagopa.interop.web.infrastructure.config.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath("//div[contains(@class, 'MuiDrawer-root')][1]")
public interface Drawer extends Component {
    @XPath(".//button[1]")
    Clickable closeButton();

    default void close(){
        closeButton().click();
    }
}
