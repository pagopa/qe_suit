package it.pagopa.send.infrastructure.template;

import io.restassured.response.Response;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Punto di partenza per eseguire una chiamata REST-assured generata (Oper) e, opzionalmente,
 * ripeterla finché una {@link PollingStrategy} non è soddisfatta (es. attendere che una risorsa
 * raggiunga un certo stato). Ogni operazione REST costruisce il proprio TestChain tramite
 * {@link RestClient#execute}.
 */
public class TestChain<T> {

    private final Supplier<Response> responseSupplier;
    private final Class<T> responseType;

    TestChain(Supplier<Response> responseSupplier, Class<T> responseType) {
        this.responseSupplier = responseSupplier;
        this.responseType = responseType;
    }

    public PollingResult<T> withoutPolling() {
        return new PollingResult<>(ApiResponse.from(responseSupplier.get()), responseType);
    }

    public PollingResult<T> withPolling(PollingStrategy strategy) {
        return withPolling(strategy, null, null);
    }

    public PollingResult<T> withPolling(PollingStrategy strategy, Duration timeout, Duration interval) {
        ApiResponse response = PollingUtils.pollUntil(
                () -> ApiResponse.from(responseSupplier.get()),
                strategy::isSatisfied,
                timeout,
                interval
        );
        return new PollingResult<>(response, responseType);
    }
}
