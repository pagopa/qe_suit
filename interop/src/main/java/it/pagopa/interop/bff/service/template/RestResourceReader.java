package it.pagopa.interop.bff.service.template;

import it.pagopa.interop.common.utils.PollingUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.function.BiPredicate;

public interface RestResourceReader<Request, Entity> extends ContextHandler<Entity> {

    default Entity getAndAssert(Request request, BiPredicate<HttpStatusCode, Entity> predicate) {
        Entity entity = PollingUtils.pollUntilWithHttpInfo(
                () -> doRead(request),
                predicate
        );

        updateContext(entity);
        return entity;
    }

    default Entity getResourceSuccessfully(Request request) {
        return getAndAssert(request, (statusCode, _response) -> statusCode.is2xxSuccessful());
    }

    ResponseEntity<Entity> doRead(Request request);
}
