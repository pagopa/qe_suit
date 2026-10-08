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
        // Nota: oneTrustBanner() restituisce sempre un Optional con un lazy proxy anche se il banner
        // non è nel DOM. Si usa try-catch per verificare la presenza reale prima del click.
        oneTrustBanner().ifPresent(banner -> {
            try { banner.accept(); } catch (Exception ignored) {}
        });
        timeline().assertLoaded();
    }
}
