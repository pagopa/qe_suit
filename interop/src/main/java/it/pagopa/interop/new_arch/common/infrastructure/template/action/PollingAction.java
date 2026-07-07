package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.PollingActionContext;
import it.pagopa.interop.new_arch.common.infrastructure.utils.PollingUtils;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PollingAction<Response, Model extends Identifiable> implements ApiFinalizer<Response> {

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<ContextAction<Response, Model>> contextActionProvider;
    @Getter
    private ApiResponse rawResponse;
    @Getter
    private PollingActionContext<? super Response> context;

    @SuppressWarnings("unchecked")
    PollingAction<Response, Model> handle(PollingActionContext<? super Response> context) {
        this.context = context;
        this.rawResponse = PollingUtils.pollUntil(
                context.getResponseSupplier(),
                r -> context.getPollingStrategy().isSatisfied(rawResponse.statusCode(), (Response) rawResponse.as(context.getResponseClass())),
                context.getTimeout() != null ? context.getTimeout() : Duration.ofSeconds(10),
                context.getInterval() != null ? context.getInterval() : Duration.ofSeconds(1)
        );

        return this;
    }

    PollingAction<Response, Model> handleWithout(PollingActionContext<Response> context) {
        this.context = context;
        this.rawResponse = context.getResponseSupplier().get();
        return this;
    }

    public ContextAction<Response, Model> saveToContext(Function<Response, Model> mapper, String alias) {
        return contextActionProvider.getObject().handle(context, rawResponse, mapper, alias);
    }

    public ContextAction<Response, Model> saveToContext(Function<Response, Model> mapper) {
        return contextActionProvider.getObject().handle(context, rawResponse, mapper, null);
    }

    public ContextAction<Response, Model> saveToContext(Function<Response, List<Model>> mapper, String... aliases) {
        return contextActionProvider.getObject().handle(context, rawResponse, mapper, aliases);
    }
}