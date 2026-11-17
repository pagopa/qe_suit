package it.pagopa.interop.common.purpose.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
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
    private final CurrentUserSession currentUserSession;
    private final CurrentM2MSession currentM2MSession;
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

        interopJourney.withConsumer(consumer, role);
        purposeUseCase.updateMetadata(purpose, metadata.name(), metadata.description());
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
        Set<String> updatedFields = new HashSet<>();

        for (List<String> row : rows.subList(1, rows.size())) {
            if (row.size() != 2 || row.get(0) == null) {
                throw new IllegalArgumentException("Ogni riga dei metadati della finalita deve contenere campo e valore");
            }

            String field = row.get(0);
            if (!updatedFields.add(field)) {
                throw new IllegalArgumentException("Campo metadato finalita duplicato: " + field);
            }

            switch (field) {
                case "nome" -> name = row.get(1);
                case "descrizione" -> description = row.get(1);
                default -> throw new IllegalArgumentException("Campo metadato finalita non supportato: " + field);
            }
        }

        return new MetadataUpdate(name, description);
    }

    private record MetadataUpdate(String name, String description) {}

    @Given("l'e-service con una finalità in stato {purposeState} associata da/dal {tenant}")
    public void createPurposeForEService(PurposeVersionState purposeState, Tenant consumer) {
        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .createPurpose(purposeState);
    }

    @Given("una finalità in stato {purposeState} associata da/dal {tenant}")
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
                .withConsumer(consumer, UserRole.ADMIN)
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
        // TODO fare riferimento al ticket tecnico QA-19317
        // Serve una soluzione per evitare di impostare sia currentUserSession che currentM2MSession
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        currentM2MSession.set(consumer, M2MRole.M2M_ADMIN);
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
        // TODO fare riferimento al ticket tecnico QA-19317
        // Serve una soluzione per evitare di impostare sia currentUserSession che currentM2MSession
        currentUserSession.set(User.getTenantAdmin(tenant), tenant);
        currentM2MSession.set(tenant, M2MRole.M2M_ADMIN);

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
}
