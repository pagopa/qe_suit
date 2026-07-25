package it.pagopa.interop.common.infrastructure.template.action;

import it.pagopa.interop.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.common.infrastructure.response.ApiResponse;
import it.pagopa.interop.common.infrastructure.response.RawResponse;
import it.pagopa.interop.common.kernel.Identifiable;
import org.assertj.core.api.Assertions;

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

    RawResponse getRaw();

    default ResponseFinalizer<Response> assertStatusCode(int expectedStatusCode) {
        RawResponse rawResponse = getRaw();

        if(!(rawResponse instanceof ApiResponse apiResponse)){
            throw new IllegalStateException("Response is not ApiResponse");
        }

        Assertions.assertThat(apiResponse.getStatusCode()).isEqualTo(expectedStatusCode);
        return this;
    }

    default ResponseFinalizer<Response> assertThat(Predicate<? super Response> predicate) {
        Response response = get();

        if (!predicate.test(response)) {
            throw new AssertionError("Assertion failed for response: " + response);
        }

        return this;
    }
}
