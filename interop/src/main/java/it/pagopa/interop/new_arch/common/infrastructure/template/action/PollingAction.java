package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.ApiContext;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.PollingActionContext;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.AssertionStrategy;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PollingAction<Response, Model extends Identifiable> implements Finalizer<Response, Model> {

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<AssertAction<Response, Model>> assertActionProvider;
    @Setter(onMethod_ = {@Autowired})
    private DomainContext domainContext;
    @Setter(onMethod_ = {@Autowired})
    private ApiContext apiContext;
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

    public AssertAction<Response, Model> andAssertThat(AssertionStrategy assertionStrategy) {
        return assertActionProvider.getObject().handle(rawResponse, context, assertionStrategy);
    }

    @SuppressWarnings("unchecked")
    public PollingAction<Response, Model> saveToContext(Function<Response, Model> mapper, String alias) {
        Response resp = (Response) rawResponse.as(context.getResponseClass());
        Model model = mapper.apply(resp);

        if (model != null) {
            apiContext.setLastResponse(rawResponse);
            domainContext.upsert(new DomainContext.ContextEntry<>(model, alias));
        }
        return this;
    }

    public PollingAction<Response, Model> saveToContext(Function<Response, Model> mapper) {
        return this.saveToContext(mapper, null);
    }

    @SuppressWarnings("unchecked")
    public PollingAction<Response, Model> saveListToContext(Function<Response, List<Model>> mapper, String... aliases) {
        Response resp = (Response) rawResponse.as(context.getResponseClass());
        List<? extends Identifiable> models = mapper.apply(resp);

        if (aliases.length > models.size()) {
            throw new IllegalArgumentException("The given aliases exceed the maximum number of test models");
        }

        List<DomainContext.ContextEntry<? extends Identifiable>> contextEntries = new ArrayList<>();

        for (int i = 0; i < models.size(); i++) {
            Identifiable model = models.get(i);
            String modelAlias = i < aliases.length ? aliases[i] : null;
            contextEntries.add(new DomainContext.ContextEntry<>(model, modelAlias));
        }

        apiContext.setLastResponse(rawResponse);
        domainContext.upsert(contextEntries);

        return this;
    }
}