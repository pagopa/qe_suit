package it.pagopa.send.suite.contract.stream.legacy;

import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.generated.openapi.clients.streamlegacy.ApiClient;
import it.pagopa.send.stream.infrastructure.StreamLegacyApiOperations;
import it.pagopa.send.stream.infrastructure.StreamLegacyCreatedTracker;
import it.pagopa.send.stream.infrastructure.StreamLegacyRequestFactory;
import it.pagopa.send.stream.infrastructure.config.StreamLegacyContractValidatorFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Contract test (fuzzing) delle API B2B pn-stream v2.3 sulla lettura degli eventi
 * ({@code pn-stream-api-external-older-version.yaml}, tag Events).
 * <p>
 * Checklist delle operazioni coperte:
 * <ol>
 *   <li>{@code consumeEventStreamV23} GET /delivery-progresses/v2.3/streams/{streamId}/events pathParams+queryParams 200</li>
 * </ol>
 * La baseline passa un {@code lastEventId} sintatticamente valido, cosi' che anche il cursore
 * ({@code ^0\d{37}$}) sia sottoposto al fuzzing: aspettative in {@code withLastEventIdExpectations}.
 * Ogni caso usa uno stream nuovo, quindi alla prima lettura: nessun 429 da retry-after.
 * <p>
 * Le operazioni {@code v2.3} sono {@code deprecated} nello spec: testarle e' lo scopo della
 * classe, percio' il warning di deprecazione e' soppresso.
 */
@SuppressWarnings("deprecation")
public class StreamV23EventsContractTest extends AbstractStreamLegacyContractTest {

    private static final StreamApiVersion VERSION = StreamApiVersion.V23;

    public StreamV23EventsContractTest(
            ApiClient apiClient,
            StreamLegacyContractValidatorFactory validators,
            StreamLegacyRequestFactory requestFactory,
            StreamLegacyCreatedTracker createdTracker,
            SendJourney sendJourney,
            UserProvider userProvider,
            StreamLegacyApiOperations operations,
            CurrentUserSession currentUserSession
    ) {
        super(apiClient, validators, requestFactory, createdTracker, sendJourney, userProvider, operations,
                currentUserSession);
    }

    @TestFactory
    Stream<DynamicTest> consumeEventStreamV23() {
        return withBaseline(baseline(VERSION, ops -> ops.consume().apply(freshStreamId(VERSION)), expectSuccess()),
                withLastEventIdExpectations(withStreamIdExpectations(validators.standard(VERSION)
                        .as(OWNER)
                        .apiCall(() -> apiClient.events().consumeEventStreamV23())
                        .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                        .queryParams(AbstractStreamLegacyContractTest::lastEventIdQueryParams))
                        .tests());
    }
}
