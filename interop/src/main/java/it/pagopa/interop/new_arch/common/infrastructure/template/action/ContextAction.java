package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.ApiContext;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.BaseActionContext;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContextAction<Response, Model extends Identifiable> implements DomainFinalizer<Model>, ApiFinalizer<Response> {

    @Setter(onMethod_ = {@Autowired})
    private DomainContext domainContext;
    @Setter(onMethod_ = {@Autowired})
    private ApiContext apiContext;
    @Getter
    private ApiResponse rawResponse;
    @Getter
    private BaseActionContext context;
    @Getter
    private Model model;
    @Getter
    private List<Model> models;


    ContextAction<Response, Model> handle(BaseActionContext context, ApiResponse rawResponse, Function<Response, Model> mapper, String alias) {
        this.context = context;
        this.rawResponse = rawResponse;

        return saveToContext(mapper, alias);
    }

    ContextAction<Response, Model> handle(BaseActionContext context, ApiResponse rawResponse, Function<Response, List<Model>> mapper, String... aliases) {
        this.context = context;
        this.rawResponse = rawResponse;
        return saveListToContext(mapper, aliases);
    }

    @SuppressWarnings("unchecked")
    private ContextAction<Response, Model> saveToContext(Function<Response, Model> mapper, String alias) {
        Response resp = (Response) rawResponse.as(context.getResponseClass());
        Model model = mapper.apply(resp);
        this.model = model;

        if (model != null) {
            apiContext.setLastResponse(rawResponse);
            domainContext.upsert(new DomainContext.ContextEntry<>(model, alias));
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private ContextAction<Response, Model> saveListToContext(Function<Response, List<Model>> mapper, String... aliases) {
        Response resp = (Response) rawResponse.as(context.getResponseClass());
        List<Model> models = mapper.apply(resp);
        this.models = models;

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