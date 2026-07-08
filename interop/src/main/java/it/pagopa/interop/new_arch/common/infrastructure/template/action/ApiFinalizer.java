package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ApiFinalizer<Response, Model extends Identifiable> {

    <T> ApiFinalizer<T, Model> map(Function<? super Response, ? extends T> mapper);

    Response get();

    ApiResponse getRaw();

    default ApiFinalizer<Response, Model> assertThat(BiPredicate<Integer, ? super Response> predicate) {
        Response response = get();
        Integer statusCode = getRaw().statusCode();

        if (!predicate.test(statusCode, response)) {
            throw new AssertionError("Assertion failed for response: " + response);
        }

        return this;
    }

    default ApiFinalizer<Response, Model> assertThat(Predicate<ApiResponse> predicate) {
        ApiResponse response = getRaw();

        if (!predicate.test(response)) {
            throw new AssertionError("Assertion failed for response: " + response);
        }

        return this;
    }
}
