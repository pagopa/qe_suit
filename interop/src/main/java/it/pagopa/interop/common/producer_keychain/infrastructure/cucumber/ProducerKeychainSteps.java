package it.pagopa.interop.common.producer_keychain.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProducerKeychainSteps {
    private final InteropJourney journey;
    private final ProducerKeychainFactory factory;

    @Given("un portachiavi erogatore creato dal {tenant}")
    public void createProducerKeychain(Tenant producer) {
        journey.withProducer(producer, UserRole.ADMIN)
                .createProducerKeychain(factory.creationCommand(
                        List.of(User.getTenantAdmin(producer).getUserId())));
    }
}
