package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.async.PollingUtils;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.ApiContext;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.PollingActionContext;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Function;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PollingAction<Response, Model extends Identifiable> implements ApiFinalizer<Response, Model> {

    @Setter(onMethod_ = {@Autowired})
    private ApiContext apiContext;

    @Getter
    @Setter(onMethod_ = {@Autowired})
    private DomainContext domainContext;

    @Getter
    private ApiResponse raw;

    @Getter
    private PollingActionContext<? super Response> context;

    @SuppressWarnings("unchecked")
    PollingAction<Response, Model> handle(PollingActionContext<? super Response> context) {
        this.context = context;
        this.raw = PollingUtils.pollUntil(
                context.getResponseSupplier(),
                r -> context.getPollingStrategy().isSatisfied(
                        r.statusCode(),
                        (Response) r.as(context.getResponseClass())
                ),
                context.getTimeout() != null ? context.getTimeout() : Duration.ofSeconds(10),
                context.getInterval() != null ? context.getInterval() : Duration.ofSeconds(1)
        );

        apiContext.setLastResponse(raw);
        return this;
    }

    PollingAction<Response, Model> handleWithout(PollingActionContext<Response> context) {
        this.context = context;
        this.raw = context.getResponseSupplier().get();
        apiContext.setLastResponse(raw);
        return this;
    }

    @Override
    public <T> ApiFinalizer<T, Model> map(Function<? super Response, ? extends T> mapper) {
        ApiFinalizer<Response, Model> source = this;

        return new MappedApiFinalizer<>(source, mapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response get() {
        return (Response) raw.as(context.getResponseClass());
    }
}