package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class PurposeTemplateSteps {

    private final EntityStore entityStore;
    private final InteropJourney interopJourney;
    private final PurposeTemplateUseCase purposeTemplateUseCase;
    private final AuthSession authSession;
    private PurposeTemplate purposeTemplateReference;

    @Given("i dati del template finalità allo stato attuale appuntati come riferimento")
    public void savePurposeTemplateReference() {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplate = purposeTemplateUseCase.getPurposeTemplate(purposeTemplate);
        purposeTemplateReference = purposeTemplate.toBuilder().build();
    }

    @ParameterType("nome, descrizione e destinatari|il nome|la descrizione|i destinatari")
    public PurposeTemplateMetadataField purposeTemplateMetadataField(String field) {
        return switch (field) {
            case "il nome" -> PurposeTemplateMetadataField.NAME;
            case "la descrizione" -> PurposeTemplateMetadataField.DESCRIPTION;
            case "i destinatari" -> PurposeTemplateMetadataField.TARGET_DESCRIPTION;
            case "nome, descrizione e destinatari" -> PurposeTemplateMetadataField.ALL;
            default -> throw new IllegalArgumentException(
                    "Campo metadato template finalità non supportato: " + field);
        };
    }

    @Then("il template finalità conserva lo stato e i dati di riferimento")
    public void assertPurposeTemplatePreserved() {
        assertThat(purposeTemplateReference)
                .as("Riferimento del template finalità salvato prima della modifica")
                .isNotNull();
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplate = purposeTemplateUseCase.getPurposeTemplate(purposeTemplate);

        assertThat(purposeTemplate)
                .as("Stato e dati del template finalità")
                .usingRecursiveComparison()
                .ignoringFields("updatedAt")
                .isEqualTo(purposeTemplateReference);
    }

    @Then("il template finalità conserva lo stato e i dati di riferimento eccetto {purposeTemplateMetadataField}")
    public void assertPurposeTemplatePreservedExceptField(PurposeTemplateMetadataField excludedField) {
        assertThat(purposeTemplateReference)
                .as("Riferimento del template finalità salvato prima della modifica")
                .isNotNull();
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplate = purposeTemplateUseCase.getPurposeTemplate(purposeTemplate);

        if (excludedField.fieldNames.contains("title")) {
            assertThat(purposeTemplate.getTitle())
                    .as("Nome del template finalità dopo la modifica")
                    .isNotEqualTo(purposeTemplateReference.getTitle());
        }
        if (excludedField.fieldNames.contains("description")) {
            assertThat(purposeTemplate.getDescription())
                    .as("Descrizione del template finalità dopo la modifica")
                    .isNotEqualTo(purposeTemplateReference.getDescription());
        }
        if (excludedField.fieldNames.contains("targetDescription")) {
            assertThat(purposeTemplate.getTargetDescription())
                    .as("Destinatari del template finalità dopo la modifica")
                    .isNotEqualTo(purposeTemplateReference.getTargetDescription());
        }
        assertThat(purposeTemplate)
                .as("Stato e dati del template finalità eccetto " + excludedField.label)
                .usingRecursiveComparison()
                .ignoringFields("updatedAt")
                .ignoringFields(excludedField.fieldNames.toArray(String[]::new))
                .isEqualTo(purposeTemplateReference);
    }

    @Given("un template di finalità {purposeTemplateState} creato da {tenant}")
    public void createPurposeTemplate(PurposeTemplateState state, Tenant creator) {
        interopJourney.withProducer(creator, UserRole.ADMIN)
                .createPurposeTemplate(creator, state);
    }

    @When("{tenant} tenta di associare al proprio template di finalità l'e-service creato")
    public void tryLinkEServiceToPurposeTemplate(Tenant tenant) {
        authSession.set(UserRole.ADMIN, tenant);
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        EService eService = entityStore.getLastOrThrow(EService.class);
        purposeTemplateUseCase.tryLinkEService(purposeTemplate, eService);
    }

    @When("{tenant} tenta di associare al proprio template di finalità il template e-service creato")
    public void tryLinkEServiceTemplateToPurposeTemplate(Tenant tenant) {
        authSession.set(UserRole.ADMIN, tenant);
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        EServiceTemplate eServiceTemplate = entityStore.getLastOrThrow(EServiceTemplate.class);
        purposeTemplateUseCase.tryLinkEServiceTemplate(purposeTemplate, eServiceTemplate);
    }

    @Given("un template finalità creato da/dal {tenant} e pubblicato")
    public void createPurposeTemplate(Tenant creator) {
        createPurposeTemplate(creator, PurposeTemplateState.PUBLISHED);
    }

    @Given("un template finalità creato da/dal {tenant} in stato {purposeTemplateState}")
    public void createPurposeTemplate(Tenant creator, PurposeTemplateState state) {
        interopJourney.withProducer(creator, UserRole.ADMIN)
                .createPurposeTemplate(Tenant.fromTenantType(TenantKind.PA), state);
    }

    @When("un {userRole} di/del {tenant} modifica i metadati del template finalità:")
    public void updatePurposeTemplateMetadata(UserRole role, Tenant creator, DataTable dataTable) {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        MetadataUpdate metadata = readMetadataUpdate(purposeTemplate, dataTable);

        if (role == UserRole.ADMIN) {
            interopJourney.withConsumer(creator);
        } else {
            interopJourney.withConsumer(creator, role);
        }
        purposeTemplateUseCase.updateMetadata(
                purposeTemplate,
                metadata.name(),
                metadata.description(),
                metadata.targetDescription(),
                metadata.purposeIsFreeOfCharge(),
                metadata.purposeDailyCalls(),
                metadata.handlesPersonalData());
    }

    @When("un {userRole} di/del {tenant} tenta di modificare i metadati del template finalità:")
    public void tryToUpdatePurposeTemplateMetadata(UserRole role, Tenant creator, DataTable dataTable) {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        MetadataUpdate metadata = readMetadataUpdate(purposeTemplate, dataTable);

        if (role == UserRole.ADMIN) {
            interopJourney.withConsumer(creator);
        } else {
            interopJourney.withConsumer(creator, role);
        }
        purposeTemplateUseCase.tryToUpdateMetadata(
                purposeTemplate,
                metadata.name(),
                metadata.description(),
                metadata.targetDescription(),
                metadata.purposeIsFreeOfCharge(),
                metadata.purposeDailyCalls(),
                metadata.handlesPersonalData());
    }

    @Then("la richiesta di modifica dei metadati del template finalità fallisce come operazione malformata")
    public void shouldFailToUpdatePurposeTemplateMetadataAsMalformed() {
        purposeTemplateUseCase.shouldFailToUpdateMetadataAsMalformed();
    }

    @Then("la richiesta di modifica dei metadati del template finalità fallisce per conflitto di risorsa")
    public void shouldFailToUpdatePurposeTemplateMetadataAsConflict() {
        purposeTemplateUseCase.shouldFailToUpdateMetadataAsConflict();
    }

    @Then("la richiesta di modifica dei metadati del template finalità fallisce come operazione vietata")
    public void shouldFailToUpdatePurposeTemplateMetadataAsForbidden() {
        purposeTemplateUseCase.shouldFailToUpdateMetadataAsForbidden();
    }

    @Then("la richiesta di modifica dei metadati del template finalità fallisce per risorsa non trovata")
    public void shouldFailToUpdatePurposeTemplateMetadataAsNotFound() {
        purposeTemplateUseCase.shouldFailToUpdateMetadataAsNotFound();
    }

    private MetadataUpdate readMetadataUpdate(PurposeTemplate purposeTemplate, DataTable dataTable) {
        List<List<String>> rows = dataTable.cells();
        if (rows.size() < 2 || (!rows.get(0).equals(List.of("campo", "valore unico"))
                && !rows.get(0).equals(List.of("campo", "valore")))) {
            throw new IllegalArgumentException(
                    "La tabella dei metadati del template finalità deve avere le colonne 'campo' e 'valore' o 'valore unico' e almeno un campo"
            );
        }
        boolean uniqueValues = rows.get(0).equals(List.of("campo", "valore unico"));

        String name = purposeTemplate.getTitle();
        String description = purposeTemplate.getDescription();
        String targetDescription = purposeTemplate.getTargetDescription();
        Boolean purposeIsFreeOfCharge = null;
        Integer purposeDailyCalls = null;
        Boolean handlesPersonalData = null;
        Set<String> updatedFields = new HashSet<>();

        for (List<String> row : rows.subList(1, rows.size())) {
            if (row.size() != 2 || row.get(0) == null || row.get(1) == null) {
                throw new IllegalArgumentException(
                        "Ogni riga dei metadati del template finalità deve contenere campo e valore");
            }

            String field = row.get(0);
            if (!updatedFields.add(field)) {
                throw new IllegalArgumentException("Campo metadato template finalità duplicato: " + field);
            }

            String value = row.get(1);
            if (uniqueValues && Set.of("purposeTitle", "purposeDescription", "targetDescription").contains(field)) {
                value = withUniqueSuffix(value);
            }
            switch (field) {
                case "purposeTitle" -> name = value;
                case "purposeDescription" -> description = value;
                case "targetDescription" -> targetDescription = value;
                case "purposeIsFreeOfCharge" -> purposeIsFreeOfCharge = parseBoolean(field, value);
                case "purposeDailyCalls" -> purposeDailyCalls = parseInteger(field, value);
                case "handlesPersonalData" -> handlesPersonalData = parseBoolean(field, value);
                default -> throw new IllegalArgumentException(
                        "Campo metadato template finalità non supportato: " + field);
            }
        }

        return new MetadataUpdate(
                name,
                description,
                targetDescription,
                purposeIsFreeOfCharge,
                purposeDailyCalls,
                handlesPersonalData);
    }

    private Boolean parseBoolean(String field, String value) {
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
            throw new IllegalArgumentException("Valore booleano non valido per " + field + ": " + value);
        }
        return Boolean.valueOf(value);
    }

    private Integer parseInteger(String field, String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Valore numerico non valido per " + field + ": " + value, exception);
        }
    }

    private String withUniqueSuffix(String value) {
        return value + " " + RandomUtils.randomAlphanumeric(10);
    }

    private record MetadataUpdate(
            String name,
            String description,
            String targetDescription,
            Boolean purposeIsFreeOfCharge,
            Integer purposeDailyCalls,
            Boolean handlesPersonalData) {}

    public enum PurposeTemplateMetadataField {
        NAME(Set.of("title"), "il nome"),
        DESCRIPTION(Set.of("description"), "la descrizione"),
        TARGET_DESCRIPTION(Set.of("targetDescription"), "i destinatari"),
        ALL(Set.of("title", "description", "targetDescription"), "nome, descrizione e destinatari");

        private final Set<String> fieldNames;
        private final String label;

        PurposeTemplateMetadataField(Set<String> fieldNames, String label) {
            this.fieldNames = fieldNames;
            this.label = label;
        }
    }

    @Then("l'associazione dell'e-service al template di finalità è negata")
    public void assertEServiceLinkDenied() {
        purposeTemplateUseCase.shouldFailLinkToReceiveEService();
    }

    @Then("l'associazione del template e-service al template di finalità è negata")
    public void assertEServiceTemplateLinkDenied() {
        purposeTemplateUseCase.shouldFailLinkToReceiveEService();
    }
}
