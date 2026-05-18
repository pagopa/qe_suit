package it.pagopa.send.domain.web.pages.mittente;

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
        successTitle().readAndAssert(text ->
                Assertions.assertThat(text)
                        .isIn(
                                "Notification has been created",
                                "La notifica è stata creata"
                        )
        );
    }
}
