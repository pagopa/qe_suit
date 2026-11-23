package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserType;

/**
 * Blocco di un destinatario nel passo "Destinatari" di "Invia una nuova notifica": il blocco si ripete per ogni
 * destinatario, con i campi indicizzati ({@code recipients[i].taxId}, ...), quindi i selettori sono relativi al blocco.
 * L'indirizzo fisico resta quello proposto dal portale, il recupero da ANPR.
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
        if (spec.digitalDomicile() != null) {
            digitalDomicile().write(spec.digitalDomicile());
        }
    }

    private static String denomination(RecipientSpec spec, String defaultDenomination) {
        return spec.denominationOverride() != null ? spec.denominationOverride() : defaultDenomination;
    }
}
