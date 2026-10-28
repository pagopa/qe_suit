package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
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
import java.util.Map;

@RequiredArgsConstructor
public class EServiceCreationJourneySteps {

    private final InteropJourney interopJourney;
    private final EServiceRequestFactory eServiceRequestFactory;
    private final EServicePreparationConfigurationMapper eServicePreparationConfigurationMapper;

    @Given("i seguenti e-service in bozza:")
    public void prepareEServicesForPublication(List<EServicePreparationConfiguration> configurations) {
        for (EServicePreparationConfiguration configuration : configurations) {
            EServiceCreationCommand command = eServiceRequestFactory.defaultCreationEServiceCommand();
            eServicePreparationConfigurationMapper.apply(configuration, command);

            interopJourney
                    .withProducer(configuration.getProducer(), UserRole.ADMIN)
                    .createEService(command);
        }
    }

    @Given("i seguenti e-service pubblicati:")
    public void publishEServices(List<EServicePreparationConfiguration> configurations) {
        for (EServicePreparationConfiguration configuration : configurations) {
            EServiceCreationCommand command = eServiceRequestFactory.defaultCreationEServiceCommand();
            eServicePreparationConfigurationMapper.apply(configuration, command);

            interopJourney
                    .withProducer(configuration.getProducer(), UserRole.ADMIN)
                    .createEService(command)
                    .prepareEServiceDescriptorForPublication(1)
                    .publishDescriptor(1);
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


    @Given("un e-service in {eServiceDescriptorState}, creato dal {tenant}, con un attributo certificato e una soglia personalizzata per fruitore pari a {int}")
    public void createEService(EServiceDescriptorState state, Tenant producer, int customThreshold, DataTable defaultThresholds) {

        Map<String, Integer> thresholdsMap = defaultThresholds.asMap(String.class, Integer.class);

        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createCertifiedAttribute()
                .createEService(EServiceDescriptorState.DRAFT)
                .updateDescriptorWithContextAttributes(
                        config -> config
                                .dailyCallsPerConsumer(thresholdsMap.get("consumerThreshold"))
                                .dailyCallsTotal(thresholdsMap.get("totalThreshold")),
                        customThreshold,
                        state
                )
                .waitUntilEService(eService ->
                        eService.getLatestDescriptor().getState() == state
                );
    }

    @Given("una nuova versione dell'e-service in {eServiceDescriptorState}, creata dal {tenant}, con un attributo certificato e una soglia personalizzata per fruitore pari a {int}")
    public void createNewInstanceDraftEService(EServiceDescriptorState state, Tenant producer, int customThreshold, DataTable defaultThresholds) {
        Map<String, Integer> thresholdsMap = defaultThresholds.asMap(String.class, Integer.class);

        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createCertifiedAttribute()
                .createEService(EServiceDescriptorState.DRAFT)
                .updateDescriptorWithContextAttributes(
                        config -> config
                                .dailyCallsPerConsumer(thresholdsMap.get("consumerThreshold"))
                                .dailyCallsTotal(thresholdsMap.get("totalThreshold")),
                        customThreshold,
                        EServiceDescriptorState.PUBLISHED
                )
                .waitUntilEService(eService ->
                        eService.getLatestDescriptor().getState() == EServiceDescriptorState.PUBLISHED
                )
                .addDescriptor(state)
                .waitUntilEService(eService ->
                        eService.getDescriptors().size() == 2 && eService.getLatestDescriptor().getState() == state
                );
    }
}
