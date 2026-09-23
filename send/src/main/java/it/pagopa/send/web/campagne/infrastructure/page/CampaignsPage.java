package it.pagopa.send.web.campagne.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.campagne.infrastructure.page.component.CampagneBox;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

@Url("${url.notifiche.mittente.campaigns}#selfCareToken=${token.mittente}")
public interface CampaignsPage extends Page {
    @XPath("//*[@id=\"Campagne-page\"]")
    Readable<String> header();
    CampagneBox table();
    @XPath("//*[@data-testid='emptyState']//h6[contains(@class, 'MuiTypography-root') and contains(@class, 'MuiTypography-subtitle2')]")
    Readable<String> emptyStateLabel();
    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        header().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Campaigns", "Campagne");
        });
    }
}
