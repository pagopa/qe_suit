package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.application.AttributeUseCase;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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

    @When("l'erogatore tenta di modificare la scadenza dell'attributo verificato {string} di {tenant} impostandola a ieri")
    public void attemptVerifiedAttributeExpirationUpdate(String alias, Tenant tenant) {
        Attribute attribute = entityStore.getByAliasOrThrow(alias, Attribute.class);
        tenantUseCase.attemptVerifiedAttributeExpirationUpdate(
                TenantRef.of(tenant.getOrganizationId()), attribute.getRef(),
                OffsetDateTime.now(ZoneOffset.UTC).minusDays(1)
        );
    }

    @When("l'erogatore tenta di creare un attributo \"{attributeKind}\" con il nome dell'{attributeByAlias}")
    public void attemptAttributeCreationWithDuplicateName(AttributeKind kind, Attribute existing) {
        if (existing.getKind() != kind) {
            throw new IllegalArgumentException("The existing attribute must have kind " + kind);
        }
        attributeUseCase.attemptAttributeCreation(kind, existing.getName());
    }

    @When("l'erogatore tenta di creare un attributo \"{attributeKind}\" con un link all'interno del nome")
    public void attemptAttributeCreationWithHyperlink(AttributeKind kind) {
        attributeUseCase.attemptAttributeCreation(kind, "Attributo https://example.org");
    }

    @When("il certificatore tenta di assegnare nuovamente l'{attributeByAlias} all'ente {tenant}")
    public void attemptCertifiedAttributeAssignment(Attribute attribute, Tenant tenant) {
        tenantUseCase.attemptCertifiedAttributeAssignment(TenantRef.of(tenant.getOrganizationId()), attribute);
    }
}
