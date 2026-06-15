package it.pagopa.interop.web.dev_tools;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.dev_tools.components.DebugClientAssertionSection;
import it.pagopa.interop.web.dev_tools.components.SimulatoreOttenimentoTokenSection;
import org.assertj.core.api.Assertions;

@Url("${interop.web.base-url}/tool-sviluppo")
public interface DevToolsPage extends Page {

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[2]/div/div/h1")
    Readable<String> pageTitle();

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[2]/div/div/p")
    Readable<String> pageSubtitle();

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[3]/div/div[3]/section")
    DebugClientAssertionSection debugClientAssertionSection();

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[3]/div/div[2]/section")
    SimulatoreOttenimentoTokenSection simulatoreOttenimentoTokenSection();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert(title ->
                Assertions.assertThat(title).isNotBlank()
                        .containsIgnoringCase("Tool per lo sviluppo")
        );

        pageSubtitle().readAndAssert("Una sezione che contiene alcuni strumenti utili a supporto dello sviluppo");

        debugClientAssertionSection().assertLoaded();
        simulatoreOttenimentoTokenSection().assertLoaded();
    }
}
