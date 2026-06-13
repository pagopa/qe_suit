package it.pagopa.interop.web.page.producer_keychain.table;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.component.Button;

public interface ProducerKeychainActionButton extends Button {

    @XPath(".//div[contains(@class, 'MuiMenu-paper')]")
    interface ProducerKeychainMenu extends Component, Clickable {
        @XPath(".//li[contains(@class, 'MuiMenuItem-root') and contains(., 'Elimina')]")
        Button deleteBtn();
    }

    ProducerKeychainMenu actionMenu();

    default ProducerKeychainMenu openMenu(){
        this.click();
        return this.actionMenu();
    }
}
