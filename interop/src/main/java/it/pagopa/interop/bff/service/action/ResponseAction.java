package it.pagopa.interop.bff.service.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.domain.context.ScenarioContext;
import it.pagopa.interop.common.domain.model.DomainModelRegistry;
import it.pagopa.interop.common.domain.model.TestModel;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResponseAction<Entity> {

    @Setter(onMethod_ = {@Autowired}) private ScenarioContext scenarioContext;
    @Setter(onMethod_ = {@Autowired}) private DomainModelRegistry domainModelRegistry;
    @Setter(onMethod_ = {@Autowired}) private ObjectMapper objectMapper;
    @Setter(onMethod_ = {@Autowired}) private ObjectProvider<AssertAction<Entity>> assertActionProvider;

    private ResponseEntity<Entity> response;

    ResponseAction<Entity> handle(ResponseEntity<Entity> response) {
        this.response = response;
        this.syncContext();
        return this;
    }

    private void syncContext() {
        // 1. SALVATAGGIO INCONDIZIONATO: Registra sempre l'ultima ResponseEntity nel contesto
        scenarioContext.upsert(response);

        // 2. Logica di business sul dominio (attiva SOLO per i successi 2xx)
        Entity body = response.getBody();
        if (response.getStatusCode().is2xxSuccessful() && body != null) {
            Class<? extends TestModel> domainClass = domainModelRegistry.getDomainClassFor(body.getClass());

            if (domainClass != null) {
                TestModel domainModel = objectMapper.convertValue(body, domainClass);
                scenarioContext.upsert(domainModel);
            } else if (body instanceof TestModel testModel) {
                scenarioContext.upsert(testModel);
            } else {
                scenarioContext.upsert(body);
            }
        }
    }

    public Entity body() {
        return response.getBody();
    }

    public ResponseEntity<Entity> raw() {
        return response;
    }

    public AssertAction<Entity> andAssert() {
        return assertActionProvider.getObject().handle(response);
    }
}