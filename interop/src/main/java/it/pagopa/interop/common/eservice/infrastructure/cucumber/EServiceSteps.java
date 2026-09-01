package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.context.EntityStore;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@RequiredArgsConstructor
public class EServiceSteps {
    private final EServiceUseCase eServiceUseCase;
    private final EServiceDescriptorGateway eServiceDescriptorGateway;
    private final CurrentUserSession userContext;
    private final EntityStore entityStore;

    @When("{tenant} crea una nuova versione del {currentEService}")
    public void createNewEserviceVersion(Tenant tenant, EService eService){
        userContext.set(User.getTenantAdmin(tenant), tenant);
        eServiceUseCase.addDescriptor(eService);

        EServiceDescriptor descriptor = eService.getDescriptors().get(eService.getDescriptors().size()-1);
        String openApiInterface = "assets/origin-interface.yaml";
        eServiceDescriptorGateway.linkOpenApiInterface(eService.getRef(), descriptor.getRef(), openApiInterface);
        eServiceDescriptorGateway.publishDescriptor(eService.getRef(), descriptor.getRef());
    }

    @Given("il {tenant} consulta la pagina dell'eservice")
    public void ilPotenzialeFruitoreConsultaLaPaginaDellEservice(Tenant potenzialeFruitore) {
        EService eService = entityStore.getLastOrThrow(EService.class);

        UUID eserviceId = eService.getId();
        UUID lastDescriptorId = eService.getDescriptors()
                .get(eService.getDescriptors().size() - 1)
                .getId();
        // ... naviga alla pagina web usando eserviceId e lastDescriptorId
    }

    @Then("il {tenant} trova il pulsante di richiesta di fruizione disabilitato per tutte le versioni antecedenti l'ultima")
    public void ilPotenzialeFruitoreTrovaIlPulsanteDiRichiestaDiFruizioneDisabilitatoPerTutteLeVersioniAntecedentiLUltima(Tenant potenzialeFruitore) {
        // Write code here that turns the phrase above into concrete actions
        System.out.println("il fruitore " + potenzialeFruitore + " fa cose ...");
    }
}
