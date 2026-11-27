package it.pagopa.send.radd.infrastructure;

import it.pagopa.send.generated.openapi.clients.raddregistry.model.AddressV2;
import it.pagopa.send.generated.openapi.clients.raddregistry.model.Coordinates;
import it.pagopa.send.generated.openapi.clients.raddregistry.model.CreateRegistryRequestV2;
import it.pagopa.send.generated.openapi.clients.raddregistry.model.SelectiveUpdateRegistryRequestV2;
import it.pagopa.send.generated.openapi.clients.raddregistry.model.UpdateRegistryRequestV2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Richieste baseline del registry RADD v2, valide per contratto: un fallimento del contract test deve essere sempre
 * attribuibile alla mutazione applicata dal fuzzer, non al fixture. Ogni richiesta valorizza tutti i campi del DTO
 * (required e opzionali), cosi' anche gli opzionali sono oggetto di fuzzing.
 * <p>
 * Le due operazioni di modifica condividono gli stessi campi ma hanno semantica diversa:
 * <ul>
 *   <li>{@code updateRegistry} (PATCH): aggiornamento parziale, nessun campo required, quelli non inviati restano
 *       invariati;</li>
 *   <li>{@code selectiveUpdateRegistry} (PUT): aggiornamento completo, {@code address}, {@code description},
 *       {@code phoneNumbers} e {@code externalCodes} required, gli opzionali non inviati vengono cancellati.
 *       L'indirizzo non e' modificabile: deve coincidere con quello della sede.</li>
 * </ul>
 * Sede e codice esterno sono randomizzati per evitare conflitti (409) tra creazioni concorrenti.
 */
@Component
public class RaddAltRegistryRequestFactory {

    static final String PARTNER_TYPE_CAF = "CAF";
    static final String PHONE_NUMBER = "3921573273";
    static final String EMAIL = "contract-test@esempio.it";
    static final String WEBSITE = "https://www.esempio.it";
    static final String OPENING_TIME = "Lun 09:00-13:00, 14:00-18:00; Mar 09:30-12:30; Mer 09:00-18:00";
    static final int RETRIEVE_LIMIT = 10;

    public CreateRegistryRequestV2 createRequest() {
        LocalDate today = LocalDate.now();
        return new CreateRegistryRequestV2()
                .address(address())
                .description("Sportello contract test QA")
                .phoneNumbers(phoneNumbers())
                .email(EMAIL)
                .openingTime(OPENING_TIME)
                .startValidity(today.toString())
                .endValidity(today.plusYears(1).toString())
                .externalCodes(externalCodes())
                .appointmentRequired(false)
                .website(WEBSITE)
                .partnerType(PARTNER_TYPE_CAF);
    }

    /** PATCH: tutti i campi sono opzionali, si inviano tutti per renderli oggetto di fuzzing. */
    public UpdateRegistryRequestV2 updateRequest() {
        return new UpdateRegistryRequestV2()
                .externalCodes(externalCodes())
                .description("Sportello contract test QA aggiornato")
                .openingTime(OPENING_TIME)
                .phoneNumbers(phoneNumbers())
                .endValidity(LocalDate.now().plusYears(1).toString())
                .appointmentRequired(true)
                .website(WEBSITE)
                .email(EMAIL)
                .coordinates(new Coordinates().latitude(41.9006).longitude(12.4823));
    }

    /**
     * PUT: indirizzo e inizio validita' sono quelli usati alla creazione della sede, perche' l'indirizzo non e'
     * modificabile e il resto della richiesta sostituisce integralmente i dati esistenti.
     */
    public SelectiveUpdateRegistryRequestV2 selectiveUpdateRequest(CreateRegistryRequestV2 created) {
        return new SelectiveUpdateRegistryRequestV2()
                .externalCodes(externalCodes())
                .description("Sportello contract test QA aggiornato")
                .phoneNumbers(phoneNumbers())
                .address(copy(created.getAddress()))
                .openingTime(OPENING_TIME)
                .startValidity(created.getStartValidity())
                .endValidity(created.getEndValidity())
                .email(EMAIL)
                .appointmentRequired(true)
                .website(WEBSITE);
    }

    /** Query di {@code retrieveRegistries}: prima pagina, senza {@code lastKey}; chiavi = nomi dei parametri OpenAPI. */
    public Map<String, Object> retrieveQuery() {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("limit", RETRIEVE_LIMIT);
        return query;
    }

    private static AddressV2 address() {
        int civicNumber = ThreadLocalRandom.current().nextInt(1, 300);
        return new AddressV2()
                .addressRow("Via del Corso " + civicNumber)
                .cap("00186")
                .city("Roma")
                .province("RM")
                .country("Italia");
    }

    private static AddressV2 copy(AddressV2 address) {
        return new AddressV2()
                .addressRow(address.getAddressRow())
                .cap(address.getCap())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry());
    }

    private static List<String> phoneNumbers() {
        return new ArrayList<>(List.of(PHONE_NUMBER));
    }

    private static List<String> externalCodes() {
        return new ArrayList<>(List.of("QA-" + UUID.randomUUID().toString().substring(0, 8)));
    }
}
