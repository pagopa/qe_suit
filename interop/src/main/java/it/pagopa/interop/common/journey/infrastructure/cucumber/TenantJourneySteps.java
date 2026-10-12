package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class TenantJourneySteps {

    private final InteropJourney interopJourney;

    @Given("assegna l'attributo certificato a {tenant}")
    public void assignCertifiedAttribute(Tenant consumer) {
        TenantRef tenantRef = TenantRef.of(consumer.getOrganizationId());
        interopJourney.assignCertifiedAttribute(tenantRef);
    }
}
