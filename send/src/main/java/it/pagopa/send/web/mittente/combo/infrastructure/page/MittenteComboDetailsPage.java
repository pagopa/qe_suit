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

@Url("${url.notifiche.mittente.base}/campaigns/${campaignId}/communications/${iun}")
public interface MittenteComboDetailsPage extends Page {

    ComboOverviewSection overviewSection();

    Optional<ComboDocumentsSection> documentsSection();

    Optional<ComboPaymentsSection> paymentsSection();

    ComboStatusSection statusSection();

    ComboChannelsSection channelsSection();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        assertLoadedWithInterval(java.time.Duration.ofSeconds(30));
    }

    default void assertLoadedFast() {
        assertLoadedWithInterval(java.time.Duration.ofSeconds(1), java.time.Duration.ofSeconds(90));
    }

    default void assertLoadedWithInterval(java.time.Duration pollInterval) {
        assertLoadedWithInterval(pollInterval, java.time.Duration.ofSeconds(60));
    }

    default void assertLoadedWithInterval(java.time.Duration pollInterval, java.time.Duration atMostDuration) {
        // Nota: oneTrustBanner() restituisce sempre un Optional con un lazy proxy anche se il banner
        // non è nel DOM. Si usa isPresent() + try-catch per verificare la presenza reale prima del click.
        oneTrustBanner().ifPresent(banner -> {
            try { banner.accept(); } catch (Exception ignored) {}
        });

        try {
            org.awaitility.Awaitility.await()
                    .atMost(atMostDuration)
                    .pollInterval(pollInterval)
                    .ignoreExceptions()
                    .until(() -> {
                        oneTrustBanner().ifPresent(banner -> {
                            try { banner.accept(); } catch (Exception ignored) {}
                        });
                        try {
                            overviewSection().assertLoaded();
                            statusSection().assertLoaded();
                            channelsSection().assertLoaded();
                            return true;
                        } catch (Throwable t) {
                            try {
                                reload();
                            } catch (Exception ignored) {
                            }
                            return false;
                        }
                    });
        } catch (Exception ignored) {
        }

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
