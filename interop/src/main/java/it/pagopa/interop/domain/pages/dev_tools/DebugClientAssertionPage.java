package it.pagopa.interop.domain.pages.dev_tools;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${interop.web.base-url}/tool-sviluppo/debug-voucher")
public interface DebugClientAssertionPage extends Page {

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[2]/div/div/h1")
    Readable<String> pageTitle();

    @XPath("//*[@id=\"clientAssertion\"]")
    Writable<String> clientAssertionInput();

    @XPath("//*[@id=\"clientId\"]")
    Writable<String> clientIdInput();

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[3]/div[2]/div/form/div/button[2]")
    Clickable submitButton();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert(webElm ->
                Assertions.assertThat(webElm.getText()).isNotBlank()
                        .containsIgnoringCase("Debug della client assertion")
        );
    }

    default void setClientAssertion(String clientAssertion) {
        clientAssertionInput().writeAndAssert(clientAssertion);
    }

    default void setClientId(String clientId) {
        clientIdInput().writeAndAssert(clientId);
    }

    default void validate(){
        submitButton().click();
    }
}
