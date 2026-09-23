package it.pagopa.send.web.campagne.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.campagne.infrastructure.page.component.CampagneBox;
import it.pagopa.send.web.campagne.infrastructure.page.component.Table;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Optional;

@Url("${url.notifiche.mittente.campaigns}/CampAnalogic#selfCareToken=${token.mittente}")
public interface CampaignDetailPage extends Page {
    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();
    @XPath("//div[contains(@class,'MuiGrid-root')]//p[contains(@class,'MuiTypography-root')]")
    List<Readable<String>> labels();
    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        header().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
        });
    }
}
