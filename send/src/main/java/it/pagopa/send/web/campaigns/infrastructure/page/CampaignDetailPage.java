package it.pagopa.send.web.campaigns.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.campaigns.infrastructure.page.component.Table;
import it.pagopa.send.web.campaigns.infrastructure.page.component.TableRow;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Url("${url.notifiche.mittente.campaigns}/${campaignId}#selfCareToken=${selfCareToken}")
public interface CampaignDetailPage extends Page {
    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();
    @XPath("//div[contains(@class,'MuiPaper-root')]//p[contains(@class,'MuiTypography-root')]")
    List<Readable<String>> labels();
    @XPath("//*[@data-testid='emptyState']//h6[contains(@class, 'MuiTypography-root') and contains(@class, 'MuiTypography-subtitle2')]")
    Readable<String> emptyStateLabel();
    @XPath("//*[@id=\"recipientId\"]")
    TextField recipientId();
    @XPath("//*[@id=\"iunMatch\"]")
    TextField iunSearchInput();
    @XPath("//*[@id=\"status\"]")
    TextField status();
    @XPath("//*[@id=\"confirm-button\"]")
    Button filterButton();
    Table communications();
    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        header().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
        });
    }

    default void searchCommunication(Map<String, String> searchParams) {
        searchParams.forEach(this::applySearchParam);
        filterButton().click();
    }

    private void applySearchParam(String key, String value) {
        switch (key) {
            case "iun" -> iunSearchInput().cleanAndWrite(value);
            case "recipientId" -> recipientId().cleanAndWrite(value);
            case "status" -> status().cleanAndWrite(value);
            default -> throw new IllegalArgumentException("Parametro di ricerca campagna non supportato: " + key);
        }
    }
}
