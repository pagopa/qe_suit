package it.pagopa.send.web.mittente.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.mittente.infrastructure.page.component.StatisticsFilterComponent;
import it.pagopa.send.web.mittente.infrastructure.page.component.StatisticsGraphCardComponent;
import org.assertj.core.api.Assertions;

import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;

import java.util.List;
import java.util.Optional;

@Url("${url.notifiche.mittente.statistics}")
public interface StatisticsPage extends Page {

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id='subtitle-page']//p")
    Readable<String> subtitle();

    @XPath("//*[@data-testid='exportJpgButton']")
    Button exportJpgButton();

    @XPath("//*[@id='page-header-container']/following-sibling::span[1]")
    Readable<String> lastUpdate();

    @XPath("//main//h2")
    List<Readable<String>> sectionTitles();

    @XPath("//button[@aria-describedby='tooltip-section-1']/preceding-sibling::p")
    Readable<String> filterSubtitle();

    @XPath("//button[@aria-describedby='tooltip-section-1']")
    Button filterTooltip();

    @XPath("(//*[@data-testid='statistics-filter'])[1]")
    StatisticsFilterComponent filter();

    @XPath("//main//h3")
    List<Readable<String>> graphTitles();

    @XPath("//main//h3/following-sibling::p[1]")
    List<Readable<String>> graphDescriptions();

    List<StatisticsGraphCardComponent> graphCards();

    @XPath("//ul[@role='listbox']/li")
    List<Readable<String>> graphTypeOptions();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
       oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
       header().readAndAssert((h) -> {
           Assertions.assertThat(h).isNotNull();
           Assertions.assertThat(h).isIn("Statistiche", "Statistics");;
       });
    }
}
