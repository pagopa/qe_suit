package it.pagopa.send.steps.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("sottoinsieme di ${url.selfcare.notifiche.base}")
public interface ReserverdAreaPage extends Page {

    @XPath("//*[@id=\"root\"]/div/div[1]/nav/div/div/div/div[1]/div/div/p")
    Readable<String> header();

    @XPath("//*[@id=\"forward_prod-pn-test\"]")
    Clickable accediSendButton();

    default void accediToSend() {
        accediSendButton().click();
    }
    
    @Override
    default void assertLoaded() {
        header().readAndAssert(h -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h.getText())
                      .isIn("Area Riservata", "Area Riservata");
        });
    }
}
