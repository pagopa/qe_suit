package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceTemplateParameterTypes {

    private final EntityStore entityStore;

    @ParameterType("e-service template|EService Template|Template|template|Template creato|template creato")
    public EServiceTemplate currentEServiceTemplate(String token) {
        return entityStore.getLastOrThrow(EServiceTemplate.class);
    }
}

