package it.pagopa.interop.service.eservice;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.web.pages.eservice_creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceWebService {

    private final EServiceCreationPage creationPage;

    public void createEservice(){
        creationPage.generalInformationStep().fillGeneralInformation(doDefaultRequest());
    }

    private EServiceSeed doDefaultRequest(){
        return new EServiceSeed()
                .name("Test eService")
                .description("Test eService description")
                .asyncExchange(false)
                .personalData(false)
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER);
    }

}
