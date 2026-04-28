package it.pagopa.send.steps.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("https://uat.selfcare.pagopa.it/auth/logout/google")
public interface LogoutPage extends Page {

    @XPath("//*[@id=\"root\"]/div/div[1]/div/div/div[2]/div/h4")
    Readable<String> header();

    @Override
    default void assertLoaded() {
        header().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h.getText()).isIn("You have been logged out", "Notifications", "Sei stato disconnesso");
        });
    }
}
