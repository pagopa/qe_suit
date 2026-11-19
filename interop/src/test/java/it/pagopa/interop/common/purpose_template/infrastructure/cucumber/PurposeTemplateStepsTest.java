package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.mockito.Mockito.*;

class PurposeTemplateStepsTest {

    @ParameterizedTest
    @EnumSource(value = Tenant.class, names = {"COMUNE_DI_MILANO", "PAGO_PA"})
    void createsAPublishedTemplateForPublicAdministrationsAsTheRequestedCreator(Tenant creator) {
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var steps = new PurposeTemplateSteps(journey, mock(PurposeTemplateUseCase.class));

        steps.createPurposeTemplate(creator);

        var order = inOrder(journey);
        order.verify(journey).withProducer(creator);
        order.verify(journey).createPurposeTemplate(
                Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.PUBLISHED);
        verifyNoMoreInteractions(journey);
    }

    @Test
    void delegatesReactivationWithoutChangingTheActorOrChannel() {
        var journey = mock(InteropJourney.class);
        var useCase = mock(PurposeTemplateUseCase.class);
        var template = PurposeTemplate.builder().build();

        new PurposeTemplateSteps(journey, useCase).attemptPurposeTemplateReactivation(template);

        verify(useCase).attemptPurposeTemplateReactivation(template);
        verifyNoMoreInteractions(useCase);
        verifyNoInteractions(journey);
    }

    @Test
    void delegatesPublicationWithoutChangingTheActorOrChannel() {
        var journey = mock(InteropJourney.class);
        var useCase = mock(PurposeTemplateUseCase.class);
        var template = PurposeTemplate.builder().build();

        new PurposeTemplateSteps(journey, useCase).attemptPurposeTemplatePublication(template);

        verify(useCase).attemptPurposeTemplatePublication(template);
        verifyNoMoreInteractions(useCase);
        verifyNoInteractions(journey);
    }
}
