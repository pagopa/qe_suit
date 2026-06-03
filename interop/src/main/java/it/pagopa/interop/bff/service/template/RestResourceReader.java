package it.pagopa.interop.bff.service.template;

import it.pagopa.interop.common.utils.PollingUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.function.BiPredicate;

public interface RestResourceReader<Request, Entity> extends ContextHandler<Entity> {

    default Entity getAndAssert(Request request, BiPredicate<HttpStatusCode, Entity> predicate) {
        ResponseEntity<Entity> entity = PollingUtils.pollUntilWithHttpInfo(
                () -> doRead(request),
                predicate
        );

        doUpdateHttpContext(entity);
        doUpdateModelContext(entity.getBody());

        return entity.getBody();
    }

    default Entity getSuccessfully(Request request) {
        return getAndAssert(request, (statusCode, _response) -> statusCode.is2xxSuccessful());
    }

    default Entity tryGet(Request request) {
        return getAndAssert(request, (statusCode, _response) -> true);
    }

    ResponseEntity<Entity> doRead(Request request);
}
