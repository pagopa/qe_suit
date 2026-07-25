package it.pagopa.interop.common.infrastructure.template.action;

import it.pagopa.interop.common.infrastructure.async.PollingUtils;
import it.pagopa.interop.common.infrastructure.cucumber.context.ApiContext;
import it.pagopa.interop.common.infrastructure.cucumber.context.DomainContext;
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
    private ApiContext apiContext;

    @Getter
    @Setter(onMethod_ = {@Autowired})
    private DomainContext domainContext;

    @Getter
    private RawResponse raw;

    @Getter
    private PollingActionContext<? super Response> context;

    PollingAction<Response> handle(PollingActionContext<? super Response> context) {
        this.context = context;

        if(raw instanceof ApiResponse apiResponse) {
            if (context.getTimeout() == null)
                this.raw = PollingUtils.pollUntil(
                        context.getResponseSupplier(),
                        r -> context.getPollingStrategy().isSatisfied(apiResponse)
                );
            else
                this.raw = PollingUtils.pollUntil(
                        context.getResponseSupplier(),
                        r -> context.getPollingStrategy().isSatisfied(apiResponse),
                        context.getTimeout(),
                        context.getInterval()
                );

            apiContext.setLastResponse(apiResponse);
        }
        else
            throw new IllegalArgumentException("RawResponse is not an instance of ApiResponse. PollingAction can only handle ApiResponse instances.");

        return this;
    }

    PollingAction<Response> handleWithout(PollingActionContext<Response> context) {
        this.context = context;
        this.raw = context.getResponseSupplier().get();

        if (raw instanceof ApiResponse apiResponse) {
            apiContext.setLastResponse(apiResponse);
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