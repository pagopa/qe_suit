package it.pagopa.send.steps.mittenti.pages;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.selfcare.notifiche.base}/dashboard/nuova-notifica#selfCareToken=${token.mittente}")
public interface NotificationSuccessPage extends Page {

    @XPath("//*[@id='title-sync-feedback']")
    Readable<String> successTitle();

    @XPath("//*[@id='go-to-notifications']")
    Clickable goToNotificationsButton();

    @Override
    default void assertLoaded() {
        successTitle().readAndAssert(element ->
                Assertions.assertThat(element.getText())
                        .isIn(
                                "Notification has been created",
                                "La notifica è stata creata"
                        )
        );
    }
}
