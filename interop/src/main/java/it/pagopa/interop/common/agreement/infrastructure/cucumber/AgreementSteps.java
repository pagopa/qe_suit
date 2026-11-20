package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain
        .Agreement;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.SelectedEServiceDescriptor;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class AgreementSteps {
    private final AgreementUseCase agreementUseCase;
    private final AuthSession authSession;
    private final EntityStore entityStore;
    private final InteropJourney interopJourney;
    private final TenantUseCase tenantUseCase;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final EServiceRequestFactory eServiceRequestFactory;

    @When("l'erogatore accede ai dettagli della richiesta di fruizione ricevuta")
    public void viewProducerAgreementDetails() {
        Agreement agreement = entityStore.getLastOrThrow(Agreement.class);
        Tenant tenant = Tenant.fromOrganizationId(agreement.getProducerId());
        authSession.set(tenant);
        agreementUseCase.viewProducerAgreementDetails(agreement);
    }

    @When("il fruitore accede ai dettagli della richiesta di fruizione inoltrata")
    public void viewConsumerAgreementDetails() {
        Agreement agreement = entityStore.getLastOrThrow(Agreement.class);
        Tenant tenant = Tenant.fromOrganizationId(agreement.getConsumerId());
        authSession.set(tenant);
        agreementUseCase.viewConsumerAgreementDetails(agreement);
    }

    @When("l'erogatore approva la richiesta di fruizione")
    public void approveAgreement() {
        Agreement agreement = entityStore.getLastOrThrow(Agreement.class);
        Tenant producer = Tenant.fromOrganizationId(agreement.getProducerId());
        authSession.set(producer);
        viewProducerAgreementDetails();
        agreementUseCase.activateAgreement(agreement);
    }

    @Then("l'erogatore può scaricare il documento della richiesta di fruizione solo quanto è pronto")
    public void verifyAgreementDownloadAvailabilityForProducer() {
        viewProducerAgreementDetails();
        agreementUseCase.verifyProducerAgreementDownloadAvailability(entityStore.getLastOrThrow(Agreement.class));
    }

    @When("l'erogatore tenta di approvare la {currentAgreement}")
    public void attemptAgreementActivation(Agreement agreement) {
        agreementUseCase.attemptAgreementActivation(agreement);
    }

    @When("il fruitore tenta di inviare la {currentAgreement}")
    public void attemptAgreementSubmission(Agreement agreement) {
        agreementUseCase.attemptAgreementSubmission(agreement);
    }

    @Then("il fruitore può scaricare il documento della richiesta di fruizione solo quanto è pronto")
    public void verifyAgreementDownloadAvailabilityForConsumer() {
        viewConsumerAgreementDetails();
        agreementUseCase.verifyConsumerAgreementDownloadAvailability(entityStore.getLastOrThrow(Agreement.class));
    }

    @Given("associa un Agreement in stato DRAFT all'{currentEService}")
    public void createAgreement(EService eService) {
        agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor());
    }

    @Given("{eServiceByAlias} del/di {tenant} sottoscritto da/dal {tenant} con (una )richiesta di fruizione (in ){agreementState}")
    public void createAgreement(EService eService, Tenant producer, Tenant consumer, AgreementState targetState) {
        interopJourney
                .withConsumer(consumer)
                .linkAgreement(eService, targetState);
    }

    @When("il sistema impedisce a/al {tenant} di inoltrare una richiesta di fruizione per la {currentArchivedEServiceDescriptor} dell'{currentEService}")
    @When("il sistema impedisce a/al {tenant} di inoltrare una richiesta di fruizione per la {currentDeprecatedEServiceDescriptor} dell'{currentEService}")
    public void createAgreement(Tenant consumer, EServiceDescriptor eServiceDescriptor, EService eService) {
        authSession.set(consumer);
        AgreementCreationFailureReason reason = switch (eServiceDescriptor.getState()){
            case DRAFT,SUSPENDED,ARCHIVING_SUSPENDED, WAITING_FOR_APPROVAL -> AgreementCreationFailureReason.ESERVICE_INVALID_STATE;
            case DEPRECATED -> AgreementCreationFailureReason.DEPRECATED_VERSION;
            case ARCHIVED -> AgreementCreationFailureReason.ARCHIVED_STATE;
            default -> throw new IllegalArgumentException("Status not among the ones to be handled");
        };
        agreementUseCase.shouldFailToCreateAgreement(eService, eServiceDescriptor, reason);
    }

    @When("l'erogatore tenta di sospendere la {currentAgreement}")
    public void attemptAgreementSuspension(Agreement agreement) {
        agreementUseCase.attemptAgreementSuspension(agreement);
    }

    @When("il fruitore tenta di riattivare la {currentAgreement}")
    public void attemptAgreementReactivation(Agreement agreement) {
        agreementUseCase.attemptAgreementReactivation(agreement);
    }

    @When("il fruitore tenta di archiviare la {currentAgreement}")
    public void attemptAgreementArchiving(Agreement agreement) {
        agreementUseCase.attemptAgreementArchiving(agreement);
    }

    @When("il fruitore tenta di allegare un documento valido alla {currentAgreement}")
    public void attemptConsumerDocumentUpload(Agreement agreement) {
        agreementUseCase.attemptConsumerDocumentUpload(agreement);
    }

    @When("il fruitore tenta di creare una richiesta di fruizione per la versione più recente del{eServiceByAlias}")
    public void attemptAgreementCreation(EService eService) {
        agreementUseCase.attemptAgreementCreation(eService, eService.getLatestDescriptor());
    }

    @Given("il contratto della {currentAgreement} è disponibile")
    public void agreementContractAvailable(Agreement agreement) {
        agreementUseCase.awaitContractAvailable(agreement);
    }

    @Given("il documento firmato della {currentAgreement} non è ancora disponibile")
    public void agreementSignedContractNotYetAvailable(Agreement agreement) {
        agreementUseCase.verifySignedContractNotYetAvailable(agreement);
    }

    @Given("il file allegato alla {currentAgreement}")
    public void attachConsumerDocumentToAgreement(Agreement agreement) {
        agreementUseCase.addConsumerDocument(agreement, null);
    }

    @Given("il file {string} allegato alla {currentAgreement}")
    public void attachNamedConsumerDocumentToAgreement(String fileName, Agreement agreement) {
        agreementUseCase.addConsumerDocument(agreement, fileName);
    }

    @Given("il documento allegato alla {currentAgreement} viene eliminato")
    public void removeAttachedDocumentFromAgreement(Agreement agreement) {
        agreementUseCase.removeLastConsumerDocument(agreement);
    }

    @Given("la {currentAgreement} inviata")
    public void submitAgreement(Agreement agreement) {
        Agreement submitted = agreementUseCase.submitAgreement(agreement);
        agreementUseCase.awaitState(submitted, Set.of(AgreementState.PENDING));
    }

    @Given("l'{attributeByAlias} assegnato a/al {tenant} per la {currentAgreement}")
    public void assignVerifiedAttributeForAgreement(Attribute attribute, Tenant consumer, Agreement agreement) {
        tenantUseCase.assignVerifiedAttribute(
                TenantRef.of(consumer.getOrganizationId()),
                attribute.getRef(),
                agreement.getRef()
        );
    }

    @Given("la {currentAgreement} attivata")
    public void activateAgreement(Agreement agreement) {
        Agreement activated = agreementUseCase.activateAgreement(agreement);
        agreementUseCase.awaitState(activated, Set.of(AgreementState.ACTIVE));
    }

    @Given("{eServiceDescriptorByVersionAndAlias} configurata per l'approvazione manuale delle richieste di fruizione")
    public void configureDescriptorForManualAgreementApproval(SelectedEServiceDescriptor selected) {
        EService eService = selected.getEService();
        EServiceDescriptor descriptor = eService.getDescriptorOrThrow(selected.getVersion());
        Tenant producer = Tenant.fromOrganizationId(UUID.fromString(eService.getProducerId()));
        authSession.set(producer);

        UpdateEServiceDescriptorCommand command = eServiceRequestFactory.defaultUpdateDescriptorCommand()
                .agreementApprovalPolicy(AgreementApprovalPolicy.MANUAL);
        EServiceDescriptor updated = eServiceDescriptorUseCase.updateDescriptor(eService, descriptor, command);
        eService.upsertDescriptor(updated);
    }

    @Given("una richiesta di fruizione rifiutata per l'{currentEService}, creata da/dal {tenant}")
    @Given("una richiesta di fruizione rifiutata per {eServiceByAlias}, creata da/dal {tenant}")
    public void createRejectedAgreementForEService(EService eService, Tenant consumer) {
        interopJourney
                .withConsumer(consumer)
                .linkAgreement(eService, AgreementState.REJECTED);
    }

    @Given("una richiesta di fruizione rifiutata per {eServiceByAlias}, creata da/dal {tenant} e identificata come {string}")
    public void createRejectedAgreementForEServiceWithAlias(EService eService, Tenant consumer, String alias) {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("Agreement alias must not be blank");
        }
        if (entityStore.getByAlias(alias, Agreement.class).isPresent()) {
            throw new IllegalArgumentException("Agreement alias is already registered: " + alias);
        }
        interopJourney
                .withConsumer(consumer)
                .linkAgreement(eService, AgreementState.REJECTED, null, alias);
    }

    @When("il fruitore tenta di duplicare la {currentAgreement}")
    @When("il fruitore tenta di duplicare la {agreementByAlias}")
    public void attemptAgreementCloning(Agreement agreement) {
        agreementUseCase.attemptAgreementCloning(agreement);
    }

    @When("l'erogatore tenta di rifiutare la {currentAgreement} con una motivazione valida")
    public void attemptAgreementRejection(Agreement agreement) {
        agreementUseCase.attemptAgreementRejection(agreement);
    }

    @When("l'erogatore tenta di scaricare il documento firmato della {currentAgreement}")
    public void attemptAgreementSignedDocumentDownload(Agreement agreement) {
        agreementUseCase.attemptAgreementSignedDocumentDownload(agreement);
    }

    @When("il fruitore tenta di allegare la {currentAgreement} un file con formato non valido")
    public void attemptInvalidFormatConsumerDocumentUpload(Agreement agreement) {
        agreementUseCase.attemptInvalidFormatConsumerDocumentUpload(agreement);
    }

    @When("l'utente tenta di scaricare il documento dalla {currentAgreement}")
    public void attemptAgreementDocumentDownload(Agreement agreement) {
        agreementUseCase.attemptAgreementDocumentDownload(agreement);
    }

    @When("il fruitore tenta di eliminare il documento dalla {currentAgreement}")
    public void attemptAgreementDocumentDeletion(Agreement agreement) {
        agreementUseCase.attemptAgreementDocumentDeletion(agreement);
    }

}
