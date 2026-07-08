package it.pagopa.interop.new_arch.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementState;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.new_arch.common.journey.application.InteropJourney;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.kernel.domain.UserRole;
import it.pagopa.interop.new_arch.common.purpose.domain.PurposeVersionState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceJourneySteps {

    private final InteropJourney interopJourney;

    @Given("un EService creato da/dal {tenant} con una richiesta di fruizione e una finalità associate da/dal {tenant}")
    public void createClientWithAgreementAndPurpose(Tenant producer, Tenant consumer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(consumer, UserRole.ADMIN)
                .linkAgreementInState(AgreementState.ACTIVE)
                .linkPurposeInState(PurposeVersionState.ACTIVE);
    }
}
