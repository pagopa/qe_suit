package it.pagopa.interop.bff.eservice.application;

import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateVersionMapper;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeCertifiedDiscreteComparator;
import it.pagopa.interop.common.attribute.domain.AttributeCertifiedDiscreteConfig;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.Attributes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceDescriptorQuotas;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BffUpdateEServiceDescriptorCommandAttributeTest {

    @Test
    void defaultGroupIsTheFirstRequirementAndEmptyCategoriesRemainEmpty() {
        var attribute = Attribute.builder().id(UUID.randomUUID()).kind(AttributeKind.CERTIFIED).build();
        assertThat(attribute.getGroup()).isEqualTo(1);
        var command = new BffUpdateEServiceDescriptorCommand();
        command.attributes(Attributes.builder().certified(attribute).build());
        var payload = command.getBffPayload().getAttributes();
        assertThat(payload.getCertified()).hasSize(1);
        assertThat(payload.getCertified().get(0)).extracting(value -> value.getId()).containsExactly(attribute.getId());
        assertThat(payload.getDeclared()).isEmpty();
        assertThat(payload.getVerified()).isEmpty();
    }

    @Test
    void templateReadAndBothTemplateWritePathsPreserveOneBasedGroups() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        var source = new DescriptorAttributes().certified(List.of(
                List.of(new DescriptorAttribute().id(first)
                        .kind(it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind.CERTIFIED)),
                List.of(new DescriptorAttribute().id(second)
                        .kind(it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind.CERTIFIED))));
        var attributes = Mappers.getMapper(BffEServiceTemplateVersionMapper.class).toAttributes(source);
        assertThat(attributes.getCertified()).extracting(Attribute::getGroup).containsExactly(1, 2);

        var instance = BffEServiceTemplateInstanceUpdateDescriptorCommand.from(
                new UpdateEServiceTemplateInstanceDescriptorQuotas());
        instance.attributes(attributes);
        var instanceSeed = instance.getBffUpdatePublishedEServiceInstancePayload().getAttributes();
        assertThat(instanceSeed.getCertified()).hasSize(2);
        assertThat(instanceSeed.getCertified().get(0)).extracting(value -> value.getId()).containsExactly(first);
        assertThat(instanceSeed.getCertified().get(1)).extracting(value -> value.getId()).containsExactly(second);
        assertThat(instanceSeed.getDeclared()).isEmpty();

        var template = new BffUpdateEServiceTemplateVersionCommand();
        template.attributes(attributes);
        var templateSeed = template.getBffPayload().getAttributes();
        assertThat(templateSeed.getCertified()).hasSize(2);
        assertThat(templateSeed.getCertified().get(0)).extracting(value -> value.getId()).containsExactly(first);
        assertThat(templateSeed.getCertified().get(1)).extracting(value -> value.getId()).containsExactly(second);
        assertThat(templateSeed.getDeclared()).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void invalidGroupsFailExplicitlyOnEveryWritePath(Integer group) {
        var attributes = Attributes.builder().certified(Attribute.builder().id(UUID.randomUUID())
                .kind(AttributeKind.CERTIFIED).group(group).build()).build();
        var descriptor = new BffUpdateEServiceDescriptorCommand();
        var instance = BffEServiceTemplateInstanceUpdateDescriptorCommand.from(
                new UpdateEServiceTemplateInstanceDescriptorQuotas());
        var template = new BffUpdateEServiceTemplateVersionCommand();
        assertThatThrownBy(() -> descriptor.attributes(attributes))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("start at 1");
        assertThatThrownBy(() -> instance.attributes(attributes))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("start at 1");
        assertThatThrownBy(() -> template.attributes(attributes))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("start at 1");
    }

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
