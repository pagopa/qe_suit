package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * Richiesta di conferma "Vuoi uscire?", aperta quando si lascia la pagina verso un'altra pagina del portale:
 * "Annulla" resta sulla pagina, "Esci" prosegue verso la pagina scelta.
 */
public interface ExitDialog extends Component {
    @XPath("//*[@data-testid=\"promptDialog\"]//h2")
    Readable<String> title();

    @XPath("//*[@data-testid=\"promptDialog\"]//*[contains(@class, 'MuiDialogContentText-root')]")
    Readable<String> message();

    @XPath("//*[@data-testid=\"promptDialog\"]//button[not(@data-testid=\"confirmExitBtn\")]")
    Button cancelButton();

    @XPath("//*[@data-testid=\"confirmExitBtn\"]")
    Button exitButton();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
