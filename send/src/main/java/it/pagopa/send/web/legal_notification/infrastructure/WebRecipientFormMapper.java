package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.send.common.legal_notification.domain.NotificationDefaults;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserType;
import it.pagopa.send.web.notification_creation.infrastructure.page.component.RecipientForm;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converte il destinatario di dominio nei valori dei campi del blocco destinatario del portale, senza scriverli.
 * Il portale vuole il numero civico in un campo a sé, che nel dominio fa parte dell'indirizzo (in fondo, se c'è,
 * altrimenti "snc", senza numero civico); "presso" e dettagli vanno insieme nelle note dell'indirizzo.
 */
@Component
public class WebRecipientFormMapper {

    // indirizzo con il numero civico in fondo, es. "Via Ok 890"
    private static final Pattern STREET_AND_NUMBER = Pattern.compile("(.+?)\\s+(\\d+\\w*)");
    private static final String NO_HOUSE_NUMBER = "snc";

    public RecipientForm toForm(RecipientSpec spec) {
        User recipient = spec.recipient();
        boolean legalPerson = recipient.getType() == UserType.PG;
        String defaultDenomination = legalPerson ? recipient.getOrganization() : recipient.getName();
        return new RecipientForm(
                legalPerson,
                spec.denominationOverride() != null ? spec.denominationOverride() : defaultDenomination,
                legalPerson ? null : recipient.getFamilyName(),
                spec.taxIdOverride() != null ? spec.taxIdOverride() : recipient.getTaxId(),
                spec.physicalAddress() != null ? toAddress(spec.physicalAddress()) : null,
                spec.digitalDomicile());
    }

    private static RecipientForm.Address toAddress(NotificationDefaults.PhysicalAddressDefaults physicalAddress) {
        Matcher street = STREET_AND_NUMBER.matcher(physicalAddress.address());
        boolean hasNumber = street.matches();
        String details = Stream.of(physicalAddress.at(), physicalAddress.addressDetails())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        return new RecipientForm.Address(
                hasNumber ? street.group(1) : physicalAddress.address(),
                hasNumber ? street.group(2) : NO_HOUSE_NUMBER,
                details.isEmpty() ? null : details,
                physicalAddress.zip(),
                physicalAddress.municipality(),
                physicalAddress.municipalityDetails(),
                physicalAddress.province(),
                physicalAddress.foreignState());
    }
}
