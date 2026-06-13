package it.pagopa.interop.web.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.domain.Component;

public interface InterfaceComponent extends Component {
    @XPath(".//button[contains(., 'Carica il file')]/..//input[@type='file']")
    Uploadable apiInterfaceAttachment();

    @XPath(".//button[contains(., 'Salva documento')]")
    Button saveAttachmentButton();

    default void uploadApiInterface(String interfacePath) {
        apiInterfaceAttachment().upload(interfacePath);
        saveAttachmentButton().click();
    }

    @Override
    default void assertLoaded() {

    }
}
