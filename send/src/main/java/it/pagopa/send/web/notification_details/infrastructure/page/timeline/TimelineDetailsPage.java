package it.pagopa.send.web.notification_details.infrastructure.page.timeline;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;

import java.util.Optional;

@Url("${url.notifiche.mittente.dashboard}/${iun}/dettaglio/timeline")
public interface TimelineDetailsPage extends Page {

    TimelineComponent timeline();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        timeline().assertLoaded();
    }
}
