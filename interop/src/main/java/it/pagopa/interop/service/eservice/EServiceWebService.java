package it.pagopa.interop.service.eservice;

import static it.pagopa.interop.web.pages.eservice_creation.component.TechnicalSpecificationStepComponent.TechnicalSpecificationStepSeed;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.web.pages.eservice_creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceWebService {

    private final EServiceCreationPage creationPage;

    public void createEservice() {
        creationPage
                .fillGeneralInformation(doDefaultRequest())
                .skipThresholdAndAttribute()
                .fillTechnicalSpecification(TechnicalSpecificationStepSeed.buildDefault());
    }


    private EServiceSeed doDefaultRequest() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);

        return new EServiceSeed()
                .name("Test eService " + randomSuffix)
                .description("Test eService description")
                .asyncExchange(false)
                .personalData(false)
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER);
    }

}
