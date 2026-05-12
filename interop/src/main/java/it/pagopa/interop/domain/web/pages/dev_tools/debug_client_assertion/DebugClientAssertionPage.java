package it.pagopa.interop.domain.web.pages.dev_tools.debug_client_assertion;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.domain.web.pages.dev_tools.debug_client_assertion.components.DebugResultComponent;
import lombok.experimental.Delegate;
import org.assertj.core.api.Assertions;

@Url("${interop.web.base-url}/tool-sviluppo/debug-voucher")
public interface DebugClientAssertionPage extends Page {

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[2]/div/div/h1")
    Readable<String> pageTitle();

    @XPath("//*[@id=\"clientAssertion\"]")
    Writable<String> clientAssertionInput();

    @XPath("//*[@id=\"dpopProof\"]")
    Writable<String> dpopProofInput();

    @XPath("//*[@id=\"clientId\"]")
    Writable<String> clientIdInput();

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[3]/div[2]/div/form/div/button[2]")
    Clickable submitButton();

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[3]/div/div/div[1]/section[1]")
    DebugResultComponent debugResults();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert(title ->
                Assertions.assertThat(title).isNotBlank()
                        .containsIgnoringCase("Debug della client assertion")
        );
    }

    default void setClientAssertion(String clientAssertion) {
        clientAssertionInput().writeAndAssert(clientAssertion);
    }

    default void setClientId(String clientId) {
        clientIdInput().writeAndAssert(clientId);
    }

    default void setDpopProof(String dpopProof) {
        dpopProofInput().writeAndAssert(dpopProof);
    }

    default void submitForm() {
        submitButton().click();
        debugResults().assertLoaded();
    }
}
