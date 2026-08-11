package it.pagopa.interop.common.infrastructure.template.action;

import it.pagopa.interop.common.infrastructure.context.EntityStore;
import it.pagopa.interop.common.infrastructure.context.LastApiResponseStore;
import it.pagopa.interop.common.infrastructure.response.ApiResponse;
import it.pagopa.interop.common.infrastructure.response.RawResponse;
import it.pagopa.interop.common.infrastructure.template.action.context.PollingActionContext;
import it.pagopa.interop.common.infrastructure.utils.async.PollingUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PollingAction<Response> implements ResponseFinalizer<Response> {

    @Setter(onMethod_ = {@Autowired})
    private LastApiResponseStore lastApiResponseStore;

    @Getter
    @Setter(onMethod_ = {@Autowired})
    private EntityStore entityStore;

    @Getter
    private RawResponse raw;

    @Getter
    private PollingActionContext<? super Response> context;

    PollingAction<Response> handle(PollingActionContext<? super Response> context) {
        this.context = context;

        RawResponse firstResponse = context.getResponseSupplier().get();

        if (isSatisfied(firstResponse)) {
            this.raw = firstResponse;
        } else if (context.getTimeout() == null) {
            this.raw = PollingUtils.pollUntil(
                    context.getResponseSupplier(),
                    this::isSatisfied
            );
        } else {
            this.raw = PollingUtils.pollUntil(
                    context.getResponseSupplier(),
                    this::isSatisfied,
                    context.getTimeout(),
                    context.getInterval()
            );
        }

        updateLastApiResponse();

        return this;
    }

    PollingAction<Response> handleWithout(PollingActionContext<Response> context) {
        this.context = context;
        this.raw = context.getResponseSupplier().get();

        updateLastApiResponse();

        return this;
    }

    @Override
    public <T> ResponseFinalizer<T> map(Function<? super Response, ? extends T> mapper) {
        T mappedResponse = mapper.apply(get());
        return new ResolvedResponseFinalizer<>(
                mappedResponse,
                raw,
                entityStore
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response get() {
        if (context.getResponseClass() == Void.class) {
            return null;
        }

        return (Response) raw.as(context.getResponseClass());
    }

    private boolean isSatisfied(RawResponse response) {
        if (response instanceof ApiResponse apiResponse) {
            return context.getPollingStrategy().isSatisfied(apiResponse);
        }

        /*
         * Una risposta non HTTP non ha status code da valutare.
         * Se è stata prodotta correttamente, viene considerata valida.
         */
        return response != null;
    }

    private void updateLastApiResponse() {
        if (raw instanceof ApiResponse apiResponse) {
            lastApiResponseStore.setLastResponse(apiResponse);
        }
    }
}