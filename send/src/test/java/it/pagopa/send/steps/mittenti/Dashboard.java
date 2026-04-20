package it.pagopa.send.steps.mittenti;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.steps.login.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

@Url("${url.notifiche.mittente.dashboard}#selfCareToken=${token.mittente}")
public interface Dashboard extends Page {

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id=\"notificationsTable.body.row\"]/td[7]/button")
    Clickable notificationDetails();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
       header().readAndAssert((h) -> {
           Assertions.assertThat(h).isNotNull();
           Assertions.assertThat(h.getText()).isIn("Notifiche", "Notifications");
       });
       oneTrustBanner().ifPresent(OneTrustBanner::accept);
    }

    default void goToNotificationDetails() {
        notificationDetails().click();
    }
}
