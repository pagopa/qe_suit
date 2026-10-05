package it.pagopa.send.web.campaigns.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.campaigns.infrastructure.page.component.CampagneBox;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;
import java.util.Optional;

@Url("${url.notifiche.mittente.campaigns}#selfCareToken=${selfCareToken}")
public interface CampaignsPage extends Page {
    @XPath("//*[@id=\"Campagne-page\"]")
    Readable<String> header();
    CampagneBox box();
    @XPath("//*[@data-testid='emptyState']//h6[contains(@class, 'MuiTypography-root') and contains(@class, 'MuiTypography-subtitle2')]")
    Readable<String> emptyStateLabel();
    @XPath("//*[@id='rows-per-page']")
    Button rowsXPageButton();
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
