package it.pagopa.interop.web.purpose_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.RadioGroup;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.SoftAssertions;

/**
 * Page Object per lo step "Informazioni Generali" del wizard di creazione
 * di un Template di Finalità, visualizzato dopo la conferma della creazione
 * dalla {@link PurposeTemplateCatalogPage}.
 */
@Url("${interop.web.template-purpose}/${templatePurposeId}/modifica")
public interface PurposeTemplateGeneralInfoPage extends Page {

    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//*[@id='purposeTitle']")
    TextField purposeTitle();

    @XPath(".//*[@id='purposeDescription']")
    TextField purposeDescription();

    @XPath(".//*[@id='targetDescription']")
    TextField targetDescription();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//input[@name='purposeIsFreeOfCharge']]")
    RadioGroup purposeIsFreeOfCharge();

    @XPath(".//*[@id='purposeFreeOfChargeReason']")
    TextField purposeFreeOfChargeReason();

    @XPath(".//*[@id='purposeDailyCalls']")
    TextField purposeDailyCalls();

    @XPath(".//button[@type='submit' and contains(., 'Salva bozza e prosegui')]")
    Button saveDraftButton();

    @Override
    default void assertLoaded() {
        title().readAndAssert("Informazioni generali");

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(purposeTitle().get(FindPolicy.PRESENT))
                    .as("Campo 'Nome finalità'").isPresent();
            softly.assertThat(purposeDescription().get(FindPolicy.PRESENT))
                    .as("Campo 'Descrizione finalità'").isPresent();
            softly.assertThat(targetDescription().get(FindPolicy.PRESENT))
                    .as("Campo 'A chi è rivolto il template'").isPresent();
            softly.assertThat(purposeIsFreeOfCharge().get(FindPolicy.PRESENT))
                    .as("Radio group 'Titolo gratuito'").isPresent();
            softly.assertThat(purposeFreeOfChargeReason().get(FindPolicy.PRESENT))
                    .as("Campo 'Motivazione titolo gratuito'").isPresent();
            softly.assertThat(purposeDailyCalls().get(FindPolicy.PRESENT))
                    .as("Campo 'Chiamate API/giorno'").isPresent();
            saveDraftButton().assertLoaded();
        });
    }
}


