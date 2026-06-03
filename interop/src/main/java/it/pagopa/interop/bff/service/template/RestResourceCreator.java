package it.pagopa.interop.bff.service.template;

import it.pagopa.interop.common.service.template.RequestOverride;
import it.pagopa.interop.common.utils.PollingUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.function.BiPredicate;

public interface RestResourceCreator<Request, Entity> extends ContextHandler<Entity> {

    default Entity createAndAssert(Request request, BiPredicate<HttpStatusCode, Entity> predicate) {
        ResponseEntity<Entity> response = PollingUtils.pollUntilWithHttpInfo(
                () -> doCreate(request),
                predicate
        );

        doUpdateHttpContext(response);
        return response.getBody();
    }

    default Entity createAndAssert(RequestOverride<Request> overrides, BiPredicate<HttpStatusCode, Entity> predicate) {
        Request request = doDefaultRequest();
        overrides.applyTo(request);
        return createAndAssert(request, predicate);
    }

    default Entity createSuccessfully(RequestOverride<Request> overrides) {
        Request request = doDefaultRequest();
        overrides.applyTo(request);
        return createSuccessfully(request);
    }

    default Entity createSuccessfully(Request request) {
        return createAndAssert(request, (statusCode, body) -> statusCode.is2xxSuccessful());
    }

    default Entity tryCreate(Request request) {
        return createAndAssert(request, (statusCode, body) -> true);
    }

    ResponseEntity<Entity> doCreate(Request request);

    Request doDefaultRequest();
}