package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.kernel.utils.async.PollingUtils;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

@RequiredArgsConstructor
public class EServiceJourneySteps {

    private final InteropJourney interopJourney;

    @Given("un EService/eservice creato da/dal {tenant} con una richiesta di fruizione e una finalità associate da/dal {tenant}")
    public void createEServiceAndLinkAgreementAndPurpose(Tenant producer, Tenant consumer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(consumer, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .linkPurpose(PurposeVersionState.ACTIVE);
    }

    @Given("un EService/eservice creato da/dal {tenant} con una richiesta di fruizione in stato ACTIVE e una finalità in stato {purposeState} associate da/dal {tenant}")
    public void createEServiceAndLinkAgreementAndPurpose(Tenant producer, PurposeVersionState purposeState, Tenant consumer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(consumer, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .linkPurpose(purposeState);
    }

    @Given("un EService/eservice creato da/dal {tenant} con una richiesta di fruizione associata da/dal {tenant}")
    public void createEServiceAndLinkAgreement(Tenant producer, Tenant consumer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(consumer, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE);
    }

    @Given("un EService/eservice creato da/dal {tenant} con un descrittore divenuto DEPRECATED/deprecato dopo la fruizione di/del {tenant}")
    @Given("un EService/eservice creato da/dal {tenant} con una versione divenuta DEPRECATED/deprecata dopo la fruizione di/del {tenant}")
    public void createDeprecatedEServiceDescriptor(Tenant producer, Tenant consumer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(consumer, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(producer, UserRole.ADMIN)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .waitUntilEService(eservice -> eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.DEPRECATED);
    }
}
