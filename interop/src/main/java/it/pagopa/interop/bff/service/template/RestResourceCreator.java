package it.pagopa.interop.bff.service.template;

import it.pagopa.interop.common.service.template.RequestOverride;
import it.pagopa.interop.common.utils.PollingUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.function.BiPredicate;

public interface RestResourceCreator<Request, Entity> {

    default Entity createAndAssert(Request request, BiPredicate<HttpStatusCode, Entity> predicate) {
        return PollingUtils.pollUntilWithHttpInfo(
                () -> doCreate(request),
                predicate
        );
    }

    default Entity createAndAssert(RequestOverride<Request> overrides, BiPredicate<HttpStatusCode, Entity> predicate) {
        Request request = doDefaultRequest();
        overrides.applyTo(request);
        return createAndAssert(request, predicate);
    }

    default Entity createResourceSuccessfully(RequestOverride<Request> overrides) {
        Request request = doDefaultRequest();
        overrides.applyTo(request);
        return createResourceSuccessfully(request);
    }

    default Entity createResourceSuccessfully(Request request) {
        return createAndAssert(request, (statusCode, body) -> statusCode.is2xxSuccessful());
    }

    default Entity createResource(Request request) {
        return createAndAssert(request, (statusCode, body) -> true);
    }

    ResponseEntity<Entity> doCreate(Request request);

    Request doDefaultRequest();
}