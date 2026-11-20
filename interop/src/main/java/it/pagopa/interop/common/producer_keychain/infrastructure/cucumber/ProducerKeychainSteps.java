package it.pagopa.interop.common.producer_keychain.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.kernel.domain.KeyRegistrationMaterial;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainFactory;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainUseCase;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProducerKeychainSteps {
    private final InteropJourney journey;
    private final ProducerKeychainFactory factory;
    private final ProducerKeychainUseCase useCase;

    @Given("un portachiavi erogatore creato dal {tenant}")
    public void createProducerKeychain(Tenant producer) {
        journey.withProducer(producer, UserRole.ADMIN)
                .createProducerKeychain(factory.creationCommand(
                        List.of(User.getTenantAdmin(producer).getUserId())));
    }

    @When("l'utente tenta di associare il {currentProducerKeychain} al{eServiceByAlias}")
    public void attemptEServiceAssociation(ProducerKeychain keychain, EService eService) {
        useCase.attemptEServiceAssociation(keychain.getRef(), eService.getRef());
    }

    @When("l'erogatore tenta di aggiungere la chiave di test \"{keyRegistrationMaterial}\" al \"{currentProducerKeychain}\" con il nome {string}")
    public void attemptKeyRegistration(KeyRegistrationMaterial material, ProducerKeychain keychain, String name) {
        useCase.attemptKeyRegistration(keychain, material, name);
    }

    @When("l'utente tenta di creare un portachiavi di erogazione con il nome {string}")
    public void attemptProducerKeychainCreation(String name) {
        useCase.attemptProducerKeychainCreation(factory.creationCommand().name(name));
    }
}
