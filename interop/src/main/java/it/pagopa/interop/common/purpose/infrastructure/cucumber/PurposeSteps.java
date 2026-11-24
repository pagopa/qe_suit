package it.pagopa.interop.common.purpose.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
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
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose.domain.RiskAnalysisSigningState;
import it.pagopa.interop.common.purpose.infrastructure.cucumber.dto.PurposeAliasConfiguration;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
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
    private Purpose purposeReference;

    @Given("i dati della finalità allo stato attuale appuntati come riferimento")
    public void savePurposeReference() {
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        purpose = purposeUseCase.getPurpose(purpose);
        purposeReference = purpose.toBuilder().build();
    }

    @ParameterType("il nome e la descrizione|il nome|la descrizione")
    public PurposeMetadataField purposeMetadataField(String field) {
        return switch (field) {
            case "il nome" -> PurposeMetadataField.NAME;
            case "la descrizione" -> PurposeMetadataField.DESCRIPTION;
            case "il nome e la descrizione" -> PurposeMetadataField.NAME_AND_DESCRIPTION;
            default -> throw new IllegalArgumentException("Campo metadato finalita non supportato: " + field);
        };
    }

    @Then("la finalità conserva lo stato e i dati di riferimento")
    public void assertPurposePreserved() {
        assertPurposePreserved(Set.of(), "Stato e dati della finalita");
    }

    @Then("la finalità conserva lo stato e i dati di riferimento eccetto {purposeMetadataField}")
    public void assertPurposePreservedExceptField(PurposeMetadataField excludedField) {
        assertPurposePreserved(excludedField.fieldNames,
                "Stato e dati della finalita eccetto " + excludedField.label);
    }

    private void assertPurposePreserved(Set<String> excludedFields, String description) {
        assertThat(purposeReference)
                .as("Riferimento della finalita salvato prima della modifica")
                .isNotNull();
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        purpose = purposeUseCase.getPurpose(purpose);

        if (excludedFields.contains("title")) {
            assertThat(purpose.getTitle())
                    .as("Nome della finalita dopo la modifica")
                    .isNotEqualTo(purposeReference.getTitle());
        }
        if (excludedFields.contains("description")) {
            assertThat(purpose.getDescription())
                    .as("Descrizione della finalita dopo la modifica")
                    .isNotEqualTo(purposeReference.getDescription());
        }
        assertThat(purpose)
                .as(description)
                .usingRecursiveComparison()
                .ignoringFields("versions.updatedAt")
                .ignoringFields(excludedFields.toArray(String[]::new))
                .isEqualTo(purposeReference);
    }

    public enum PurposeMetadataField {
        NAME(Set.of("title"), "il nome"),
        DESCRIPTION(Set.of("description"), "la descrizione"),
        NAME_AND_DESCRIPTION(Set.of("title", "description"), "il nome e la descrizione");

        private final Set<String> fieldNames;
        private final String label;

        PurposeMetadataField(Set<String> fieldNames, String label) {
            this.fieldNames = fieldNames;
            this.label = label;
        }
    }

    @When("un {userRole} di/del {tenant} modifica i metadati della finalità:")
    public void updatePurposeMetadata(UserRole role, Tenant consumer, DataTable dataTable) {
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        MetadataUpdate metadata = readMetadataUpdate(purpose, dataTable);
        EService eService = entityStore.getById(purpose.getEserviceId(), EService.class)
                .orElseThrow(() -> new IllegalStateException(
                        "E-service not found for purpose " + purpose.getId()
                ));

        if (role == UserRole.ADMIN) {
            interopJourney.withConsumer(consumer);
        } else {
            interopJourney.withConsumer(consumer, role);
        }
        if (eService.getMode() == EServiceMode.RECEIVE) {
            purposeUseCase.updateReversePurposeMetadata(
                    purpose, metadata.name(), metadata.description(), metadata.isFreeOfCharge(),
                    metadata.freeOfChargeReason(), metadata.dailyCalls());
        } else if (eService.getMode() == EServiceMode.DELIVER) {
            purposeUseCase.updatePurposeMetadata(
                    purpose, metadata.name(), metadata.description(), metadata.isFreeOfCharge(),
                    metadata.freeOfChargeReason(), metadata.dailyCalls());
        } else {
            throw new IllegalStateException("E-service mode is missing for purpose " + purpose.getId());
        }
    }

    @When("un {userRole} di/del {tenant} tenta di modificare i metadati della finalità:")
    public void tryToUpdatePurposeMetadata(UserRole role, Tenant consumer, DataTable dataTable) {
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        MetadataUpdate metadata = readMetadataUpdate(purpose, dataTable);
        EService eService = entityStore.getById(purpose.getEserviceId(), EService.class)
                .orElseThrow(() -> new IllegalStateException(
                        "E-service not found for purpose " + purpose.getId()
                ));

        if (role == UserRole.ADMIN) {
            interopJourney.withConsumer(consumer);
        } else {
            interopJourney.withConsumer(consumer, role);
        }
        if (eService.getMode() == EServiceMode.RECEIVE) {
            purposeUseCase.tryToUpdateReversePurposeMetadata(
                    purpose, metadata.name(), metadata.description(), metadata.isFreeOfCharge(),
                    metadata.freeOfChargeReason(), metadata.dailyCalls());
        } else if (eService.getMode() == EServiceMode.DELIVER) {
            purposeUseCase.tryToUpdatePurposeMetadata(
                    purpose, metadata.name(), metadata.description(), metadata.isFreeOfCharge(),
                    metadata.freeOfChargeReason(), metadata.dailyCalls());
        } else {
            throw new IllegalStateException("E-service mode is missing for purpose " + purpose.getId());
        }
    }

    @Then("la richiesta di modifica dei metadati della finalità fallisce come operazione malformata")
    public void shouldFailToUpdatePurposeMetadataAsMalformed() {
        purposeUseCase.shouldFailToUpdatePurposeMetadataAsMalformed();
    }

    @Then("la richiesta di modifica dei metadati della finalità fallisce come operazione vietata")
    public void shouldFailToUpdatePurposeMetadataAsForbidden() {
        purposeUseCase.shouldFailToUpdatePurposeMetadataAsForbidden();
    }

    @Then("la richiesta di modifica dei metadati della finalità fallisce per conflitto di risorsa")
    public void shouldFailToUpdatePurposeMetadataAsConflict() {
        purposeUseCase.shouldFailToUpdatePurposeMetadataAsConflict();
    }

    private MetadataUpdate readMetadataUpdate(Purpose purpose, DataTable dataTable) {
        List<List<String>> rows = dataTable.cells();
        if (rows.size() < 2 || !rows.get(0).equals(List.of("campo", "valore"))) {
            throw new IllegalArgumentException(
                    "La tabella dei metadati della finalita deve avere le colonne 'campo' e 'valore' e almeno un campo"
            );
        }

        String name = purpose.getTitle();
        String description = purpose.getDescription();
        Boolean isFreeOfCharge = null;
        String freeOfChargeReason = null;
        Integer dailyCalls = null;
        Set<String> updatedFields = new HashSet<>();

        for (List<String> row : rows.subList(1, rows.size())) {
            if (row.size() != 2 || row.get(0) == null) {
                throw new IllegalArgumentException("Ogni riga dei metadati della finalita deve contenere campo e valore");
            }

            String field = row.get(0);
            if (!updatedFields.add(field)) {
                throw new IllegalArgumentException("Campo metadato finalita duplicato: " + field);
            }

            String value = "%empty".equals(row.get(1)) ? "" : row.get(1);
            switch (field) {
                case "title" -> name = value;
                case "description" -> description = value;
                case "isFreeOfCharge" -> isFreeOfCharge = parseBoolean(field, value);
                case "freeOfChargeReason" -> freeOfChargeReason = value;
                case "dailyCalls" -> dailyCalls = parseInteger(field, value);
                default -> throw new IllegalArgumentException("Campo metadato finalita non supportato: " + field);
            }
        }

        return new MetadataUpdate(name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls);
    }

    private Boolean parseBoolean(String field, String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("Valore booleano non valido per il campo " + field + ": " + value);
    }

    private Integer parseInteger(String field, String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Valore intero non valido per il campo " + field + ": " + value, exception);
        }
    }

    private record MetadataUpdate(
            String name,
            String description,
            Boolean isFreeOfCharge,
            String freeOfChargeReason,
            Integer dailyCalls
    ) {}

    @Given("l'e-service con una finalità in stato {purposeState} associata da/dal {tenant}")
    public void createPurposeForEService(PurposeVersionState purposeState, Tenant consumer) {
        interopJourney
                .withConsumer(consumer)
                .createPurpose(purposeState);
    }

    @Given("l'e-service con una finalità inversa in stato {purposeState} associata da/dal {tenant}")
    public void createReversePurposeForEService(PurposeVersionState purposeState, Tenant consumer) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceRiskAnalysis riskAnalysis = entityStore.getLastOrThrow(EServiceRiskAnalysis.class);

        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .createReversePurposeAndLinkToEService(eService, riskAnalysis, purposeState);
    }

    @Given("l'e-service con una finalità creata dal template in stato {purposeState} associata da/dal {tenant}")
    public void createPurposeFromTemplateForEService(PurposeVersionState purposeState, Tenant consumer) {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        EService eService = entityStore.getLastOrThrow(EService.class);

        interopJourney
                .withConsumer(consumer)
                .createPurposeFromTemplateAndLinkToEService(purposeTemplate, eService, purposeState);
    }

    @Given("una finalità pubblicata da/dal {tenant} dal template finalità")
    public void createPublishedPurposeFromTemplate(Tenant consumer) {
        createPurposeFromLastPublishedTemplate(consumer, PurposeVersionState.ACTIVE);
    }

    @Given("una finalità in bozza creata da/dal {tenant} dal template finalità")
    public void createDraftPurposeFromTemplate(Tenant consumer) {
        createPurposeFromLastPublishedTemplate(consumer, PurposeVersionState.DRAFT);
    }

    private void createPurposeFromLastPublishedTemplate(Tenant consumer, PurposeVersionState state) {
        PurposeTemplate purposeTemplate = entityStore.getAll(PurposeTemplate.class).stream()
                .filter(template -> template.getState() == PurposeTemplateState.PUBLISHED)
                .reduce((previous, current) -> current)
                .orElseThrow(() -> new IllegalStateException(
                        "Nessun template finalita pubblicato presente nell'entity store"));
        EService eService = entityStore.getLastOrThrow(EService.class);

        interopJourney
                .withConsumer(consumer)
                .createPurposeFromTemplateAndLinkToEService(purposeTemplate, eService, state);
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
