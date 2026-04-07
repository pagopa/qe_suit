package it.pn.frontend.e2e.steps.send.mittenti;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.selfcare.notifiche.base}/dashboard#selfCareToken=${token.mittente}")
public interface Dashboard extends Page {

    @XPath("//*[@id=\"Notifiche-page\"]")
    Readable<String> header();

    @Override
    default void assertLoaded() {
       header().readAndAssert((h) -> {
           Assertions.assertThat(h).isNotNull();
           Assertions.assertThat(h.getText()).isEqualTo("Notifiche");
       });
    }
}
