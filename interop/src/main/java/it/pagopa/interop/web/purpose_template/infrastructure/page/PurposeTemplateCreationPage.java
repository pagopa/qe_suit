package it.pagopa.interop.web.purpose_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Switch;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.SoftAssertions;

import java.util.Optional;

/**
 * Page Object per la pagina di dettaglio/form del Template di Finalità su cui
 * si viene rediretti (stessa sessione, nessuna {@code navigateTo()} esplicita)
 * subito dopo la conferma di creazione ({@link PurposeTemplateCatalogPage#confirmCreation()}).
 * L'annotazione {@code @Url} è richiesta dal framework ma non viene mai utilizzata
 * esplicitamente in questo scenario, poiché non si naviga mai a questa pagina
 * tramite {@code navigateTo()}: si arriva sempre per redirect dalla pagina di
 * creazione template.
 */
@Url("${interop.web.purpose-template}/${templateId}")
public interface PurposeTemplateCreationPage extends Page {

    @XPath("//*[@id='purposeTitle']")
    TextField nameField();

    @XPath("//*[@id='purposeDescription']")
    TextField descriptionField();

    // TODO selettore best-effort basato sul testo della label, in assenza di un id/data-testid noto.
    //  Da verificare/adattare confrontando con il markup reale della pagina quando verrà sviluppata.
@XPath(".//label[.//span[contains(normalize-space(.), \"Ho diritto all'utilizzo dell'e-service a titolo gratuito\")]]//span[contains(@class, 'MuiSwitch-switchBase')]")
    Switch freeOfChargeSwitch();

    // TODO selettore best-effort basato sul testo della label, in assenza di un id/data-testid noto.
    //  Da verificare/adattare confrontando con il markup reale della pagina quando verrà sviluppata.
@XPath(".//div[contains(@class, 'MuiTextField-root')][.//label[contains(normalize-space(.), \"Spiega perché puoi usare l'eservice a titolo gratuito\")]]")
    Optional<TextField> freeOfChargeExplanationField();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(nameField().read())
                    .as("Name field is not blank")
                    .isNotBlank();
            softly.assertThat(descriptionField().get())
                    .as("Description field is present")
                    .isPresent();
            freeOfChargeSwitch().assertLoaded();
        });
    }
}

