package it.pagopa.interop.web.debug_client_assertion.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

public interface DebugRequestContentComponent extends Component {
    String VOUCHER_TYPE_LABEL = "Voucher rilevato";
    String CLIENT_ASSERTION_LABEL = "Client_assertion";
    String CLIENT_ASSERTION_TYPE_LABEL = "Client_assertion_type";
    String GRANT_TYPE_LABEL = "Grant_type";
    String CLIENT_ID_LABEL = "Client_id";
    String DPOP_PROOF_LABEL = "DPoP proof";

    String VOUCHER_LABEL_CONTAINER_XPATH = ".//div[contains(@class, 'MuiBox-root')][p[contains(@class, 'MuiTypography-root') and text()='" + VOUCHER_TYPE_LABEL + "']]";
    String CLIENT_ASSERTION_LABEL_CONTAINER_XPATH = ".//div[contains(@class, 'MuiBox-root')][p[contains(@class, 'MuiTypography-root') and text()='" + CLIENT_ASSERTION_LABEL + "']]";
    String CLIENT_ASSERTION_TYPE_LABEL_CONTAINER_XPATH = ".//div[contains(@class, 'MuiBox-root')][p[contains(@class, 'MuiTypography-root') and text()='" + CLIENT_ASSERTION_TYPE_LABEL + "']]";
    String GRANT_TYPE_LABEL_CONTAINER_XPATH = ".//div[contains(@class, 'MuiBox-root')][p[contains(@class, 'MuiTypography-root') and text()='" + GRANT_TYPE_LABEL + "']]";
    String CLIENT_ID_LABEL_CONTAINER_XPATH = ".//div[contains(@class, 'MuiBox-root')][p[contains(@class, 'MuiTypography-root') and text()='" + CLIENT_ID_LABEL + "']]";
    String DPOP_LABEL_CONTAINER_XPATH = ".//div[contains(@class, 'MuiBox-root')][p[contains(@class, 'MuiTypography-root') and text()='" + DPOP_PROOF_LABEL + "']]";

    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//h2/following::p[1]")
    Readable<String> subtitle();

    @XPath(VOUCHER_LABEL_CONTAINER_XPATH + "//p")
    Readable<String> voucherLabel();

    @XPath(VOUCHER_LABEL_CONTAINER_XPATH + "/following-sibling::div[contains(@class, 'MuiBox-root')][1]//p")
    Readable<String> voucherValue();

    @XPath(CLIENT_ID_LABEL_CONTAINER_XPATH + "//p")
    Readable<String> clientIdLabel();

    @XPath(CLIENT_ID_LABEL_CONTAINER_XPATH + "/following-sibling::div[contains(@class, 'MuiBox-root')][1]//p")
    Readable<String> clientIdValue();

    @XPath(CLIENT_ASSERTION_LABEL_CONTAINER_XPATH + "//p")
    Readable<String> clientAssertionLabel();

    @XPath(CLIENT_ASSERTION_LABEL_CONTAINER_XPATH + "/following-sibling::div[contains(@class, 'MuiBox-root')][1]//p")
    Readable<String> clientAssertionValue();

    @XPath(DPOP_LABEL_CONTAINER_XPATH + "//p")
    Readable<String> dpopProofLabel();

    @XPath(DPOP_LABEL_CONTAINER_XPATH + "/following-sibling::div[contains(@class, 'MuiBox-root')][1]//p")
    Readable<String> dpopProofValue();

    @XPath(CLIENT_ASSERTION_TYPE_LABEL_CONTAINER_XPATH + "//p")
    Readable<String> clientAssertionTypeLabel();

    @XPath(CLIENT_ASSERTION_TYPE_LABEL_CONTAINER_XPATH + "/following-sibling::div[contains(@class, 'MuiBox-root')][1]//p")
    Readable<String> clientAssertionTypeValue();

    @XPath(GRANT_TYPE_LABEL_CONTAINER_XPATH + "//p")
    Readable<String> grantTypeLabel();

    @XPath(GRANT_TYPE_LABEL_CONTAINER_XPATH + "/following-sibling::div[contains(@class, 'MuiBox-root')][1]//p")
    Readable<String> grantTypeValue();


    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            title().readAndAssert("Il contenuto della tua richiesta");
            subtitle().readAndAssert("Di seguito il body (payload) della richiesta che è stata inoltrata al server autorizzativo di PDND Interoperabilità");
            voucherLabel().readAndAssert(VOUCHER_TYPE_LABEL);
            clientAssertionLabel().readAndAssert(CLIENT_ASSERTION_LABEL);
            clientAssertionTypeLabel().readAndAssert(CLIENT_ASSERTION_TYPE_LABEL);
            grantTypeLabel().readAndAssert(GRANT_TYPE_LABEL);
        });
    }

    default void verifyVoucherType(String voucherType) {
        if (voucherType != null) {
            SoftAssertions.assertSoftly(softly -> {
                voucherLabel().readAndAssert(VOUCHER_TYPE_LABEL);
                voucherValue().readAndAssert(voucherType);
            });
        }
    }

    default void verifyClientId(String clientId) {
        if (clientId != null) {
            SoftAssertions.assertSoftly(softly -> {
                clientIdLabel().readAndAssert(CLIENT_ID_LABEL);
                clientIdValue().readAndAssert(clientId);
            });
        }
    }

    default void verifyClientAssertion(String clientAssertion) {
        if (clientAssertion != null) {
            SoftAssertions.assertSoftly(softly -> {
                clientAssertionLabel().readAndAssert(CLIENT_ASSERTION_LABEL);
                clientAssertionValue().readAndAssert(clientAssertion);
            });
        }
    }

    default void verifyDpopProof(String dpopProof) {
        if (dpopProof != null) {
            SoftAssertions.assertSoftly(softly -> {
                dpopProofLabel().readAndAssert(DPOP_PROOF_LABEL);
                dpopProofValue().readAndAssert(dpopProof);
            });
        }
    }

    default void verifyClientAssertionType(String clientAssertionType) {
        if (clientAssertionType != null) {
            SoftAssertions.assertSoftly(softly -> {
                clientAssertionTypeLabel().readAndAssert(CLIENT_ASSERTION_TYPE_LABEL);
                clientAssertionTypeValue().readAndAssert(clientAssertionType);
            });
        }
    }

    default void verifyGrantType(String grantType) {
        if (grantType != null) {
            SoftAssertions.assertSoftly(softly -> {
                grantTypeLabel().readAndAssert(GRANT_TYPE_LABEL);
                grantTypeValue().readAndAssert(grantType);
            });
        }
    }
}
