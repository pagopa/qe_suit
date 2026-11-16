package it.pagopa.send.web.mittente.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.mittente.infrastructure.page.component.ComboChannelsSection;
import it.pagopa.send.web.mittente.infrastructure.page.component.ComboDocumentsSection;
import it.pagopa.send.web.mittente.infrastructure.page.component.ComboOverviewSection;
import it.pagopa.send.web.mittente.infrastructure.page.component.ComboPaymentsSection;
import it.pagopa.send.web.mittente.infrastructure.page.component.ComboStatusSection;
import org.assertj.core.api.SoftAssertions;

import java.util.List;
import java.util.Optional;

@Url("${url.notifiche.mittente.base}/campaigns/${campaignId}/communications/${iun}")
public interface MittenteComboDetailsPage extends Page {

    ComboOverviewSection overviewSection();

    @XPath("//nav[@aria-label='breadcrumbs']//li[contains(@class, 'MuiBreadcrumbs-li')]")
    List<Readable<String>> breadcrumbItems();

    // riquadro principale: coppie label/valore sotto il titolo (IUN)
    @XPath("//div[contains(@class, 'MuiPaper-root')][.//h1]//p[contains(@class, 'MuiTypography-body2')]")
    List<Readable<String>> summaryLabels();

    @XPath("//div[contains(@class, 'MuiPaper-root')][.//h1]//div[contains(@class, 'MuiTypography-body2')]")
    List<Readable<String>> summaryValues();

    Optional<ComboDocumentsSection> documentsSection();

    Optional<ComboPaymentsSection> paymentsSection();

    ComboStatusSection statusSection();

    ComboChannelsSection channelsSection();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().filter(b -> b.get().isPresent()).ifPresent(OneTrustBanner::accept);

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
