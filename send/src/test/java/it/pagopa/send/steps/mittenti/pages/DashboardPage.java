package it.pagopa.send.steps.mittenti.pages;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.steps.login.component.OneTrustBanner;
import org.assertj.core.api.Assertions;
import it.pagopa.send.steps.mittenti.components.NotificationRow;
import it.pagopa.send.steps.mittenti.components.SearchFiltersComponent;

import java.util.List;
import java.util.Optional;

@Url("${url.notifiche.mittente.dashboard}#selfCareToken=${token.mittente}")
public interface DashboardPage extends Page {

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id=\"notificationsTable.body.row\"]/td[7]/div/button")
    Clickable notificationDetails();

    @XPath("//*[@id=\"new-notification-btn\"]")
    Clickable clickCreaNotificaButton();

    @XPath(".//*[@data-testid='cancelButton'][not(@disabled)]")
    Optional<Clickable> removeFilters();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div[2]/div[2]")
    SearchFiltersComponent filters();

    List<NotificationRow> rows();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
       header().readAndAssert((h) -> {
           Assertions.assertThat(h).isNotNull();
           Assertions.assertThat(h.getText()).isIn("Notifiche", "Notifications");
       });
    }

    default void assertLoadedWithBannerCheck() {
        assertLoaded();
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
    }

    default void goToNotificationDetails() {
        notificationDetails().click();
    }

    default void clickCreaNotifica() {
        clickCreaNotificaButton().click();
    }
    default void removeFiltersIfPresent() {
        removeFilters().ifPresent(Clickable::click);
    }
}
