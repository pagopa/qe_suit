package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
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
                .linkAgreement(AgreementState.ACTIVE)
                .linkPurpose(PurposeVersionState.ACTIVE);
    }
}
