package it.pagopa.interop.web.pages.dev_tools.components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.component.Button;
import org.assertj.core.api.SoftAssertions;

public interface SimulatoreOttenimentoTokenSection extends Component {

    @XPath(".//h2")
    Readable<String> sectionTitle();

    @XPath(".//p")
    Readable<String> description();

    @XPath("//a[contains(@class,'MuiButton-root') and contains(normalize-space(.), 'Simula per e-service')]")
    Button eserviceButton();

    @XPath("//a[contains(@class,'MuiButton-root') and contains(normalize-space(.), 'Simula per Interoperabilità')]")
    Button interopButton();

    @Override
    default void assertLoaded() {

        sectionTitle().readAndAssert("Simulatore ottenimento token");

        description().readAndAssert("Dopo aver creato e selezionato un client, puoi simulare l’ottenimento di un voucher attraverso questo strumento.");

        eserviceButton().get()
                .ifPresentOrElse(
                        we -> SoftAssertions.assertSoftly(softly -> {
                            softly.assertThat(we.getText()).isEqualToIgnoringCase("Simula per e-service");
                            softly.assertThat(we.getAttributes().get("href")).isEqualToIgnoringCase("/ui/it/tool-sviluppo/api-e-service/simulazione-voucher");
                        }),
                        () -> {
                            throw new AssertionError("Il bottone 'Simula per e-service' non è presente nella pagina");
                        }
                );

        interopButton().get()
                .ifPresentOrElse(
                        we -> SoftAssertions.assertSoftly(softly -> {
                            softly.assertThat(we.getText()).isEqualToIgnoringCase("Simula per Interoperabilità");
                            softly.assertThat(we.getAttributes().get("href")).isEqualToIgnoringCase("/ui/it/tool-sviluppo/api-interop/simulazione-voucher");
                        }),
                        () -> {
                            throw new AssertionError("Il bottone 'Simula per Interoperabilità' non è presente nella pagina");
                        }
                );
    }
}
