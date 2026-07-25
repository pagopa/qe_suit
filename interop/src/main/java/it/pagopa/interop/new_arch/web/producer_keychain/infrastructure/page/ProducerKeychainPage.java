package it.pagopa.interop.new_arch.web.producer_keychain.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.new_arch.web.infrastructure.component.Button;
import it.pagopa.interop.new_arch.web.infrastructure.component.TextField;
import it.pagopa.interop.new_arch.web.producer_keychain.infrastructure.page.component.ProducerKeychainRow;
import it.pagopa.interop.new_arch.web.producer_keychain.infrastructure.page.component.ProducerKeychainTable;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.Keys;

import java.util.Optional;

@Url("${interop.web.base-url}/erogazione/portachiavi")
public interface ProducerKeychainPage extends Page {

    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//h2/following::p[1]")
    Readable<String> subtitle();

    @XPath(".//button[normalize-space()='Crea nuovo']")
    Button addProducerKeychainBtn();

    @XPath(".//div[contains(@class, 'MuiTextField-root')][.//label[normalize-space()='Cerca per nome']]")
    TextField searchField();

    ProducerKeychainTable keychainTable();

    default Optional<ProducerKeychainRow> searchKeychain(String keychain) {
        searchField().writeAndAssert(keychain);
        searchField().write(Keys.ENTER.name());

        return keychainTable().rows().stream()
                .filter(row -> row.name().read().equals(keychain))
                .findFirst();
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(title().read())
                    .isEqualTo("I miei portachiavi erogatore");

            softly.assertThat(subtitle().read())
                    .isEqualTo("In quest’area puoi visualizzare e gestire i portachiavi associati ai tuoi e-service, caricando le chiavi pubbliche necessarie per la verifica delle risposte da parte dei fruitori.");

            keychainTable().assertLoaded();
        });
    }
}
