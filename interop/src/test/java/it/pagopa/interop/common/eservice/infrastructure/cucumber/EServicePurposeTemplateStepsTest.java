package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EServicePurposeTemplateStepsTest {
    private final InteropJourney journey = mock(InteropJourney.class, RETURNS_SELF);
    private final InMemoryEntityStore entities = new InMemoryEntityStore();
    private final EServicePurposeTemplateSteps steps = new EServicePurposeTemplateSteps(journey, entities);

    @Test
    void linksPurposeTemplateInRequestedStateToAliasedEServiceAsRequester() {
        EService eService = EService.builder().id(UUID.randomUUID()).build();
        entities.upsert("ES-1", eService);

        steps.linkPurposeTemplate("ES-1", PurposeTemplateState.PUBLISHED);

        var order = inOrder(journey);
        order.verify(journey).withConsumer(Tenant.PAGO_PA, UserRole.ADMIN);
        order.verify(journey).createPurposeTemplate(Tenant.PAGO_PA, PurposeTemplateState.PUBLISHED);
        order.verify(journey).linkEServiceToPurposeTemplate(eService);
    }

    @Test
    void resolvesBothSupportedPurposeTemplateStates() {
        assertThat(steps.purposeTemplateState("pubblicato")).isEqualTo(PurposeTemplateState.PUBLISHED);
        assertThat(steps.purposeTemplateState("in bozza")).isEqualTo(PurposeTemplateState.DRAFT);
        assertThatThrownBy(() -> steps.purposeTemplateState("archiviato"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsUnknownEServiceAliasBeforeStartingTheJourney() {
        assertThatThrownBy(() -> steps.linkPurposeTemplate("missing", PurposeTemplateState.DRAFT))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("missing");
        verifyNoInteractions(journey);
    }
}
