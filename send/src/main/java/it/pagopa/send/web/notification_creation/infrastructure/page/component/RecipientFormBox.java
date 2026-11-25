package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.infrastructure.suit.component.TextField;

/**
 * Blocco di un destinatario nel passo "Destinatari" di "Invia una nuova notifica": il blocco si ripete per ogni
 * destinatario, con i campi indicizzati ({@code recipients[i].taxId}, ...), quindi i selettori sono relativi al blocco.
 * L'indirizzo fisico è quello del destinatario, inserito a mano come quello inviato dal canale BFF: senza indirizzo
 * resta il recupero da ANPR, che negli ambienti di test non trova gli indirizzi (notifica rifiutata, ADDRESS_NOT_FOUND).
 * I valori arrivano già nella forma dei campi ({@link RecipientForm}): la conversione dal dominio è del canale.
 */
public interface RecipientFormBox extends Component {

    @XPath(".//label[.//input[@value='PF']]")
    RadioButton naturalPerson();

    @XPath(".//label[.//input[@value='PG']]")
    RadioButton legalPerson();

    @XPath(".//input[contains(@name, '.taxId')]")
    TextField taxId();

    // nome della persona fisica, ragione sociale della persona giuridica
    @XPath(".//input[contains(@name, '.firstName')]")
    TextField firstName();

    @XPath(".//input[contains(@name, '.lastName')]")
    TextField lastName();

    @XPath(".//input[contains(@name, '.digitalDomicile')]")
    TextField digitalDomicile();

    // indirizzo fisico inserito a mano (in alternativa al recupero da ANPR)
    @XPath(".//label[.//input[@value='MANUAL']]")
    RadioButton manualAddress();

    @XPath(".//input[contains(@name, '.address') and not(contains(@name, 'Details'))]")
    TextField address();

    @XPath(".//input[contains(@name, '.houseNumber')]")
    TextField houseNumber();

    // note aggiuntive del portale: "presso, scala, piano"
    @XPath(".//input[contains(@name, '.addressDetails')]")
    TextField addressDetails();

    @XPath(".//input[contains(@name, '.zip')]")
    TextField zip();

    @XPath(".//input[contains(@name, '.municipality') and not(contains(@name, 'Details'))]")
    TextField municipality();

    @XPath(".//input[contains(@name, '.municipalityDetails')]")
    TextField municipalityDetails();

    @XPath(".//input[contains(@name, '.province')]")
    TextField province();

    // precompilato dal portale con "Italia"
    @XPath(".//input[contains(@name, '.foreignState')]")
    TextField foreignState();

    default boolean isEmpty() {
        return taxId().read().isEmpty();
    }

    default void fillFields(RecipientForm form) {
        if (form.legalPerson()) {
            legalPerson().select();
        } else {
            naturalPerson().select();
        }
        firstName().write(form.firstName());
        if (form.lastName() != null) {
            lastName().write(form.lastName());
        }
        taxId().write(form.taxId());
        if (form.address() != null) {
            fillAddress(form.address());
        }
        if (form.digitalDomicile() != null) {
            digitalDomicile().write(form.digitalDomicile());
        }
    }

    private void fillAddress(RecipientForm.Address address) {
        manualAddress().select();
        address().write(address.street());
        houseNumber().write(address.houseNumber());
        if (address.addressDetails() != null) {
            addressDetails().write(address.addressDetails());
        }
        zip().write(address.zip());
        municipality().write(address.municipality());
        if (address.municipalityDetails() != null) {
            municipalityDetails().write(address.municipalityDetails());
        }
        province().write(address.province());
        if (address.foreignState() != null) {
            foreignState().cleanAndWrite(address.foreignState());
        }
    }
}
