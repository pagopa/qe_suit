package it.pagopa.interop.common.infrastructure.template.action;

import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.context.inmemory.InMemoryEntityStore;
import it.pagopa.interop.common.infrastructure.context.inmemory.InMemoryLastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.interop.common.infrastructure.template.action.context.BaseActionContext;
import it.pagopa.interop.common.infrastructure.template.action.context.PollingActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PollingActionTest {

    @Test
    void handle_uses_first_api_response_when_it_already_satisfies_polling_strategy() {
        Response restAssuredResponse = mock(Response.class);
        when(restAssuredResponse.getStatusCode()).thenReturn(200);
        when(restAssuredResponse.asString()).thenReturn("{\"ok\":true}");

        ApiResponse apiResponse = new ApiResponse(restAssuredResponse);
        PollingAction<String> pollingAction = new PollingAction<>();
        InMemoryLastApiResponseStore lastApiResponseStore = new InMemoryLastApiResponseStore();

        pollingAction.setLastApiResponseStore(lastApiResponseStore);
        pollingAction.setEntityStore(new InMemoryEntityStore());

        BaseActionContext baseContext = new BaseActionContext(() -> apiResponse, String.class);
        PollingActionContext<String> context = new PollingActionContext<>(baseContext, RawResponse::isSuccess, null, null);

        PollingAction<String> result = pollingAction.handle(context);

        assertSame(pollingAction, result);
        assertSame(apiResponse, pollingAction.getRaw());
        assertSame(apiResponse, lastApiResponseStore.getLastResponse());
        assertEquals("{\"ok\":true}", pollingAction.getRaw().getRawContent());
    }
}
