package it.pagopa.interop.common.tenant.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.tenant.infrastructure.cucumber.dto.ProducerDelegationRecipient;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantDelegationSettingsStepsTest {
    static final String PREREQUISITE = "i seguenti enti abilitati a ricevere deleghe di erogazione:";
    private static final ValidatorFactory VALIDATORS = Validation.buildDefaultValidatorFactory();
    private final DataTableMapper transformer = new DataTableMapper(VALIDATORS.getValidator());
    private final InteropJourney journey = mock(InteropJourney.class);
    private final TenantDelegationSettingsSteps steps = new TenantDelegationSettingsSteps(journey);

    @AfterEach
    void clearKeys() {
        DataTableMapper.clearGherkinKeys();
    }

    @AfterAll
    static void closeValidators() {
        VALIDATORS.close();
    }

    @Test
    void bindsAndConvertsTheApprovedTableBeforeDelegating() throws Exception {
        var method = TenantDelegationSettingsSteps.class.getMethod("enableProducerDelegationRecipients", List.class);
        assertThat(method.getAnnotation(Given.class).value()).isEqualTo(PREREQUISITE);
        steps.enableProducerDelegationRecipients(List.of(
                transform(Map.of("ente", "Comune di Pozzallo")), transform(Map.of("ente", "Sogecap"))));
        var order = inOrder(journey);
        order.verify(journey).ensureProducerDelegationEnabled(Tenant.COMUNE_DI_POZZALLO);
        order.verify(journey).ensureProducerDelegationEnabled(Tenant.SOGECAP);
    }

    @Test
    void rejectsMissingEmptyUnknownOrUnsupportedInput() {
        assertThatThrownBy(() -> transform(Map.of())).isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> transform(Map.of("ente", ""))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> transform(Map.of("ente", "Unknown"))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> transform(Map.of("ente", "Sogecap", "extra", "true")))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(journey);
    }

    @Test
    void rejectsEmptyDuplicateAndNullTenantsBeforeAnySetup() {
        assertThatThrownBy(() -> steps.enableProducerDelegationRecipients(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        var recipient = transform(Map.of("ente", "Sogecap"));
        assertThatThrownBy(() -> steps.enableProducerDelegationRecipients(List.of(
                recipient, transform(Map.of("ente", "sogecap"))))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> steps.enableProducerDelegationRecipients(List.of(
                recipient, new ProducerDelegationRecipient()))).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(journey);
    }

    private ProducerDelegationRecipient transform(Map<String, String> row) {
        return (ProducerDelegationRecipient) transformer.transform(row, ProducerDelegationRecipient.class);
    }
}
