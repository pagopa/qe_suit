package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.application.AttributeUseCase;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttributeSteps {
    private final AttributeUseCase attributeUseCase;
    private final TenantUseCase tenantUseCase;
    private final EntityStore entityStore;

    @Given("l'attributo certificato {string} revocato a {tenant}")
    public void revokeCertifiedAttribute(String alias, Tenant tenant) {
        Attribute attribute = entityStore.getByAliasOrThrow(alias, Attribute.class);
        tenantUseCase.revokeCertifiedAttribute(TenantRef.of(tenant.getOrganizationId()), attribute.getRef());
    }

    @When("l'utente rimuove la soglia personalizzata dall'attributo certificato {int} del gruppo {int} dall'{currentEService}")
    public void removeCertifiedAttributeThreshold(int attributeIndex, int groupIndex, EService eService) {
        attributeUseCase.removeCertifiedAttributeThreshold(eService, groupIndex, attributeIndex);
    }

    @Then("viene confermato l'esito di successo per la rimozione della soglia personalizzata")
    public void certifiedAttributeThresholdRemoved() {
        attributeUseCase.certifiedAttributeThresholdRemoved();
    }
}
