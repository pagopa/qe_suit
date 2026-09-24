package it.pagopa.send.web.mittente.combo;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.mittente.combo.component.ComboChannelsSection;
import it.pagopa.send.web.mittente.combo.component.ComboDocumentsSection;
import it.pagopa.send.web.mittente.combo.component.ComboOverviewSection;
import it.pagopa.send.web.mittente.combo.component.ComboPaymentsSection;
import it.pagopa.send.web.mittente.combo.component.ComboStatusSection;
import org.assertj.core.api.SoftAssertions;

import java.util.Optional;

@Url("${url.notifiche.mittente.dashboard}/campaigns/${campaignId}/communications/${iun}")
public interface MittenteComboDetailsPage extends Page {

    ComboOverviewSection overviewSection();

    Optional<ComboDocumentsSection> documentsSection();

    Optional<ComboPaymentsSection> paymentsSection();

    ComboStatusSection statusSection();

    ComboChannelsSection channelsSection();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatCode(() -> overviewSection().assertLoaded())
                .as("Caricamento Sezione Overview")
                .doesNotThrowAnyException();
        softly.assertThatCode(() -> statusSection().assertLoaded())
                .as("Caricamento Sezione Stato")
                .doesNotThrowAnyException();
        softly.assertThatCode(() -> channelsSection().assertLoaded())
                .as("Caricamento Sezione Canali")
                .doesNotThrowAnyException();
        softly.assertAll();
    }
}
