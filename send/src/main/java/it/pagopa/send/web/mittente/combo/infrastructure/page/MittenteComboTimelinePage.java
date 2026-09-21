package it.pagopa.send.web.mittente.combo;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.mittente.combo.component.ComboTimelineComponent;
import org.assertj.core.api.SoftAssertions;

import java.util.Optional;

@Url("${url.notifiche.mittente.dashboard}/campagne/{campaignId}/comunicazioni/{iun}/timeline")
public interface MittenteComboTimelinePage extends Page {

    ComboTimelineComponent timeline();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatCode(() -> timeline().assertLoaded())
                .as("Caricamento Timeline Comunicazione Bonaria")
                .doesNotThrowAnyException();
        softly.assertAll();
    }
}
