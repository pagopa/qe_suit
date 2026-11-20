package it.pagopa.interop.common.purpose.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.eservice.application.EServiceRiskAnalysisUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose.domain.RiskAnalysisSigningState;
import it.pagopa.interop.common.purpose.infrastructure.cucumber.dto.PurposeAliasConfiguration;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class PurposeSteps {

    private final EntityStore entityStore;
    private final InteropJourney interopJourney;
    private final PurposeUseCase purposeUseCase;
    private final EServiceRiskAnalysisUseCase eServiceRiskAnalysisUseCase;
    private final AuthSession authSession;

    @Given("l'e-service con una finalità in stato {purposeState} associata da/dal {tenant}")
    public void createPurposeForEService(PurposeVersionState purposeState, Tenant consumer) {
        interopJourney
                .withConsumer(consumer)
                .createPurpose(purposeState);
    }

    @Given("l'e-service con una finalità creata dal template in stato {purposeState} associata da/dal {tenant}")
    public void createPurposeFromTemplateForEService(PurposeVersionState purposeState, Tenant consumer) {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        EService eService = entityStore.getLastOrThrow(EService.class);

        interopJourney
                .withConsumer(consumer)
                .createPurposeFromTemplateAndLinkToEService(purposeTemplate, eService, purposeState);
    }

    @When("il fruitore aggiorna la stima di chiamate giornaliere oltre la sua soglia per la finalità")
    public void updatePurposeDailyCallsEstimateBeyondThreshold() {
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        EService eService = entityStore.getById(purpose.getEserviceId(), EService.class)
                .orElseThrow(() -> new IllegalStateException(
                        "E-service not found for purpose " + purpose.getId()
                ));
        Tenant consumer = Tenant.fromOrganizationId(purpose.getConsumerId());
        authSession.set(consumer);
        purposeUseCase.updatePurposeDailyCallsEstimateBeyondThreshold(purpose, eService);
    }

    @Then("{tenant} approva la finalità che va in stato {purposeState}")
    public void approvePurpose(Tenant tenant, PurposeVersionState expectedState) {
        processWaitingPurpose(tenant, expectedState,
                (purpose, version) -> purposeUseCase.activatePurpose(
                        purpose, version, null
                )
        );
    }

    @Then("{tenant} delegato all'erogazione approva la finalità che va in stato {purposeState}")
    public void approvePurposeWithDelegation(Tenant tenant, PurposeVersionState expectedState) {
        DelegationRef delegationRef = entityStore.getLastOrThrow(Delegation.class).getRef();
        processWaitingPurpose(tenant, expectedState,
                (purpose, version) -> purposeUseCase.activatePurpose(
                        purpose, version, delegationRef
                )
        );
    }

    @Then("{tenant} rifiuta la finalità che va in stato {purposeState}")
    public void rejectPurpose(Tenant tenant, PurposeVersionState expectedState) {
        processWaitingPurpose(tenant, expectedState,
                (purpose, version) -> purposeUseCase.rejectPurpose(
                        purpose, version, "Rifiutata nell'ambito del test"
                )
        );
    }

    private void processWaitingPurpose(Tenant tenant, PurposeVersionState expectedState, BiFunction<Purpose, PurposeVersion, Purpose> operation) {
        authSession.set(tenant);

        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        PurposeVersion waitingVersion = purpose.getLastWaitingForApprovalVersion();
        if (waitingVersion == null) {
            throw new IllegalStateException("No purpose version waiting for approval was found for purpose " + purpose.getId());
        }

        Purpose updatedPurpose = operation.apply(purpose, waitingVersion);
        PurposeVersionState actualState = updatedPurpose.findVersionById(waitingVersion.getId())
                .map(PurposeVersion::getPurposeVersionState)
                .orElseThrow(() -> new IllegalStateException(
                        "Processed purpose version was not returned for purpose " + purpose.getId()
                ));

        assertThat(actualState).isEqualTo(expectedState);
    }

    @Given("le seguenti finalità in bozza create da/dal {tenant} per l'e-service {string} con nomi distinti:")
    public void draftPurposesCreatedForEServiceWithDistinctNames(
            Tenant consumer,
            String eServiceAlias,
            List<PurposeAliasConfiguration> configurations
    ) {
        // Usato negli scenari con id ERROR_019, ERROR_020, ERROR_022, ERROR_024
        validateAliases(configurations);
        EService eService = entityStore.getByAliasOrThrow(eServiceAlias, EService.class);

        if (eService.getMode() == EServiceMode.RECEIVE) {
            EServiceRiskAnalysis riskAnalysis = entityStore.getLast(EServiceRiskAnalysis.class)
                    .orElseGet(() -> eServiceRiskAnalysisUseCase.getRiskAnalysis(eService));
            if (riskAnalysis == null) {
                throw new IllegalStateException("No EServiceRiskAnalysis found for RECEIVE e-service " + eService.getId());
            }
            for (PurposeAliasConfiguration configuration : configurations) {
                interopJourney
                        .withConsumer(consumer, UserRole.ADMIN)
                        .createReversePurposeAndLinkToEService(
                                eService,
                                riskAnalysis,
                                PurposeVersionState.DRAFT,
                                configuration.getAlias()
                        );
            }
        } else {
            for (PurposeAliasConfiguration configuration : configurations) {
                interopJourney
                        .withConsumer(consumer, UserRole.ADMIN)
                        .createPurposeAndLinkToEService(
                                eService,
                                PurposeVersionState.DRAFT,
                                configuration.getAlias()
                        );
            }
        }
    }

    @Given("un'altra finalità di/del {tenant} per l'e-service {string} con il nome che deriverebbe dalla duplicazione di {string}")
    public void anotherPurposeWithDuplicatedName(Tenant consumer, String eServiceAlias, String sourcePurposeAlias) {
        // Usato negli scenari con id ERROR_022
        EService eService = entityStore.getByAliasOrThrow(eServiceAlias, EService.class);
        Purpose sourcePurpose = entityStore.getByAliasOrThrow(sourcePurposeAlias, Purpose.class);

        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .clonePurpose(sourcePurpose, eService);
    }

    @Given("una finalità in bozza creata da/dal {tenant} dal template finalità per l'e-service {string} e identificata come {string}")
    public void draftPurposeCreatedFromTemplateForEService(Tenant consumer, String eServiceAlias, String purposeAlias) {
        // Usato negli scenari con id ERROR_024
        validateNewAlias(purposeAlias);
        EService eService = entityStore.getByAliasOrThrow(eServiceAlias, EService.class);
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);

        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .createPurposeFromTemplateAndLinkToEService(
                        purposeTemplate,
                        eService,
                        PurposeVersionState.DRAFT,
                        purposeAlias
                );
    }

    @Given("un'analisi del rischio della finalità in stato {string} assegnata al valutatore di/del {tenant}")
    public void purposeRiskAnalysisInStateAssignedToReviewer(String state, Tenant tenant) {
        // TODO: considerare di adattare la firma così che accetti {purposeState}
        // Usato negli scenari con id ERROR_025, ERROR_026, ERROR_028, ERROR_029, ERROR_030, ERROR_031, ERROR_032
        RiskAnalysisSigningState targetState = parseRiskAnalysisSigningState(state);
        interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(tenant, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurpose(PurposeVersionState.DRAFT);
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        interopJourney.bringPurposeRiskAnalysisToStateForReviewer(purpose, tenant, targetState);
    }

    private void validateAliases(List<PurposeAliasConfiguration> configurations) {
        if (configurations == null || configurations.isEmpty()) {
            throw new IllegalArgumentException("At least one purpose alias is required");
        }
        Set<String> seen = new HashSet<>();
        for (PurposeAliasConfiguration configuration : configurations) {
            if (configuration == null || configuration.getAlias() == null || !seen.add(configuration.getAlias())) {
                throw new IllegalArgumentException("Purpose aliases must be present and unique");
            }
            validateNewAlias(configuration.getAlias());
        }
    }

    private void validateNewAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("Purpose alias must not be blank");
        }
        if (entityStore.getByAlias(alias, Purpose.class).isPresent()) {
            throw new IllegalArgumentException("Purpose alias is already registered: " + alias);
        }
    }

    private static RiskAnalysisSigningState parseRiskAnalysisSigningState(String state) {
        return switch (state) {
            case "approvata" -> RiskAnalysisSigningState.SIGNED;
            case "rifiutata" -> RiskAnalysisSigningState.REJECTED;
            case "in attesa di valutazione" -> RiskAnalysisSigningState.SUBMITTED;
            default -> throw new IllegalArgumentException("Stato dell'analisi del rischio non supportato: " + state);
        };
    }

    @When("il fruitore tenta di modificare la {purposeByAlias} con il nome della {purposeByAlias}")
    public void attemptNameUpdateWithAnotherPurposeName(Purpose purpose, Purpose existing) {
        purposeUseCase.attemptNameUpdate(purpose, existing.getTitle());
    }

    @When("il fruitore tenta di modificare la {currentPurpose} con il nome {string}")
    public void attemptNameUpdate(Purpose purpose, String name) {
        purposeUseCase.attemptNameUpdate(purpose, name);
    }

    @When("il fruitore tenta di duplicare la {purposeByAlias}")
    public void attemptPurposeCloning(Purpose purpose) {
        purposeUseCase.attemptPurposeCloning(purpose);
    }

    @When("il valutatore tenta di firmare l'analisi del rischio della {currentPurpose}")
    public void attemptRiskAnalysisSigning(Purpose purpose) {
        purposeUseCase.attemptRiskAnalysisSigning(purpose);
    }

    @When("il valutatore tenta di rifiutare l'analisi del rischio della {currentPurpose} con la motivazione {string}")
    public void attemptRiskAnalysisRejection(Purpose purpose, String rejectionReason) {
        purposeUseCase.attemptRiskAnalysisRejection(purpose, rejectionReason);
    }

    @When("il fruitore tenta di creare una finalità per l'e-service con il nome della finalità")
    public void attemptCreatePurposeWithExistingPurposeName() {
        // Usato negli scenari con id ERROR_019
        Purpose existingPurpose = entityStore.getLastOrThrow(Purpose.class);
        EService eService = entityStore.getById(existingPurpose.getEserviceId(), EService.class)
                .orElseGet(() -> entityStore.getLastOrThrow(EService.class));

        if (eService.getMode() == EServiceMode.RECEIVE) {
            EServiceRiskAnalysis riskAnalysis = entityStore.getLast(EServiceRiskAnalysis.class)
                    .orElseGet(() -> eServiceRiskAnalysisUseCase.getRiskAnalysis(eService));
            if (riskAnalysis == null) {
                throw new IllegalStateException(
                        "No EServiceRiskAnalysis found for RECEIVE e-service " + eService.getId()
                );
            }
            purposeUseCase.attemptCreateReversePurposeWithTitle(
                    eService, riskAnalysis, existingPurpose.getTitle()
            );
        } else {
            purposeUseCase.attemptCreatePurposeWithTitle(eService, existingPurpose.getTitle());
        }
    }

    @When("il fruitore tenta di creare una finalità dal template finalità per l'e-service con il nome della finalità")
    public void attemptCreatePurposeFromTemplateWithExistingPurposeName() {
        // Usato negli scenari con id ERROR_023
        Purpose existingPurpose = entityStore.getLastOrThrow(Purpose.class);
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        EService eService = entityStore.getById(existingPurpose.getEserviceId(), EService.class)
                .orElseGet(() -> entityStore.getLastOrThrow(EService.class));
        purposeUseCase.attemptCreatePurposeFromTemplateWithTitle(
                purposeTemplate, eService, existingPurpose.getTitle()
        );
    }

    @When("il fruitore tenta di modificare la finalità da template {string} con il nome della finalità {string}")
    public void attemptUpdatePurposeFromTemplateWithExistingPurposeName(String targetPurposeAlias, String sourcePurposeAlias) {
        // Usato negli scenari con id ERROR_024
        Purpose targetPurpose = entityStore.getByAliasOrThrow(targetPurposeAlias, Purpose.class);
        Purpose sourcePurpose = entityStore.getByAliasOrThrow(sourcePurposeAlias, Purpose.class);
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeUseCase.attemptNameUpdateFromTemplate(
                purposeTemplate, targetPurpose, sourcePurpose.getTitle()
        );
    }

    @When("il valutatore tenta di salvare risposte valide nell'analisi del rischio della finalità")
    public void attemptSaveValidAnswersInPurposeRiskAnalysis() {
        // Usato negli scenari con id ERROR_030, ERROR_031
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        purposeUseCase.attemptRiskAnalysisFormUpdate(purpose);
    }
}
