package it.pagopa.interop.bff.eservice.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BffUpdateEServiceDescriptorCommandTest {

    private Attribute buildAttribute(UUID id) {
        return Attribute.builder()
                .id(id)
                .code("code")
                .name("attribute-name")
                .description("attribute-description")
                .kind(AttributeKind.DECLARED)
                .build();
    }

    @Test
    void declaredAttribute_initializes_attributes_when_missing_and_adds_a_new_group() {
        BffUpdateEServiceDescriptorCommand command = new BffUpdateEServiceDescriptorCommand();
        UUID attributeId = UUID.randomUUID();

        command.declaredAttribute(buildAttribute(attributeId));

        DescriptorAttributesSeed attributes = command.getBffPayload().getAttributes();
        assertNotNull(attributes);
        assertTrue(attributes.getCertified().isEmpty());
        assertTrue(attributes.getVerified().isEmpty());
        assertEquals(1, attributes.getDeclared().size());

        List<DescriptorAttributeSeed> group = attributes.getDeclared().get(0);
        assertEquals(1, group.size());
        assertEquals(attributeId, group.get(0).getId());
        assertFalse(group.get(0).getExplicitAttributeVerification());
    }

    @Test
    void declaredAttribute_adds_a_separate_group_for_each_call_preserving_previous_ones() {
        BffUpdateEServiceDescriptorCommand command = new BffUpdateEServiceDescriptorCommand();
        UUID firstAttributeId = UUID.randomUUID();
        UUID secondAttributeId = UUID.randomUUID();

        command.declaredAttribute(buildAttribute(firstAttributeId));
        command.declaredAttribute(buildAttribute(secondAttributeId));

        List<List<DescriptorAttributeSeed>> declared = command.getBffPayload().getAttributes().getDeclared();

        assertEquals(2, declared.size());
        assertEquals(firstAttributeId, declared.get(0).get(0).getId());
        assertEquals(secondAttributeId, declared.get(1).get(0).getId());
    }

    @Test
    void declaredAttribute_reuses_existing_attributes_seed_when_already_present() {
        DescriptorAttributesSeed existingAttributes = new DescriptorAttributesSeed()
                .certified(new ArrayList<>())
                .declared(new ArrayList<>())
                .verified(new ArrayList<>());

        BffUpdateEServiceDescriptorCommand command = BffUpdateEServiceDescriptorCommand.from(
                new it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed()
                        .attributes(existingAttributes)
        );

        UUID attributeId = UUID.randomUUID();
        command.declaredAttribute(buildAttribute(attributeId));

        assertEquals(existingAttributes, command.getBffPayload().getAttributes());
        assertEquals(1, command.getBffPayload().getAttributes().getDeclared().size());
    }
}

