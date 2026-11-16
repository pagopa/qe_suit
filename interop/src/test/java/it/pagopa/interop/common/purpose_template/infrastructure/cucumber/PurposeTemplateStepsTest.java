package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.mockito.Mockito.*;

class PurposeTemplateStepsTest {

    @ParameterizedTest
    @CsvSource({
            "DRAFT, COMUNE_DI_MILANO",
            "PUBLISHED, COMUNE_DI_MILANO",
            "SUSPENDED, COMUNE_DI_MILANO",
            "DRAFT, PAGO_PA",
            "PUBLISHED, PAGO_PA",
            "SUSPENDED, PAGO_PA"
    })
    void createsATemplateInTheRequestedStateAsTheRequestedCreator(PurposeTemplateState state, Tenant creator) {
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var currentUserSession = mock(CurrentUserSession.class);
        var steps = new PurposeTemplateSteps(
                mock(PurposeTemplateUseCase.class), journey, currentUserSession, mock(EntityStore.class));

        steps.createPurposeTemplate(state, creator);

        var order = inOrder(journey);
        order.verify(journey).withProducer(creator, UserRole.ADMIN);
        order.verify(journey).createPurposeTemplate(creator, state);
        verifyNoMoreInteractions(journey);
        verifyNoInteractions(currentUserSession);
    }
}
