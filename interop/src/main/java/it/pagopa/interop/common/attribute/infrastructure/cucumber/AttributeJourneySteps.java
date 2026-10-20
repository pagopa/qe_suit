package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class AttributeJourneySteps {
    private final InteropJourney interopJourney;

    @Given("un {userRole} di/del {tenant} crea un attributo certificato")
    public void createCertifiedAttribute(UserRole userRole, Tenant producer) {
        interopJourney
                .withProducer(producer, userRole)
                .createCertifiedAttribute();
    }

    @Given("un {userRole} di/del {tenant} assegna l'attributo certificato a {tenant}")
    public void assignAttribute(UserRole userRole, Tenant producer, Tenant consumer) {
        interopJourney
                .withProducer(producer, userRole)
                .assignCertifiedAttribute(TenantRef.of(consumer.getOrganizationId()));
    }
}
