package it.pagopa.interop.domain.web.pages.dev_tools;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${interop.web.base-url}/tool-sviluppo")
public interface DevToolsPage extends Page {

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[2]/div/div/h1")
    Readable<String> pageTitle();

    @XPath("//a[contains(@class,'MuiButton-root') and contains(normalize-space(.), 'Effettua il debug')]")
    Clickable debugClientAssertionButton();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert(title ->
                Assertions.assertThat(title).isNotBlank()
                        .containsIgnoringCase("Tool per lo sviluppo")
        );
    }
}
