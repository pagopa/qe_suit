package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServicePurposeTemplateSteps {
    private final InteropJourney journey;
    private final EntityStore entities;

    @Given("l'e-service {string} collegato a un template di finalità {purposeTemplateState}")
    public void linkPurposeTemplate(String alias, PurposeTemplateState state) {
        EService eService = entities.getByAliasOrThrow(alias, EService.class);
        journey.withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createPurposeTemplate(Tenant.PAGO_PA, state)
                .linkEServiceToPurposeTemplate(eService);
    }

    @ParameterType("pubblicato|in bozza")
    public PurposeTemplateState purposeTemplateState(String state) {
        return switch (state) {
            case "pubblicato" -> PurposeTemplateState.PUBLISHED;
            case "in bozza" -> PurposeTemplateState.DRAFT;
            default -> throw new IllegalArgumentException("Stato template di finalità non supportato: " + state);
        };
    }
}
