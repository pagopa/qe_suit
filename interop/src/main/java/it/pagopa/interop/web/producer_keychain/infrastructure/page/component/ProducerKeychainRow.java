package it.pagopa.interop.web.producer_keychain.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.suit.component.Button;
import it.pagopa.suit.component.Dialog;

@XPath(".//tr[contains(@class, 'MuiTableRow-root')]")
public interface ProducerKeychainRow extends Component {

    @XPath("(.//td)[1]")
    Readable<String> name();

    @XPath("(.//td)[2]//a")
    Button detailBtn();

    @XPath("(.//td)[2]//span//button")
    ProducerKeychainActionButton actionBtn();

    Dialog deleteDialog();

    default void delete(){
        actionBtn()
                .openMenu()
                .deleteBtn()
                .click();

        deleteDialog().confirmBtn().click();
    }

}
