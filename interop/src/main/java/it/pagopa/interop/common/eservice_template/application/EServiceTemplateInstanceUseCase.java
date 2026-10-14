package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EServiceTemplateInstanceUseCase {
    private final EServiceTemplateInstanceGateway gateway;

    public EService createEServiceInstance(EServiceTemplate template) {
        return gateway.createEServiceInstance(template);
    }
}
