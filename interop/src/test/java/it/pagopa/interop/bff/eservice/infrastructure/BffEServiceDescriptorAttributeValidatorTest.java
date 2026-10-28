package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.Attributes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BffEServiceDescriptorAttributeValidatorTest {
    @Test
    void rejectsStaleAttributesAndAcceptsAlternativesInAnyOrderWithinTheSameGroup() {
        var first = Attribute.builder().id(UUID.randomUUID()).kind(AttributeKind.CERTIFIED).group(1).build();
        var second = Attribute.builder().id(UUID.randomUUID()).kind(AttributeKind.CERTIFIED).group(1).build();
        var expected = Attributes.builder().certified(List.of(first, second)).build();
        var command = new BffUpdateEServiceDescriptorCommand();
        command.attributes(expected);
        var seed = command.getBffPayload().getAttributes();

        assertThat(BffEServiceDescriptorAttributeValidator.matches(seed, Attributes.builder().build())).isFalse();
        assertThat(BffEServiceDescriptorAttributeValidator.matches(seed, null)).isFalse();
        assertThat(BffEServiceDescriptorAttributeValidator.matches(seed,
                Attributes.builder().certified(List.of(second, first)).build())).isTrue();
        assertThat(BffEServiceDescriptorAttributeValidator.matches(seed,
                Attributes.builder().certified(List.of(first, second.toBuilder().group(2).build())).build())).isFalse();
    }
}
