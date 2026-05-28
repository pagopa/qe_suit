package it.pagopa.interop.web.pages.dev_tools.debug_client_assertion;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.commons.component.Alert;
import it.pagopa.interop.web.commons.component.Button;
import it.pagopa.interop.web.commons.component.TextField;
import it.pagopa.interop.web.pages.dev_tools.debug_client_assertion.components.DebugRequestContentComponent;
import it.pagopa.interop.web.pages.dev_tools.debug_client_assertion.components.DebugResultComponent;
import org.assertj.core.api.SoftAssertions;

@Url("${interop.web.base-url}/tool-sviluppo/debug-voucher")
public interface DebugClientAssertionPage extends Page {
    String CLIENT_ASSERTION_INPUT_ERROR_ID = "clientAssertion-error";
    String CLIENT_ASSERTION_INPUT_HELPER_TEXT_ID = "clientAssertion-infoLabel";

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[2]/div/div/h1")
    Readable<String> pageTitle();

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[2]/div/div/h1/following-sibling::p[1]")
    Readable<String> subtitle();

    @XPath("//*[@id=\"clientAssertion\"]")
    TextField clientAssertionInput();

    @XPath("//*[@id=\"dpopProof\"]")
    TextField dpopProofInput();

    @XPath("//*[@id=\"clientId\"]")
    TextField clientIdInput();

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[3]/div[2]/div/form/div/button[2]")
    Button submitButton();

    Alert resultAlert();

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[3]/div/div/div[1]/section[1]")
    DebugResultComponent debugResults();

    @XPath("//*[@id=\"interop-sidenav-main\"]/div/main/div/div[3]/div/div/div[1]/section[2]")
    DebugRequestContentComponent requestContent();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            pageTitle().readAndAssert("Debug della client assertion");
            subtitle().readAndAssert("Se non riesci ad ottenere un voucher e non sai perché, puoi effettuare il debugging con questo simulatore e capire i possibili errori.");
            softly.assertThat(getClientAssertionHelpText())
                    .isEqualTo("La client assertion è un token JWT composto da una stringa che inizia per \"ey\". Copiala senza aggiungere spazi.");
        });
    }

    default void setClientAssertion(String clientAssertion) {
        clientAssertionInput().writeAndAssert(clientAssertion);
    }

    default String getClientAssertionErrorMessage() {
        return clientAssertionInput().getErrorMessage(CLIENT_ASSERTION_INPUT_ERROR_ID);
    }

    default String getClientAssertionHelpText() {
        return clientAssertionInput().getHelperText(CLIENT_ASSERTION_INPUT_HELPER_TEXT_ID);
    }

    default void setClientId(String clientId) {
        clientIdInput().writeAndAssert(clientId);
    }

    default void setDpopProof(String dpopProof) {
        dpopProofInput().writeAndAssert(dpopProof);
    }

    default void submitForm() {
        submitButton().click();
    }
}
