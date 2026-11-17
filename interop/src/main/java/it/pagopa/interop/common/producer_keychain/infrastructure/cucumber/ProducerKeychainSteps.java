package it.pagopa.interop.common.producer_keychain.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
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
}
