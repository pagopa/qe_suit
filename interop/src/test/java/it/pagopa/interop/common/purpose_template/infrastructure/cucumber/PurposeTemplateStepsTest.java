package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.mockito.Mockito.*;

class PurposeTemplateStepsTest {

    @ParameterizedTest
    @EnumSource(value = Tenant.class, names = {"COMUNE_DI_MILANO", "PAGO_PA"})
    void createsAPublishedTemplateForPublicAdministrationsAsTheRequestedCreator(Tenant creator) {
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var steps = new PurposeTemplateSteps(
                mock(PurposeTemplateUseCase.class), journey, mock(CurrentUserSession.class), mock(EntityStore.class));

        steps.createPurposeTemplate(creator);

        var order = inOrder(journey);
        order.verify(journey).withProducer(creator, UserRole.ADMIN);
        order.verify(journey).createPurposeTemplate(
                Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.PUBLISHED);
        verifyNoMoreInteractions(journey);
    }
}
