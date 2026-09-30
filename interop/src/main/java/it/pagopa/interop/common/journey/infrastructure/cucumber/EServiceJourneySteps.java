package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class EServiceJourneySteps {

    private final InteropJourney interopJourney;
    private final EntityStore entityStore;

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

    @Given("un EService creato dal {tenant} con una versione divenuta deprecata dopo la fruizione di {tenant} ed EService in archiviazione")
    public void createDeprecatedAndArchivedEserviceDescriptor(Tenant producer, Tenant consumer){
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

    @Given("crea un EService {asyncExchange} con un attributo certificato e soglia personalizzata per fruitore a {int}")
    public void createEServiceWithCertifiedAttributeAndCustomThreshold(boolean asyncExchange, int customThreshold, DataTable defaultThresholds) {
        Map<String, Integer> thresholdsMap = defaultThresholds.asMap(String.class, Integer.class);

        interopJourney.createCertifiedAttribute();

        EServiceCreationCommand eServiceCommand = new BffEServiceCreationCommand()
                .name("e-service-" + Instant.now().getEpochSecond())
                .description("Primo descrittore")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .isAsync(asyncExchange)
                .handlePersonalData(false)
                .isConsumerDelegable(true);

        UpdateEServiceDescriptorCommand updateCommand = new BffUpdateEServiceDescriptorCommand()
                .dailyCallsPerConsumer(thresholdsMap.getOrDefault("consumerThreshold", 10))
                .dailyCallsTotal(thresholdsMap.getOrDefault("totalThreshold", 20))
                .voucherLifespan(60)
                .audience(List.of("Audience"))
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC);

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed();
        DescriptorAttributeSeed certifiedItem = new DescriptorAttributeSeed();
        Attribute attribute = entityStore.getLastOrThrow(Attribute.class);
        certifiedItem.setId(attribute.getId());
        certifiedItem.setDailyCallsPerConsumer(customThreshold);
        certifiedItem.setExplicitAttributeVerification(false);
        attributesSeed.addCertifiedItem(List.of(certifiedItem));
        updateCommand.attributes(attributesSeed);

        interopJourney
                .createEService(eServiceCommand, EServiceDescriptorState.DRAFT)
                .updateDescriptor(updateCommand, EServiceDescriptorState.PUBLISHED);
    }
}
