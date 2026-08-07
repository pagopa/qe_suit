package it.pagopa.interop.common.infrastructure.template.action;

import it.pagopa.interop.common.infrastructure.async.PollingUtils;
import it.pagopa.interop.common.infrastructure.context.EntityStore;
import it.pagopa.interop.common.infrastructure.context.LastApiResponseStore;
import it.pagopa.interop.common.infrastructure.response.ApiResponse;
import it.pagopa.interop.common.infrastructure.response.RawResponse;
import it.pagopa.interop.common.infrastructure.template.action.context.PollingActionContext;
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
        if (!(firstResponse instanceof ApiResponse firstApiResponse)) {
            throw new IllegalArgumentException("RawResponse is not an instance of ApiResponse. PollingAction can only handle ApiResponse instances.");
        }

        if (context.getPollingStrategy().isSatisfied(firstApiResponse)) {
            this.raw = firstApiResponse;
        } else if (context.getTimeout() == null) {
            this.raw = PollingUtils.pollUntil(
                    context.getResponseSupplier(),
                    response -> response instanceof ApiResponse apiResponse
                            && context.getPollingStrategy().isSatisfied(apiResponse)
            );
        } else {
            this.raw = PollingUtils.pollUntil(
                    context.getResponseSupplier(),
                    response -> response instanceof ApiResponse apiResponse
                            && context.getPollingStrategy().isSatisfied(apiResponse),
                    context.getTimeout(),
                    context.getInterval()
            );
        }

        lastApiResponseStore.setLastResponse((ApiResponse) this.raw);

        return this;
    }

    PollingAction<Response> handleWithout(PollingActionContext<Response> context) {
        this.context = context;
        this.raw = context.getResponseSupplier().get();

        if (raw instanceof ApiResponse apiResponse) {
            lastApiResponseStore.setLastResponse(apiResponse);
        } else {
            throw new IllegalArgumentException("RawResponse is not an instance of ApiResponse. PollingAction can only handle ApiResponse instances.");
        }

        return this;
    }

    @Override
    public <T> ResponseFinalizer<T> map(Function<? super Response, ? extends T> mapper) {
        ResponseFinalizer<Response> source = this;

        return new MappedResponseFinalizer<>(source, mapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response get() {
        return (Response) raw.as(context.getResponseClass());
    }
}