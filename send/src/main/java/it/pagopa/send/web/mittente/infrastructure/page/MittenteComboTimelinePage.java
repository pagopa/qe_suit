package it.pagopa.send.web.mittente.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.mittente.infrastructure.page.component.ComboTimelineComponent;
import org.assertj.core.api.SoftAssertions;

import java.util.Optional;

@Url("${url.notifiche.mittente.base}/campaigns/${campaignId}/communications/${iun}/timeline")
public interface MittenteComboTimelinePage extends Page {

    ComboTimelineComponent timeline();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().filter(b -> b.get().isPresent()).ifPresent(OneTrustBanner::accept);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatCode(() -> timeline().assertLoaded())
                .as("Caricamento Timeline Comunicazione Bonaria")
                .doesNotThrowAnyException();
        softly.assertAll();
    }
}
