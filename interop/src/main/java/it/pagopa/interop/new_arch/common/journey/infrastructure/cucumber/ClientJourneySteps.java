package it.pagopa.interop.new_arch.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.new_arch.common.infrastructure.security.crypto.Key;
import it.pagopa.interop.new_arch.common.infrastructure.security.crypto.KeyAlgorithm;
import it.pagopa.interop.new_arch.common.journey.application.InteropJourney;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.kernel.domain.UserRole;
import it.pagopa.interop.new_arch.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientJourneySteps {
    private final InteropJourney interopJourney;

    @Given("un client CONSUMER creato da/dal {tenant}, associato alla {currentPurpose}, in cui è presente un utente {userRole} e una coppia di chiavi crittografiche")
    public void setupConsumerClient(Tenant consumer, Purpose purpose, UserRole userRole) {
        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .createClientConsumer(Key.generate(KeyAlgorithm.RSA), userRole)
                .linkPurposeToClient(purpose.getId());
    }

    @Given("un client API creato da/dal {tenant} in cui è presente un utente {userRole} e una coppia di chiavi crittografiche")
    public void setupApiClient(Tenant consumer, UserRole userRole) {
        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .createApiClient(Key.generate(KeyAlgorithm.RSA), userRole);
    }
}
