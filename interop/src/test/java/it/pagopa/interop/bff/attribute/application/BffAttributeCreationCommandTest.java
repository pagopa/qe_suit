package it.pagopa.interop.bff.attribute.application;

import it.pagopa.interop.common.attribute.application.command.AttributeCreationCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class BffAttributeCreationCommandTest {

    @Test
    void name_and_description_are_applied_to_bff_payload() {
        BffAttributeCreationCommand command = new BffAttributeCreationCommand();
        AttributeCreationCommand result = command
                .name("declared-attribute")
                .description("a description");

        assertInstanceOf(BffAttributeCreationCommand.class, result);
        assertEquals("declared-attribute", command.getBffPayload().getName());
        assertEquals("a description", command.getBffPayload().getDescription());
    }

    @Test
    void from_wraps_existing_payload_without_overriding_values() {
        AttributeSeed payload = new AttributeSeed()
                .name("existing-name")
                .description("existing-description");

        BffAttributeCreationCommand command = BffAttributeCreationCommand.from(payload);

        assertEquals("existing-name", command.getBffPayload().getName());
        assertEquals("existing-description", command.getBffPayload().getDescription());
    }
}

