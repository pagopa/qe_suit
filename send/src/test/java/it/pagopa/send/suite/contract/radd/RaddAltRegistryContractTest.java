package it.pagopa.send.suite.contract.radd;

import io.restassured.response.Response;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.contract.http.TargetExpression;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.generated.openapi.clients.raddregistry.ApiClient;
import it.pagopa.send.generated.openapi.clients.raddregistry.model.CreateRegistryRequestV2;
import it.pagopa.send.generated.openapi.clients.raddregistry.model.SelectiveUpdateRegistryRequestV2;
import it.pagopa.send.generated.openapi.clients.raddregistry.model.UpdateRegistryRequestV2;
import it.pagopa.send.radd.infrastructure.RaddAltRegistryLocationPool;
import it.pagopa.send.radd.infrastructure.RaddAltRegistryLocationPool.RaddRegistryLocation;
import it.pagopa.send.radd.infrastructure.RaddAltRegistryRequestFactory;
import it.pagopa.send.radd.infrastructure.config.RaddAltRegistryApiContractConfig;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static it.pagopa.send.radd.infrastructure.config.RaddAltRegistryApiContractConfig.BAD_REQUEST_STATUS_CODE;
import static it.pagopa.send.radd.infrastructure.config.RaddAltRegistryApiContractConfig.SUCCESS_STATUS_CODE;
import static it.pagopa.send.radd.infrastructure.config.RaddAltRegistryApiContractConfig.status;

/**
 * Contract test (fuzzing) delle API B2B RADD di censimento dei punti di ritiro
 * ({@code pn-radd-alt-registry-v2-external.yaml}), con il JWT statico dell'operatore ({@code radd.jwt.raddista-1}).
 * <p>
 * Checklist delle operazioni coperte (path sotto {@code /radd-bo/api/v2}):
 * <ol>
 *   <li>{@code retrieveRegistries}      GET    /registry               queryParams 200</li>
 *   <li>{@code addRegistry}             POST   /registry               payload     200</li>
 *   <li>{@code updateRegistry}          PATCH  /registry/{locationId}  payload     200 (aggiornamento parziale)</li>
 *   <li>{@code selectiveUpdateRegistry} PUT    /registry/{locationId}  payload     200 (aggiornamento completo)</li>
 *   <li>{@code deleteRegistry}          DELETE /registry/{locationId}  pathParams  204</li>
 * </ol>
 * Fuori scope: l'header {@code x-pagopa-pn-cx-id} (valorizzato dal gateway dal JWT) e il {@code locationId} di
 * PATCH/PUT, fissato nell'{@code apiCall} sulla sede condivisa: per quelle operazioni lo scope e' il solo payload.
 * {@code lastKey} di {@code retrieveRegistries} non e' nel baseline perche' richiede una chiave di paginazione reale.
 * <p>
 * Campi opzionali: {@code REMOVED} e' atteso con successo (PATCH: il campo resta invariato; PUT/POST: il campo viene
 * cancellato o assume il default), mentre {@code REPLACED_WITH_NULL} resta 400 da policy, perche' lo spec dichiara che
 * il gateway rifiuta il {@code null} esplicito. Sulla PUT l'indirizzo non e' modificabile: anche la rimozione del
 * {@code country} (opzionale nel DTO) lo rende diverso da quello della sede ed e' attesa come errore.
 * <p>
 * Aspettative derivate dallo spec, da confermare al primo run annotando qui gli scostamenti.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, RaddAltRegistryApiContractConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class RaddAltRegistryContractTest {

    private static final FuzzScenario REMOVED = FuzzScenario.REMOVED;

    private static final List<TargetExpression<CreateRegistryRequestV2>> CREATE_OPTIONAL_FIELDS = List.of(
            CreateRegistryRequestV2::getEmail,
            CreateRegistryRequestV2::getOpeningTime,
            CreateRegistryRequestV2::getStartValidity,
            CreateRegistryRequestV2::getEndValidity,
            CreateRegistryRequestV2::getAppointmentRequired,
            CreateRegistryRequestV2::getWebsite,
            CreateRegistryRequestV2::getPartnerType,
            request -> request.getAddress().getCountry()
    );

    /** PATCH: nessun campo e' required, quelli non inviati restano invariati. */
    private static final List<TargetExpression<UpdateRegistryRequestV2>> UPDATE_OPTIONAL_FIELDS = List.of(
            UpdateRegistryRequestV2::getExternalCodes,
            UpdateRegistryRequestV2::getDescription,
            UpdateRegistryRequestV2::getOpeningTime,
            UpdateRegistryRequestV2::getPhoneNumbers,
            UpdateRegistryRequestV2::getEndValidity,
            UpdateRegistryRequestV2::getAppointmentRequired,
            UpdateRegistryRequestV2::getWebsite,
            UpdateRegistryRequestV2::getEmail,
            UpdateRegistryRequestV2::getCoordinates
    );

    /** PUT: i campi opzionali non inviati vengono cancellati; i quattro required restano 400 da policy. */
    private static final List<TargetExpression<SelectiveUpdateRegistryRequestV2>> SELECTIVE_UPDATE_OPTIONAL_FIELDS = List.of(
            SelectiveUpdateRegistryRequestV2::getOpeningTime,
            SelectiveUpdateRegistryRequestV2::getStartValidity,
            SelectiveUpdateRegistryRequestV2::getEndValidity,
            SelectiveUpdateRegistryRequestV2::getEmail,
            SelectiveUpdateRegistryRequestV2::getAppointmentRequired,
            SelectiveUpdateRegistryRequestV2::getWebsite
    );

    private final ApiClient apiClient;
    private final HttpContractValidator httpContractValidator;
    private final HttpContractValidator deleteHttpContractValidator;
    private final RaddAltRegistryRequestFactory requestFactory;
    private final RaddAltRegistryLocationPool locations;

    public RaddAltRegistryContractTest(
            @Qualifier("raddRegistryApiClient") ApiClient apiClient,
            @Qualifier("raddRegistryHttpContractValidator") HttpContractValidator httpContractValidator,
            @Qualifier("raddRegistryDeleteHttpContractValidator") HttpContractValidator deleteHttpContractValidator,
            RaddAltRegistryRequestFactory requestFactory,
            RaddAltRegistryLocationPool locations
    ) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.deleteHttpContractValidator = deleteHttpContractValidator;
        this.requestFactory = requestFactory;
        this.locations = locations;
    }

    /** {@code limit} e' opzionale da spec (default 1000): rimozione e null sono attesi con successo. */
    @TestFactory
    Stream<DynamicTest> retrieveRegistries() {
        return httpContractValidator
                .apiCall(() -> apiClient.registryV2().retrieveRegistries())
                .queryParams(requestFactory::retrieveQuery)
                .targets(List.of(REMOVED, FuzzScenario.REPLACED_WITH_NULL), expectSuccess(),
                        List.<TargetExpression<Map<String, Object>>>of(query -> query.get("limit")))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> addRegistry() {
        return httpContractValidator
                .apiCall(() -> apiClient.registryV2().addRegistry())
                .payload(requestFactory::createRequest)
                .targets(REMOVED, expectSuccess(), CREATE_OPTIONAL_FIELDS)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateRegistry() {
        return httpContractValidator
                .apiCall(() -> apiClient.registryV2().updateRegistry().locationIdPath(locations.shared().locationId()))
                .payload(requestFactory::updateRequest)
                .targets(REMOVED, expectSuccess(), UPDATE_OPTIONAL_FIELDS)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> selectiveUpdateRegistry() {
        return httpContractValidator
                .apiCall(() -> apiClient.registryV2().selectiveUpdateRegistry().locationIdPath(locations.shared().locationId()))
                .payload(() -> requestFactory.selectiveUpdateRequest(locations.shared().request()))
                .targets(REMOVED, expectSuccess(), SELECTIVE_UPDATE_OPTIONAL_FIELDS)
                .targets(REMOVED, status(BAD_REQUEST_STATUS_CODE),
                        List.<TargetExpression<SelectiveUpdateRegistryRequestV2>>of(
                                request -> request.getAddress().getCountry()))
                .tests();
    }

    /** Ogni caso cancella una sede creata appositamente. */
    @TestFactory
    Stream<DynamicTest> deleteRegistry() {
        return deleteHttpContractValidator
                .apiCall(() -> apiClient.registryV2().deleteRegistry())
                .pathParams(() -> {
                    RaddRegistryLocation location = locations.create();
                    return Map.of("locationId", location.locationId());
                })
                .tests();
    }

    private static Consumer<Response> expectSuccess() {
        return status(SUCCESS_STATUS_CODE);
    }
}
