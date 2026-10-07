package it.pagopa.send.web.mittente.combo;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.mittente.combo.component.ComboTimelineComponent;
import org.assertj.core.api.SoftAssertions;

import java.util.Optional;

@Url("${url.notifiche.mittente.base}/campaigns/${campaignId}/communications/${iun}/timeline")
public interface MittenteComboTimelinePage extends Page {

    ComboTimelineComponent timeline();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        // Nota: oneTrustBanner() restituisce sempre un Optional con un lazy proxy anche se il banner
        // non è nel DOM. Si usa try-catch per verificare la presenza reale prima del click.
        oneTrustBanner().ifPresent(banner -> {
            try { banner.accept(); } catch (Exception ignored) {}
        });

        try {
            org.awaitility.Awaitility.await()
                    .atMost(java.time.Duration.ofSeconds(60))
                    .pollInterval(java.time.Duration.ofSeconds(30))
                    .ignoreExceptions()
                    .until(() -> {
                        oneTrustBanner().ifPresent(banner -> {
                            try { banner.accept(); } catch (Exception ignored) {}
                        });
                        try {
                            if (!timeline().items().isEmpty()) {
                                timeline().assertLoaded();
                                return true;
                            }
                        } catch (Throwable ignored) {
                        }
                        try {
                            reload();
                        } catch (Exception ignored) {
                        }
                        return false;
                    });
        } catch (Exception ignored) {
        }

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatCode(() -> timeline().assertLoaded())
                .as("Caricamento Timeline Comunicazione Bonaria")
                .doesNotThrowAnyException();
        softly.assertAll();
    }
}
