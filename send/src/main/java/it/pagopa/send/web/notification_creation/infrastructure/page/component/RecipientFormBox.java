package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.common.legal_notification.domain.NotificationDefaults;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserType;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Blocco di un destinatario nel passo "Destinatari" di "Invia una nuova notifica": il blocco si ripete per ogni
 * destinatario, con i campi indicizzati ({@code recipients[i].taxId}, ...), quindi i selettori sono relativi al blocco.
 * L'indirizzo fisico è quello del destinatario, inserito a mano come quello inviato dal canale BFF: senza indirizzo
 * resta il recupero da ANPR, che negli ambienti di test non trova gli indirizzi (notifica rifiutata, ADDRESS_NOT_FOUND).
 */
public interface RecipientFormBox extends Component {

    // indirizzo con il numero civico in fondo, es. "Via Ok 890"
    Pattern STREET_AND_NUMBER = Pattern.compile("(.+?)\\s+(\\d+\\w*)");
    String NO_HOUSE_NUMBER = "snc";

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

    default void fillFields(RecipientSpec spec) {
        User recipient = spec.recipient();
        if (recipient.getType() == UserType.PG) {
            legalPerson().select();
            firstName().write(denomination(spec, recipient.getOrganization()));
        } else {
            naturalPerson().select();
            firstName().write(denomination(spec, recipient.getName()));
            lastName().write(recipient.getFamilyName());
        }
        taxId().write(spec.taxIdOverride() != null ? spec.taxIdOverride() : recipient.getTaxId());
        if (spec.physicalAddress() != null) {
            fillPhysicalAddress(spec.physicalAddress());
        }
        if (spec.digitalDomicile() != null) {
            digitalDomicile().write(spec.digitalDomicile());
        }
    }

    /**
     * Indirizzo fisico del destinatario: il portale vuole il numero civico in un campo a sé, che nel dominio fa parte
     * dell'indirizzo (in fondo, se c'è, altrimenti "snc", senza numero civico); "presso" e dettagli vanno nelle note.
     */
    private void fillPhysicalAddress(NotificationDefaults.PhysicalAddressDefaults physicalAddress) {
        manualAddress().select();
        Matcher street = STREET_AND_NUMBER.matcher(physicalAddress.address());
        boolean hasNumber = street.matches();
        address().write(hasNumber ? street.group(1) : physicalAddress.address());
        houseNumber().write(hasNumber ? street.group(2) : NO_HOUSE_NUMBER);
        String details = Stream.of(physicalAddress.at(), physicalAddress.addressDetails())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        if (!details.isEmpty()) {
            addressDetails().write(details);
        }
        zip().write(physicalAddress.zip());
        municipality().write(physicalAddress.municipality());
        if (physicalAddress.municipalityDetails() != null) {
            municipalityDetails().write(physicalAddress.municipalityDetails());
        }
        province().write(physicalAddress.province());
        if (physicalAddress.foreignState() != null) {
            foreignState().cleanAndWrite(physicalAddress.foreignState());
        }
    }

    private static String denomination(RecipientSpec spec, String defaultDenomination) {
        return spec.denominationOverride() != null ? spec.denominationOverride() : defaultDenomination;
    }
}
