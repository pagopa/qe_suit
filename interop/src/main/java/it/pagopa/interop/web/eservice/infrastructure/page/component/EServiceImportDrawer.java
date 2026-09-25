package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Drawer;

@XPath("//div[contains(@class, 'MuiDrawer-root')][1]")
public interface EServiceImportDrawer extends Drawer {

    @XPath(".//h6")
    Readable<String> title();

    @XPath(".//button[contains(., 'Carica il file')]/..//input[@type='file']")
    Uploadable zipAttachment();

    @XPath(".//*[contains(text(), 'Puoi caricare solo file con estensione')]")
    Readable<String> formatsHint();

    @Override
    default void assertLoaded() {
        title().readAndAssert("Importa E-Service");
    }
}

