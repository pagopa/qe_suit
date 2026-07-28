package it.pagopa.send.infrastructure.template;

import java.util.function.Predicate;

public class PollingResult<T> {

    private final ApiResponse raw;
    private final Class<T> responseType;

    PollingResult(ApiResponse raw, Class<T> responseType) {
        this.raw = raw;
        this.responseType = responseType;
    }

    public T get() {
        return raw.as(responseType);
    }

    public ApiResponse getRaw() {
        return raw;
    }

    public PollingResult<T> assertThat(Predicate<ApiResponse> predicate) {
        if (!predicate.test(raw)) {
            throw new AssertionError("Assertion fallita per la risposta: " + raw);
        }
        return this;
    }
}
