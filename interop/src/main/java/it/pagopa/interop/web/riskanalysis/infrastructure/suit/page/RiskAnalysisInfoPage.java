package it.pagopa.interop.web.riskanalysis.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;

/**
 * Page object for the RA information shown to a reviewer before starting
 * risk-analysis compilation.
 */
@Url("${interop.web.base-url}/analisi-del-rischio/${riskAnalysisId}")
public interface RiskAnalysisInfoPage extends Page {

    @XPath(".//a[contains(normalize-space(), 'Torna alle analisi del rischio')]")
    Button backToEServicesButton();

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Label title();

    @XPath(".//section[contains(@class, 'MuiPaper-root')][.//h2[normalize-space(.)='Informazioni generali']]")
    GeneralInfoCard generalInfoCard();

    @XPath(".//section[contains(@class, 'MuiPaper-root')][.//h2[normalize-space(.)='Stima chiamate API']]")
    APIEstimateCard apiEstimateCard();

    @XPath(".//section[contains(@class, 'MuiPaper-root')][.//h2[normalize-space(.)='Assegnazione']]")
    AssignmentCard assignmentCard();

    @XPath(".//button[contains(normalize-space(), 'Inizia la compilazione')]")
    Button startCompilationBtn();

    @XPath(".//h1")
    Label unauthorizedTitle();

    @XPath(".//h1/following-sibling::*[1]")
    Label unauthorizedSubtitle();

    @XPath(".//a[@href='/ui/it/' and normalize-space()='Torna alla home']")
    Button backToHomeBtn();

    interface GeneralInfoCard extends Component {

        @XPath(".//h2")
        Label title();

        @XPath(".//p[normalize-space()='E-service da associare']")
        Label eserviceLabel();

        @XPath(".//p[normalize-space()='E-service da associare']/parent::div/following-sibling::div//a")
        Link eserviceValue();

        @XPath(".//p[normalize-space()='Erogatore']")
        Label providerLabel();

        @XPath(".//p[normalize-space()='Erogatore']/parent::div/following-sibling::div[1]//span")
        Label providerValue();

        @XPath(".//p[normalize-space()='Nome della finalità']")
        Label purposeNameLabel();

        @XPath(".//p[normalize-space()='Nome della finalità']/parent::div/following-sibling::div[1]//span")
        Label purposeNameValue();

        @XPath(".//p[contains(normalize-space(), 'Descrizione')]")
        Label purposeDescLabel();

        @XPath(".//p[contains(normalize-space(), 'Descrizione ')]/parent::div/following-sibling::div[1]//span")
        Label purposeDescValue();

        @XPath(".//p[contains(normalize-space(), 'Come metterai a disposizione')]")
        Label accessLabel();

        @XPath(".//p[contains(normalize-space(), 'Come metterai a disposizione')]/parent::div/following-sibling::div[1]//span")
        Label accessValue();

        @XPath(".//p[normalize-space()='Motivazione titolo gratuito']")
        Label freeReasonLabel();

        @XPath(".//p[normalize-space()='Motivazione titolo gratuito']/parent::div/following-sibling::div[1]//span")
        Label freeReasonValue();

    }

    interface APIEstimateCard extends Component {
        @XPath(".//h2")
        Label title();

        @XPath(".//p[normalize-space()='Stima numero chiamate API/giorno']")
        Label apiEstimateLabel();

        @XPath(".//p[normalize-space()='Stima numero chiamate API/giorno']/parent::div/following-sibling::div[1]//span")
        Label apiEstimateValue();
    }

    interface AssignmentCard extends Component {
        @XPath(".//h2")
        Label title();

        @XPath(".//p[normalize-space()='Data assegnazione']")
        Label assignmentDateLabel();

        @XPath(".//p[normalize-space()='Data assegnazione']/parent::div/following-sibling::div[1]//span")
        Label assignmentDateValue();

        @XPath(".//p[normalize-space()='Assegnata a']")
        Label assignedToLabel();

        @XPath(".//p[normalize-space()='Assegnata a']/parent::div/following-sibling::div[1]//span")
        Label assignedToValue();


    }

    /**
     * Waits for the RA-information heading; does not verify the section contents.
     */
    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> {
                    String title = title().read();
                    return title != null && !title.isBlank();
                },
                Boolean.TRUE::equals,
                "Risk Analysis information page is not loaded or  its title is absent" );
    }
}
