package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class PurposeTemplateSteps {

    private final EntityStore entityStore;
    private final InteropJourney journey;
    private final PurposeTemplateUseCase purposeTemplateUseCase;
    private PurposeTemplate purposeTemplateReference;

    @Given("i dati del template finalità allo stato attuale appuntati come riferimento")
    public void savePurposeTemplateReference() {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplate = purposeTemplateUseCase.getPurposeTemplate(purposeTemplate);
        purposeTemplateReference = purposeTemplate.toBuilder().build();
    }

    @Given("un template finalità creato da/dal {tenant} e pubblicato")
    public void createPurposeTemplate(Tenant creator) {
        createPurposeTemplate(creator, PurposeTemplateState.PUBLISHED);
    }

    @Given("un template finalità creato da/dal {tenant} in stato {purposeTemplateState}")
    public void createPurposeTemplate(Tenant creator, PurposeTemplateState state) {
        journey.withProducer(creator, UserRole.ADMIN)
                .createPurposeTemplate(Tenant.fromTenantType(TenantKind.PA), state);
    }

    @When("un {userRole} di/del {tenant} modifica i metadati del template finalità:")
    public void updatePurposeTemplateMetadata(UserRole role, Tenant creator, DataTable dataTable) {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        MetadataUpdate metadata = readMetadataUpdate(purposeTemplate, dataTable);

        journey.withProducer(creator, role);
        purposeTemplateUseCase.updateMetadata(
                purposeTemplate, metadata.name(), metadata.description(), metadata.targetDescription());
    }

    private MetadataUpdate readMetadataUpdate(PurposeTemplate purposeTemplate, DataTable dataTable) {
        List<List<String>> rows = dataTable.cells();
        if (rows.size() < 2 || !rows.get(0).equals(List.of("campo", "valore"))) {
            throw new IllegalArgumentException(
                    "La tabella dei metadati del template finalità deve avere le colonne 'campo' e 'valore' e almeno un campo"
            );
        }

        String name = purposeTemplate.getTitle();
        String description = purposeTemplate.getDescription();
        String targetDescription = purposeTemplate.getTargetDescription();
        Set<String> updatedFields = new HashSet<>();

        for (List<String> row : rows.subList(1, rows.size())) {
            if (row.size() != 2 || row.get(0) == null) {
                throw new IllegalArgumentException(
                        "Ogni riga dei metadati del template finalità deve contenere campo e valore");
            }

            String field = row.get(0);
            if (!updatedFields.add(field)) {
                throw new IllegalArgumentException("Campo metadato template finalità duplicato: " + field);
            }

            switch (field) {
                case "nome" -> name = row.get(1);
                case "descrizione" -> description = row.get(1);
                case "destinatari" -> targetDescription = row.get(1);
                default -> throw new IllegalArgumentException(
                        "Campo metadato template finalità non supportato: " + field);
            }
        }

        return new MetadataUpdate(name, description, targetDescription);
    }

    private record MetadataUpdate(String name, String description, String targetDescription) {}
}
