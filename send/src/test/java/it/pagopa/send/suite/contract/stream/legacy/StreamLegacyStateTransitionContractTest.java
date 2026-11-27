package it.pagopa.send.suite.contract.stream.legacy;

import io.restassured.response.Response;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.stream.domain.LegacyEventStream;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.generated.openapi.clients.streamlegacy.ApiClient;
import it.pagopa.send.stream.infrastructure.StreamLegacyApiOperations;
import it.pagopa.send.stream.infrastructure.StreamLegacyApiOperations.Operations;
import it.pagopa.send.stream.infrastructure.StreamLegacyCreatedTracker;
import it.pagopa.send.stream.infrastructure.StreamLegacyRequestFactory;
import it.pagopa.send.stream.infrastructure.StreamLegacyStatusCodes;
import it.pagopa.send.stream.infrastructure.config.StreamLegacyContractValidatorFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Transizioni di stato dello {@code streamId} sulle API pn-stream legacy: test dedicati, senza
 * fuzzing, con aspettativa esplicita; una {@link DynamicTest} per ogni versione che prevede
 * l'operazione (v1.0 non ha {@code disable} ne' {@code replacedStreamId}).
 * <pre>
 * (nessuno) --create--&gt; ATTIVO --disable (v2.x)--&gt; DISABILITATO
 *                       ATTIVO / DISABILITATO --remove--&gt; RIMOSSO
 * </pre>
 * Le chiamate passano dal catalogo {@link StreamLegacyApiOperations}, perche' lo status va verificato
 * direttamente. Aspettative dallo spec e dal codice di pn-stream ({@link StreamLegacyStatusCodes}):
 * <ul>
 *   <li>stream DISABILITATO: update, disable e sostituzione rifiutati con 403; lettura, consumo
 *       e rimozione restano ammessi (controlli positivi);</li>
 *   <li>stream RIMOSSO: ogni operazione risponde 404 "Stream not found" (sostituzione:
 *       "ReplacedId Stream not found");</li>
 *   <li>stream creato con un'altra versione: accesso rifiutato con 403 (api version mismatch);</li>
 *   <li>in v1.0 la lambda di versioning converte 403 e 404 in 400.</li>
 * </ul>
 * Fuori scope: limite di stream attivi per PA (409, non dichiarato dallo spec, e interferirebbe
 * con gli altri test della stessa PA).
 */
public class StreamLegacyStateTransitionContractTest extends AbstractStreamLegacyContractTest {

    private final StreamLegacyApiOperations operations;

    public StreamLegacyStateTransitionContractTest(
            ApiClient apiClient,
            StreamLegacyContractValidatorFactory validators,
            StreamLegacyRequestFactory requestFactory,
            StreamLegacyCreatedTracker createdTracker,
            SendJourney sendJourney,
            UserProvider userProvider,
            StreamLegacyApiOperations operations
    ) {
        super(apiClient, validators, requestFactory, createdTracker, sendJourney, userProvider);
        this.operations = operations;
    }

    // ---------------------------------------------------------------- DISABILITATO (solo v2.x)

    @TestFactory
    Stream<DynamicTest> updateOnDisabledStream() {
        return onDisabledStream(Operations::update, StreamLegacyStatusCodes::forbidden);
    }

    @TestFactory
    Stream<DynamicTest> disableOnDisabledStream() {
        return onDisabledStream(Operations::disable, StreamLegacyStatusCodes::forbidden);
    }

    @TestFactory
    Stream<DynamicTest> createReplacingDisabledStream() {
        return onDisabledStream(Operations::createReplacing, StreamLegacyStatusCodes::forbidden);
    }

    @TestFactory
    Stream<DynamicTest> retrieveOnDisabledStream() {
        return onDisabledStream(Operations::retrieve, version -> StreamLegacyStatusCodes.SUCCESS);
    }

    @TestFactory
    Stream<DynamicTest> consumeOnDisabledStream() {
        return onDisabledStream(Operations::consume, version -> StreamLegacyStatusCodes.SUCCESS);
    }

    @TestFactory
    Stream<DynamicTest> removeOnDisabledStream() {
        return onDisabledStream(Operations::remove, version -> StreamLegacyStatusCodes.REMOVE_SUCCESS);
    }

    // ---------------------------------------------------------------- RIMOSSO

    @TestFactory
    Stream<DynamicTest> retrieveOnRemovedStream() {
        return onRemovedStream(allVersions(), Operations::retrieve);
    }

    @TestFactory
    Stream<DynamicTest> updateOnRemovedStream() {
        return onRemovedStream(allVersions(), Operations::update);
    }

    @TestFactory
    Stream<DynamicTest> removeOnRemovedStream() {
        return onRemovedStream(allVersions(), Operations::remove);
    }

    @TestFactory
    Stream<DynamicTest> consumeOnRemovedStream() {
        return onRemovedStream(allVersions(), Operations::consume);
    }

    @TestFactory
    Stream<DynamicTest> disableOnRemovedStream() {
        return onRemovedStream(disableVersions(), Operations::disable);
    }

    @TestFactory
    Stream<DynamicTest> createReplacingRemovedStream() {
        return onRemovedStream(disableVersions(), Operations::createReplacing);
    }

    // ---------------------------------------------------------------- VERSIONE DIVERSA

    /**
     * Lo stream e' creato con un'altra versione (v2.9, o v2.3 per v2.9) e letto con quella sotto test.
     */
    @TestFactory
    Stream<DynamicTest> retrieveOnStreamOfAnotherVersion() {
        return allVersions().map(version -> {
            StreamApiVersion creationVersion = version == StreamApiVersion.V29 ? StreamApiVersion.V23 : StreamApiVersion.V29;
            int expected = StreamLegacyStatusCodes.forbidden(version);
            return DynamicTest.dynamicTest(version + " su stream " + creationVersion + " -> " + expected, () -> {
                UUID streamId = streamId(sendJourney.withFreshLegacyStream(owner(), creationVersion));
                expect(operations.of(version).retrieve().apply(streamId), expected);
            });
        });
    }

    private Stream<DynamicTest> onDisabledStream(
            Function<Operations, Function<UUID, Response>> operation, Function<StreamApiVersion, Integer> expectedStatus) {
        return disableVersions().map(version -> {
            int expected = expectedStatus.apply(version);
            return DynamicTest.dynamicTest(version + " DISABILITATO -> " + expected, () -> {
                UUID streamId = streamId(sendJourney.withDisabledLegacyStream(owner(), version));
                Response response = operation.apply(operations.of(version)).apply(streamId);
                // una sostituzione accettata per errore crea uno stream: tracciato per la purge
                createdTracker.tracking(version, r -> expect(r, expected)).accept(response);
            });
        });
    }

    private Stream<DynamicTest> onRemovedStream(
            Stream<StreamApiVersion> versions, Function<Operations, Function<UUID, Response>> operation) {
        return versions.map(version -> {
            int expected = StreamLegacyStatusCodes.notFound(version);
            return DynamicTest.dynamicTest(version + " RIMOSSO -> " + expected, () -> {
                UUID streamId = streamId(sendJourney.withRemovedLegacyStream(owner(), version));
                Response response = operation.apply(operations.of(version)).apply(streamId);
                // una sostituzione accettata per errore crea uno stream: tracciato per la purge
                createdTracker.tracking(version, r -> expect(r, expected)).accept(response);
            });
        });
    }

    private static UUID streamId(SendJourney journey) {
        return journey.get(LegacyEventStream.class).streamId();
    }

    private static Stream<StreamApiVersion> allVersions() {
        return Arrays.stream(StreamApiVersion.values());
    }

    private static Stream<StreamApiVersion> disableVersions() {
        return allVersions().filter(StreamApiVersion::supportsDisable);
    }

    private static void expect(Response response, int status) {
        response.then().statusCode(status);
    }
}
