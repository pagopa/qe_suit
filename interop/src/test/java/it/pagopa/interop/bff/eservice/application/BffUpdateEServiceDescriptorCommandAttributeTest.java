package it.pagopa.interop.bff.eservice.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeCertifiedDiscreteComparator;
import it.pagopa.interop.common.attribute.domain.AttributeCertifiedDiscreteConfig;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.Attributes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BffUpdateEServiceDescriptorCommandAttributeTest {

    @Test
    void discreteSelectionWithoutComparatorParametersLeavesOptionalConfigurationUnset() {
        UUID discrete = UUID.randomUUID();
        var command = new BffUpdateEServiceDescriptorCommand();
        command.attributes(Attributes.builder()
                .certified(attribute(discrete, AttributeKind.CERTIFIED_DISCRETE, 1, null))
                .build());

        var seed = command.getBffPayload().getAttributes().getCertified().get(0).get(0);
        assertThat(seed.getId()).isEqualTo(discrete);
        assertThat(seed.getDiscreteConfig()).isNull();
    }

    @Test
    void mapsFourSupportedKindsToTheirBffCollectionsAndPreservesGroups() {
        UUID certified = UUID.randomUUID();
        UUID discrete = UUID.randomUUID();
        UUID declared = UUID.randomUUID();
        UUID verified = UUID.randomUUID();
        Attributes attributes = Attributes.builder()
                .certified(attribute(certified, AttributeKind.CERTIFIED, 1, null))
                .certified(attribute(discrete, AttributeKind.CERTIFIED_DISCRETE, 1,
                        AttributeCertifiedDiscreteConfig.builder()
                                .threshold(3).comparator(AttributeCertifiedDiscreteComparator.GTE).build()))
                .declared(attribute(declared, AttributeKind.DECLARED, 2, null))
                .verified(attribute(verified, AttributeKind.VERIFIED, 1, null))
                .build();

        var command = new BffUpdateEServiceDescriptorCommand();
        command.attributes(attributes);

        var payload = command.getBffPayload().getAttributes();
        assertThat(payload.getCertified()).hasSize(1);
        assertThat(payload.getCertified().get(0).stream().map(value -> value.getId()).toList())
                .containsExactly(certified, discrete);
        assertThat(payload.getCertified().get(0).get(1).getExplicitAttributeVerification()).isFalse();
        assertThat(payload.getCertified().get(0).get(1).getDiscreteConfig().getThreshold()).isEqualTo(3);
        assertThat(payload.getCertified().get(0).get(1).getDiscreteConfig().getComparator().getValue())
                .isEqualTo("GTE");
        assertThat(payload.getDeclared()).hasSize(2);
        assertThat(payload.getDeclared().get(1).get(0).getId()).isEqualTo(declared);
        assertThat(payload.getVerified()).hasSize(1);
        assertThat(payload.getVerified().get(0).get(0).getId()).isEqualTo(verified);
        assertThat(payload.getVerified().get(0).get(0).getExplicitAttributeVerification()).isTrue();
    }

    private static Attribute attribute(
            UUID id, AttributeKind kind, int group, AttributeCertifiedDiscreteConfig discreteConfig
    ) {
        return Attribute.builder().id(id).kind(kind).group(group).discreteConfig(discreteConfig).build();
    }
}
