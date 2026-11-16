package it.pagopa.interop.common.tenant.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.tenant.infrastructure.cucumber.dto.ProducerDelegationRecipient;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TenantDelegationSettingsSteps {
    private final InteropJourney journey;

    @Given("i seguenti enti abilitati a ricevere deleghe di erogazione:")
    public void enableProducerDelegationRecipients(List<ProducerDelegationRecipient> recipients) {
        var tenants = recipients.stream().map(ProducerDelegationRecipient::getTenant).toList();
        if (tenants.isEmpty() || tenants.contains(null) || tenants.stream().distinct().count() != tenants.size()) {
            throw new IllegalArgumentException("Specificare almeno un ente, senza duplicati, per ricevere deleghe");
        }
        tenants.forEach(tenant ->
                journey
                        .withProducer(tenant, User.getTenantAdmin(tenant))
                        .ensureProducerDelegationEnabled(tenant)
        );
    }
}
