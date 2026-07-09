package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ResponseFinalizer<Response> {

    <T> ResponseFinalizer<T> map(Function<? super Response, ? extends T> mapper);

    DomainContext getDomainContext();

    default ResponseFinalizer<Response> updateContext(){
        Response response = get();

        if(response instanceof Identifiable identifiable)
            getDomainContext().upsert(identifiable);
        else throw new IllegalStateException("Response is not Identifiable");

        return this;
    }

    Response get();

    ApiResponse getRaw();

    default ResponseFinalizer<Response> assertThat(BiPredicate<Integer, ? super Response> predicate) {
        Response response = get();
        Integer statusCode = getRaw().statusCode();

        if (!predicate.test(statusCode, response)) {
            throw new AssertionError("Assertion failed for response: " + response);
        }

        return this;
    }

    default ResponseFinalizer<Response> assertThat(Predicate<ApiResponse> predicate) {
        ApiResponse response = getRaw();

        if (!predicate.test(response)) {
            throw new AssertionError("Assertion failed for response: " + response);
        }

        return this;
    }
}
