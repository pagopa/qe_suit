package it.pagopa.send.steps.destinatari.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;

@Url("${url.notifiche.cittadino.base}/onboarding")
public interface ConfigureAddressSendPage extends Page {

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div[2]/button")
    Clickable skipConfigButton();

    @XPath(".//h1")
    Readable<String> header();

    interface SkipConfigDialog extends Component {
        @XPath(".//button[1]")
        Clickable cancelSkipButton();

        @XPath(".//button[2]")
        Clickable confirmSkipButton();
    }

    @XPath("/html/body/div[3]/div[3]/div")
    SkipConfigDialog skipConfigDialog();

    default void clickSkipConfigButton() {
        skipConfigButton().click();
        skipConfigDialog().confirmSkipButton().click();
    }

    @Override
    default void assertLoaded() {
        header().readAndAssert((h) -> {
            assertThat(h).isNotNull();
            assertThat(h.getText()).isIn("Configure SEND", "Configura SEND");
        });
    }
}
