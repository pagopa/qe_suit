package it.pagopa.interop.web.eservice_template.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.eservice_template.page.component.TemplateGeneralDataWizard;
import it.pagopa.interop.web.infrastructure.config.suit.component.InterfaceComponent;
import org.assertj.core.api.Assertions;

/**
 * Pagina di creazione di un Template E-service.
 * <p>
 * Il wizard di creazione si articola in 3 step, di cui solo il primo avviene su
 * questo URL (${@code /erogazione/template-eservice/crea}); dopo il submit dello
 * step 1 l'applicazione reindirizza il browser verso una pagina strutturalmente
 * identica a {@link TemplateEServiceDetailErogatorePage} (con id reali assegnati),
 * dove proseguono step 2 (solo conferma) e step 3 (upload interfaccia).
 * <p>
 * Il redirect avviene per azione applicativa (click sul bottone), non tramite
 * {@code navigateTo()} esplicito: per questo motivo non è necessario conoscere
 * {@code eserviceTemplateId}/{@code eserviceTemplateVersionId} — i metodi seguenti
 * risolvono i rispettivi selettori XPath sul DOM live del browser, indipendentemente
 * dalla location memorizzata nel proxy.
 * <p>
 * NB: il selettore di {@link #interfaceComponent()} è per analogia con
 * {@code TechnicalSpecWizard} (flusso E-service) e NON è stato confermato da
 * ispezione diretta del DOM per lo step 3 del Template — verificare a runtime.
 */
@Url("${interop.web.my-eservice-template-create}")
public interface TemplateCreationPage extends Page {

    TemplateGeneralDataWizard generalDataStep();

    @XPath(".//button[contains(., 'Salva bozza e prosegui')]")
    Button saveDraftButton();

    // TODO: verificare a runtime il selettore reale dello step 3 (upload interfaccia) del Template.
    @XPath("//section[.//h2[text()='Interfaccia']]")
    InterfaceComponent interfaceComponent();

    @Override
    default void assertLoaded() {
        generalDataStep().name().readAndAssert(value ->
                Assertions.assertThat(value).as("Name field is present on template creation page").isNotNull());
    }
}

