package it.pagopa.interop.web.purpose_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.config.WebSuiteContext;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.RadioButton;
import org.assertj.core.api.SoftAssertions;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object per la pagina di elenco dei Template di Finalità: è il punto di
 * ingresso da cui un Ente Erogatore avvia la creazione di un nuovo template
 * (bottone "Crea nuovo" + form di conferma con la selezione del trattamento
 * di dati personali).
 */
@Url("${interop.web.purpose-template}")
public interface PurposeTemplateCatalogPage extends Page {

    // pattern generico che assume la presenza di un UUID da qualche parte nell'URL post-conferma
    Pattern UUID_PATTERN = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
    );

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//button[contains(., 'Crea nuovo')]")
    Button createNewButton();

    @XPath(".//label[.//input[@name='personalData' and @value='true']]")
    RadioButton personalDataYesOption();

    @XPath(".//label[.//input[@name='personalData' and @value='false']]")
    RadioButton personalDataNoOption();

    @XPath(".//button[@type='submit' and contains(., 'Conferma')]")
    Button confirmButton();

    default PurposeTemplateCatalogPage openCreationForm() {
        createNewButton().click();
        return this;
    }

    default PurposeTemplateCatalogPage setPersonalData(boolean personalData) {
        if (personalData) personalDataYesOption().select();
        else personalDataNoOption().select();
        return this;
    }

    default void confirmCreation() {
        confirmButton().click();
    }

    /**
     * Legge l'URL corrente del browser (dopo il redirect avvenuto in seguito
     * alla conferma) interrogando direttamente l'adapter di presentazione,
     * senza dipendere dal {@link it.pagopa.interop.web.infrastructure.cucumber.WebBrowserContext}
     * (che viene aggiornato solo sulle navigazioni esplicite via {@code navigateTo()}).
     */
    default String getCurrentBrowserUrl() {
        return WebSuiteContext.getConfiguration()
                .getPresentationApiAdapters()
                .get(0)
                .getLocation()
                .getUrl();
    }

    /**
     * Estrae l'id del template di finalità appena creato dall'URL corrente.
     */
    default UUID currentTemplateIdFromUrl() {
        String url = getCurrentBrowserUrl();
        Matcher matcher = UUID_PATTERN.matcher(url);

        if (!matcher.find()) {
            throw new IllegalStateException("Nessun UUID trovato nell'URL corrente: " + url);
        }

        return UUID.fromString(matcher.group());
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(pageTitle().read())
                    .isEqualTo("I miei template di finalità agevolata");

            createNewButton().assertLoaded();
        });
    }
}
