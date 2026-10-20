package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServicePreparationConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServicePreparationConfigurationMapper;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EServiceJourneySteps {

    private final InteropJourney interopJourney;
    private final EServiceRequestFactory eServiceRequestFactory;
    private final EServicePreparationConfigurationMapper eServicePreparationConfigurationMapper;

    @Given("i seguenti e-service predisposti per la pubblicazione:")
    public void prepareEServicesForPublication(List<EServicePreparationConfiguration> configurations) {
        for (EServicePreparationConfiguration configuration : configurations) {
            EServiceCreationCommand command = eServiceRequestFactory.defaultCreationEServiceCommand();
            eServicePreparationConfigurationMapper.apply(configuration, command);

            interopJourney
                    .withProducer(configuration.getProducer(), UserRole.ADMIN)
                    .prepareEServiceForPublication(command);
        }
    }

    @Given("un EService/eservice creato da/dal {tenant} con una richiesta di fruizione e una finalità associate da/dal {tenant}")
    public void createEServiceAndLinkAgreementAndPurpose(Tenant producer, Tenant consumer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(consumer, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurpose(PurposeVersionState.ACTIVE);
    }

    @Given("un EService/eservice creato da/dal {tenant} con una richiesta di fruizione in stato ACTIVE e una finalità in stato {purposeState} associate da/dal {tenant}")
    public void createEServiceAndLinkAgreementAndPurpose(Tenant producer, PurposeVersionState purposeState, Tenant consumer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(consumer, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurpose(purposeState);
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

    @Given("un EService creato dal {tenant} con una versione divenuta deprecata dopo la fruizione di {tenant} ed EService in archiviazione")
    public void createDeprecatedAndArchivedEserviceDescriptor(Tenant producer, Tenant consumer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(consumer, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(producer, UserRole.ADMIN)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .waitUntilEService(eservice -> eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.DEPRECATED)
                .archiveEService(GracePeriodDays.NUMBER_60);
    }
}
