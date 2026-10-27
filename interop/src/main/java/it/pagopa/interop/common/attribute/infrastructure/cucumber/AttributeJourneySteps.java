package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.infrastructure.cucumber.dto.AttributeAliasConfiguration;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class AttributeJourneySteps {
    private final InteropJourney interopJourney;
    private final EntityStore entityStore;
    private final CertifiedAttributeFixtures fixtures;

    @Given("un {userRole} di/del {tenant} crea un attributo certificato")
    public void createCertifiedAttribute(UserRole userRole, Tenant producer) {
        interopJourney
                .withProducer(producer, userRole)
                .createCertifiedAttribute();
    }

    public void createCertifiedAttributes(
            UserRole userRole, Tenant producer, List<AttributeAliasConfiguration> attributes
    ) {
        validateAliases(attributes, true);
        for (AttributeAliasConfiguration configuration : attributes) {
            String alias = configuration.getAlias();
            Attribute attribute = interopJourney.withProducer(producer, userRole)
                    .createCertifiedAttribute("attribute-" + alias + "-" + UUID.randomUUID() + "-CERTIFIED");
            fixtures.registerCreated(attribute, TenantRef.of(producer.getOrganizationId()));
            entityStore.upsert(alias, attribute);
        }
    }

    @Given("un {userRole} di/del {tenant} assegna l'attributo certificato a {tenant}")
    public void assignAttribute(UserRole userRole, Tenant producer, Tenant consumer) {
        interopJourney
                .withProducer(producer, userRole)
                .assignCertifiedAttribute(TenantRef.of(consumer.getOrganizationId()));
    }

    @Given("i seguenti attributi {word} creati da un {userRole} di/del {tenant}:")
    public void createAttriburtes(String type,  UserRole userRole, Tenant producer, List<AttributeAliasConfiguration> attributes){
        createAttributes(userRole, producer, type, attributes);
    }

    @When("un {userRole} di/del {tenant} crea i seguenti attributi {word}:")
    public void createAttributes(
            UserRole userRole, Tenant producer, String type, List<AttributeAliasConfiguration> attributes
    ) {
        if ("certificati".equals(type)) {
            createCertifiedAttributes(userRole, producer, attributes);
            return;
        }
        AttributeKind kind = switch (type) {
            case "dichiarati" -> AttributeKind.DECLARED;
            case "verificati" -> AttributeKind.VERIFIED;
            case "discreti" -> AttributeKind.CERTIFIED_DISCRETE;
            default -> throw new IllegalArgumentException("Unsupported attribute fixture type: " + type);
        };
        validateAliases(attributes, true);
        for (AttributeAliasConfiguration configuration : attributes) {
            String alias = configuration.getAlias();
            Attribute attribute = interopJourney.withProducer(producer, userRole)
                    .createAttribute(kind, "attribute-" + alias + "-" + UUID.randomUUID() + "-" + kind);
            entityStore.upsert(alias, attribute);
        }
    }

    @When("un {userRole} di/del {tenant} assegna a {tenant} i seguenti attributi certificati:")
    @Given("i seguenti attributi certificati assegnati da un {userRole} di/del {tenant} a {tenant}:")
    public void assignCertifiedAttributes(
            UserRole userRole, Tenant producer, Tenant consumer, List<AttributeAliasConfiguration> attributes
    ) {
        validateAliases(attributes, false);
        TenantRef consumerRef = TenantRef.of(consumer.getOrganizationId());
        TenantRef issuerRef = TenantRef.of(producer.getOrganizationId());
        for (AttributeAliasConfiguration configuration : attributes) {
            Attribute attribute = entityStore.getByAliasOrThrow(configuration.getAlias(), Attribute.class);
            fixtures.requireCreated(issuerRef, attribute.getRef());
            interopJourney.withProducer(producer, userRole)
                    .assignCertifiedAttribute(consumerRef, attribute.getRef());
            fixtures.registerAssignment(issuerRef, consumerRef, attribute.getRef());
        }
    }

    private void validateAliases(List<AttributeAliasConfiguration> attributes, boolean requireUnused) {
        if (attributes == null || attributes.isEmpty()) {
            throw new IllegalArgumentException("At least one attribute alias is required");
        }
        Set<String> aliases = new HashSet<>();
        for (AttributeAliasConfiguration attribute : attributes) {
            if (attribute == null || attribute.getAlias() == null || !aliases.add(attribute.getAlias())) {
                throw new IllegalArgumentException("Attribute aliases must be present and unique");
            }
            if (requireUnused && entityStore.getByAlias(attribute.getAlias(), Attribute.class).isPresent()) {
                throw new IllegalArgumentException("Attribute alias is already registered: "
                        + attribute.getAlias());
            }
        }
    }
}
