package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

/**
 * Catalog e-service detail page (route {@code /catalogo-e-service/:eserviceId/:descriptorId}).
 * <p>
 * Selectors derive from the frontend sources (ConsumerEServiceDetails.page.tsx and children).
 * Selectors marked "NOT VERIFIED" depend on the DOM of {@code InformationContainer}
 * (@pagopa/interop-fe-commons), whose source is not available: label and value are assumed
 * to be adjacent siblings.
 * <p>
 * {@link #assertLoaded()} only waits for the page to be rendered; it does not assert any
 * copy, state or presence belonging to the contract matrix.
 */
@Url("${interop.web.catalog}/${eserviceId}/${descriptorId}")
public interface EServiceDetailPage extends Page {

    // SectionContainer renders <section><div(Stack)><div(row)>...<h2|h3 title/></div><p description/></div><div(Box) children/>...
    String GENERAL_SECTION = ".//section[./div[1]//h2[normalize-space()='Informazioni generali']]";
    String PURPOSE_TEMPLATES_SECTION = ".//section[./div[1]//h2[normalize-space()='Compilazione agevolata della finalità']]";
    String SIGNAL_HUB_SECTION = ".//section[./div[1]//h2[normalize-space()='Signal Hub']]";
    String ATTRIBUTES_SECTION = ".//section[./div[1]//h2[normalize-space()='Soglie e attributi']]";

    // ---------------------------------------------------------------- Header

    @XPath(".//h1")
    Label pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath(".//*[(self::a or self::button)][normalize-space()='Torna al catalogo']")
    Button backToCatalogAction();

    // h2 rendered with text-transform: uppercase (observed text may differ in case from the DOM text)
    @XPath(".//h2[normalize-space()='Versione']")
    Label versionHeaderLabel();

    @XPath(".//h2[normalize-space()='Versione']/following-sibling::button[1]")
    Button versionShortcutButton();

    // Only one chip is rendered in the page header (statusChip of the version info section)
    @XPath("(.//*[contains(@class, 'MuiChip-root')])[1]")
    Label descriptorStatusChip();

    @XPath(".//button[normalize-space()='Vedi ultima versione']")
    Button viewLatestVersionButton();

    // ------------------------------------------------------- Primary actions

    @XPath(".//button[normalize-space()='Richiedi fruizione']")
    Button agreementButton();

    @XPath(".//button[normalize-space()='Visualizza richiesta']")
    Button inspectAgreementButton();

    @XPath(".//button[normalize-space()='Completa richiesta']")
    Button completeAgreementButton();

    @XPath(".//button[normalize-space()='Aggiorna a nuova versione']")
    Button upgradeToNewVersionButton();

    // ---------------------------------------------------------------- Alerts

    List<Alert> alerts();

    // ------------------------------------------------------------------ Tabs

    @XPath(".//*[@role='tab'][normalize-space()='Dettaglio e-service']")
    Label eserviceDetailTab();

    @XPath(".//*[@role='tab'][normalize-space()='Template finalità collegati']")
    Label purposeTemplatesTab();

    // MUI lab TabPanel: a not selected panel stays in the DOM with the hidden attribute and no children
    @XPath("(.//*[@role='tabpanel'])[1]")
    Label eserviceDetailTabPanel();

    @XPath("(.//*[@role='tabpanel'])[2]")
    Label purposeTemplatesTabPanel();

    // ------------------------------------------------- General information

    @XPath(GENERAL_SECTION + "/div[1]//h2")
    Label generalInfoTitle();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Erogatore' and not(*)]")
    Label producerLabel();


    @XPath(GENERAL_SECTION + "//p[normalize-space()='Erogatore']/parent::div/following-sibling::div[1]//span")
    Label producerValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Template in uso' and not(*)]")
    Label templateInUseLabel();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Stai vedendo la versione' and not(*)]")
    Label descriptorVersionLabel();

    @XPath(GENERAL_SECTION + "//div[./div/p[normalize-space()='Stai vedendo la versione']]/div[2]//span")
    Label descriptorVersionValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Eroga dati personali' and not(*)]")
    Label personalDataLabel();

    // TODO controllare che sia valido
    @XPath(GENERAL_SECTION + "//*[normalize-space()='Eroga dati personali' and not(*)]/following-sibling::*[1]")
    Label personalDataValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Scambio dati' and not(*)]")
    Label exchangeTypeLabel();

    // TODO controllare che sia valido
    @XPath(GENERAL_SECTION + "//*[normalize-space()='Scambio dati' and not(*)]/following-sibling::*[1]")
    Label exchangeTypeValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()=\"Descrizione dell'e-service\" and not(*)]")
    Label eserviceDescriptionLabel();

    // TODO controllare che sia valido
    @XPath(GENERAL_SECTION + "//*[normalize-space()=\"Descrizione dell'e-service\" and not(*)]/following-sibling::*[1]")
    Label eserviceDescriptionValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Descrizione della versione' and not(*)]")
    Label descriptorDescriptionLabel();

    // TODO controllare che sia valido
    @XPath(GENERAL_SECTION + "//*[normalize-space()='Descrizione della versione' and not(*)]/following-sibling::*[1]")
    Label descriptorDescriptionValue();

    @XPath(GENERAL_SECTION + "//h3[normalize-space()='Fruizione tramite delega']")
    Label delegationSectionTitle();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Autorizzazione' and not(*)]")
    Label consumerDelegableLabel();

    // TODO controllare che sia valido
    @XPath(GENERAL_SECTION + "//*[normalize-space()='Autorizzazione' and not(*)]/following-sibling::*[1]")
    Label consumerDelegableValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Autorizzazione all’associazione dei client' and not(*)]")
    Label clientAccessDelegableLabel();

    // TODO controllare che sia valido
    @XPath(GENERAL_SECTION + "//*[normalize-space()='Autorizzazione all’associazione dei client' and not(*)]/following-sibling::*[1]")
    Label clientAccessDelegableValue();

    // Bottom actions are IconLink (MUI Link, rendered as <button> when component='button')
    @XPath(GENERAL_SECTION + "//*[(self::a or self::button)][normalize-space()='Vedi i dettagli tecnici dell’e-service']")
    Label showTechnicalDetailsAction();

    @XPath(GENERAL_SECTION + "//*[(self::a or self::button)][normalize-space()='Vedi i dettagli sullo scambio asincrono']")
    Label showAsyncExchangeDetailsAction();

    @XPath(GENERAL_SECTION + "//*[(self::a or self::button)][normalize-space()='Visualizza i contatti dell’erogatore']")
    Label showProducerContactsAction();

    // Closed MUI drawers are not mounted: any mounted drawer is an open one
    @XPath(".//*[contains(@class, 'MuiDrawer-root')]")
    Label openDrawer();

    // ----------------------------------------------- Facilitated compilation

    @XPath(PURPOSE_TEMPLATES_SECTION + "/div[1]//h2")
    Label purposeTemplatesTitle();

    @XPath(PURPOSE_TEMPLATES_SECTION + "/div[1]/p")
    Label purposeTemplatesDescription();

    @XPath(PURPOSE_TEMPLATES_SECTION + "//button[normalize-space()='Visualizza i template collegati']")
    Button viewLinkedPurposeTemplatesButton();

    // ------------------------------------------------------------ Signal Hub

    @XPath(SIGNAL_HUB_SECTION + "/div[1]//h2")
    Label signalHubTitle();

    // Description is <p>before <a>link</a> after</p>, wrapped by the section description <p>
    @XPath(SIGNAL_HUB_SECTION + "/div[1]/p")
    Label signalHubDescription();

    @XPath(SIGNAL_HUB_SECTION + "//a[normalize-space()='soluzione integrata']")
    Label signalHubLink();

    @XPath(SIGNAL_HUB_SECTION + "//*[normalize-space()='Disponibilità del servizio' and not(*)]")
    Label signalHubAvailabilityLabel();

    // TODO controllare che sia valido
    @XPath(SIGNAL_HUB_SECTION + "//*[normalize-space()='Disponibilità del servizio' and not(*)]/following-sibling::*[1]")
    Label signalHubAvailabilityValue();

    // ----------------------------------------------- Thresholds and attributes

    @XPath(ATTRIBUTES_SECTION + "/div[1]//h2")
    Label attributesSectionTitle();

    @XPath(ATTRIBUTES_SECTION + "/div[1]/p")
    Label attributesSectionDescription();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Soglie di chiamate API']")
    Label thresholdsTitle();

    @XPath(ATTRIBUTES_SECTION + "//*[normalize-space()='Soglia giornaliera per fruitore' and not(*)]")
    Label dailyCallsPerConsumerLabel();

    // TODO controllare che sia valido
    @XPath(ATTRIBUTES_SECTION + "//*[normalize-space()='Soglia giornaliera per fruitore' and not(*)]/following-sibling::*[1]")
    Label dailyCallsPerConsumerValue();

    @XPath(ATTRIBUTES_SECTION + "//*[normalize-space()='Soglia giornaliera totale' and not(*)]")
    Label dailyCallsTotalLabel();

    // TODO controllare che sia valido
    @XPath(ATTRIBUTES_SECTION + "//*[normalize-space()='Soglia giornaliera totale' and not(*)]/following-sibling::*[1]")
    Label dailyCallsTotalValue();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Soglie di chiamate API personalizzate']")
    Label customizedThresholdsTitle();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Attributi']")
    Label attributesTitle();

    // TODO controllare che sia valido
    @XPath(ATTRIBUTES_SECTION + "//*[normalize-space()='Questo e-service non richiede attributi' and not(*)]")
    Label noAttributesRequiredMessage();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Attributi Certificati']")
    Label certifiedAttributesTitle();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Attributi Verificati']")
    Label verifiedAttributesTitle();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Attributi Dichiarati']")
    Label declaredAttributesTitle();

    // ------------------------------- Elements of the catalog list (not expected)

    @XPath(".//h1[normalize-space()='Catalogo degli e-service']")
    Label catalogListTitle();

    @XPath(".//label[contains(normalize-space(), 'Cerca per nome')]")
    Label searchByNameField();

    @XPath(".//label[contains(normalize-space(), 'Cerca per erogatore')]")
    Label searchByProducerField();

    // ------------------------------------------------------------------ Load

    /**
     * and the four sections of the first tab panel. Does not assert matrix expectations.
     */
    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Visualizza e-service");
            softly.assertThat(pageTitle().readAndAssert(eServiceName -> Assertions.assertThat(eServiceName).as("Page title is not blank").isNotBlank()));
            agreementButton().assertLoaded();
        });
    }
}
