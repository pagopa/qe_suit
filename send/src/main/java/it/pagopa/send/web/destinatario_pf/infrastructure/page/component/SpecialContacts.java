package it.pagopa.send.web.destinatario_pf.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.Assertions;

/**
 * PEC personalizzate per ente: per ogni ente il nome, la PEC, "Modifica" ed "Elimina". Ogni elemento legge la colonna
 * di tutti gli enti; i campi di modifica si individuano dall'id del recapito dell'ente.
 */
public interface SpecialContacts extends Component {
    @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]//span[contains(@id,\"_pec-typography\")]")
    Readable<String> values();

    @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]//button[starts-with(@id,\"modifyContact-\")]")
    Button modifyButtons();

    @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]//button[starts-with(@id,\"cancelContact-\")]")
    Readable<String> deleteButtons();

    @XPath("(//*[contains(@data-testid,\"_pecSpecialContact\")]//button[starts-with(@id,\"modifyContact-\")])[1]")
    Button firstModifyButton();

    @XPath("(//*[contains(@data-testid,\"_pecSpecialContact\")]//input)[1]")
    TextField firstEditInput();

    @XPath("(//*[contains(@data-testid,\"_pecSpecialContact\")]//button[starts-with(@id,\"saveContact-\")])[1]")
    Button firstSaveButton();

    @XPath("(//*[contains(@data-testid,\"_pecSpecialContact\")]//p[contains(@id,\"-helper-text\")])[1]")
    Readable<String> firstEditErrorMessage();

    // labels

    @XPath("//*[@data-testid=\"specialContacts\"]/p[1]")
    Readable<String> title();

    @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]/preceding-sibling::*[1]")
    Readable<String> senders();

    @Override
    default void assertLoaded() {
        values().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty());
    }
}
