package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.journey.application.InteropJourney;
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
