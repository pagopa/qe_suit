package it.pagopa.send.web.notification_creation.infrastructure.page.component;

/**
 * Valori da scrivere nel blocco di un destinatario del passo "Destinatari", già nella forma dei campi del portale:
 * {@link RecipientFormBox} li scrive così come sono. {@code lastName}, {@code address} e {@code digitalDomicile}
 * sono {@code null} quando il campo resta vuoto.
 */
public record RecipientForm(boolean legalPerson, String firstName, String lastName, String taxId, Address address,
                            String digitalDomicile) {

    /** Indirizzo fisico inserito a mano; {@code addressDetails}, {@code municipalityDetails} e {@code foreignState}
     * sono {@code null} quando il campo resta com'è. */
    public record Address(String street, String houseNumber, String addressDetails, String zip, String municipality,
                          String municipalityDetails, String province, String foreignState) {
    }
}
