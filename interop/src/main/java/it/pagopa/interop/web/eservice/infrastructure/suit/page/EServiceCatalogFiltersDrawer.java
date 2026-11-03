package it.pagopa.interop.web.eservice.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.TextField;

public interface EServiceCatalogFiltersDrawer extends Component {

    String TITLE = "Filtri";
    String SECTION_QUICK_SELECTION = "Selezione rapida";
    String SECTION_TEMPLATE = "Template";
    String SECTION_TECHNICAL_SPECS = "Specifiche tecniche";
    String SECTION_DELEGATIONS = "Deleghe e notifiche modifica dati";
    String BTN_REMOVE_FILTERS = "Rimuovi filtri";
    String BTN_APPLY = "Applica filtri";

    @XPath(".//*[self::h2 or self::h6 or contains(@class, 'MuiTypography-root')][normalize-space()='Filtri']")
    Label title();

    @XPath(".//button[@aria-label='Chiudi il drawer']")
    Button closeButton();

    @XPath(".//button[contains(., '" + BTN_REMOVE_FILTERS + "')]")
    Button removeFiltersButton();

    @XPath(".//button[contains(., '" + BTN_APPLY + "')]")
    Button applyFiltersButton();

    @XPath(".//*[contains(text(), '" + SECTION_QUICK_SELECTION + "')]")
    Label quickSelectionTitle();

    @XPath(".//label[contains(., 'E-service attivi')]//span[contains(@class, 'MuiSwitch-root')]")
    Button activeEservicesSwitch();

    @XPath(".//label[contains(., 'Fruibili dal tuo ente')]//span[contains(@class, 'MuiSwitch-root')]")
    Button usableByTenantSwitch();

    @XPath(".//label[contains(., 'Già in fruizione')]//span[contains(@class, 'MuiSwitch-root')]")
    Button alreadyInUseSwitch();

    @XPath(".//*[contains(@class, 'MuiFormControl-root')][.//label[contains(., 'Categoria erogatore')]]//input")
    TextField providerCategoryInput();

    @XPath(".//*[contains(text(), '" + SECTION_TEMPLATE + "')]")
    Label templateTitle();

    @XPath(".//label[contains(., 'Creati da template')]//span[contains(@class, 'MuiSwitch-root')]")
    Button createdFromTemplateSwitch();

    @XPath(".//label[contains(., 'Con template finalità collegati')]//span[contains(@class, 'MuiSwitch-root')]")
    Button linkedPurposeTemplateSwitch();

    @XPath(".//*[contains(text(), '" + SECTION_TECHNICAL_SPECS + "')]")
    Label technicalSpecsTitle();

    @XPath(".//*[contains(@class, 'MuiFormControl-root')][.//label[contains(., 'Scambio dati')]]//input")
    TextField dataExchangeInput();

    @XPath(".//*[contains(@class, 'MuiFormControl-root')][.//label[contains(., 'Eroga o riceve dati')]]//input")
    TextField providesOrReceivesDataInput();

    @XPath(".//*[contains(text(), '" + SECTION_DELEGATIONS + "')]")
    Label delegationsAndNotificationsTitle();

    @XPath(".//*[contains(@class, 'MuiFormControl-root')][.//label[contains(., 'Deleghe')]]//input")
    TextField delegationsInput();

    @XPath(".//label[contains(., 'Signal Hub')]//span[contains(@class, 'MuiSwitch-root')]")
    Button signalHubSwitch();
}
