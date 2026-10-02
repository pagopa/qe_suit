package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.utils.RandomUtils;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
                .withProducer(producer, userRole);
                //.assignAttribute();
    }
}
