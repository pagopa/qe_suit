package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BffEServiceDescriptorMapperTest {

    private final BffEServiceDescriptorMapper mapper = new BffEServiceDescriptorMapper() {
        @Override
        public EServiceDescriptor toEServiceDescriptor(ProducerEServiceDescriptor source) {
            return EServiceDescriptor.builder()
                    .id(source.getId())
                    .version(source.getVersion())
                    .state(EServiceDescriptorState.DRAFT)
                    .build();
        }

        @Override
        public EService toDomainBase(ProducerEServiceDescriptor source) {
            return EService.builder()
                    .id(UUID.randomUUID())
                    .name("eservice")
                    .descriptors(List.of())
                    .build();
        }

        @Override
        public List<String> mapServerUrls(Collection<? extends ProducerEServiceDescriptorServerUrlsInner> serverUrls) {
            return List.of();
        }

        @Override
        public Attributes toAttributes(DescriptorAttributes source) {
            return null;
        }

        @Override
        public it.pagopa.interop.common.attribute.domain.Attribute toAttribute(DescriptorAttribute source) {
            return null;
        }
    };

    @Test
    void toEServiceWithUpsert_returns_mutable_descriptor_list() {
        ProducerEServiceDescriptor source = new ProducerEServiceDescriptor()
                .id(UUID.randomUUID())
                .version("1");

        EService mapped = mapper.toEServiceWithUpsert(source, null);

        assertDoesNotThrow(() -> mapped.upsertDescriptor(
                EServiceDescriptor.builder()
                        .id(UUID.randomUUID())
                        .version("2")
                        .state(EServiceDescriptorState.DRAFT)
                        .build()
        ));
        assertEquals(2, mapped.getDescriptors().size());
    }

    @Test
    void preservesOneBasedRequiredAttributeGroupsAcrossBffReadAndWriteMapping() {
        BffEServiceDescriptorMapper generatedMapper = Mappers.getMapper(BffEServiceDescriptorMapper.class);
        UUID firstGroupAttribute = UUID.randomUUID();
        UUID secondGroupAttribute = UUID.randomUUID();
        ProducerEServiceDescriptor source = new ProducerEServiceDescriptor()
                .id(UUID.randomUUID())
                .version("1")
                .attributes(new DescriptorAttributes()
                        .certified(List.of(
                                List.of(new DescriptorAttribute().id(firstGroupAttribute)
                                        .kind(AttributeKind.CERTIFIED)),
                                List.of(new DescriptorAttribute().id(secondGroupAttribute)
                                        .kind(AttributeKind.CERTIFIED))
                        )));

        Attributes attributes = generatedMapper.toAttributes(source.getAttributes());
        var command = new BffUpdateEServiceDescriptorCommand();
        command.attributes(attributes);

        assertEquals(List.of(1, 2), attributes.getCertified().stream()
                .map(it.pagopa.interop.common.attribute.domain.Attribute::getGroup).toList());
        assertEquals(List.of(firstGroupAttribute), command.getBffPayload().getAttributes()
                .getCertified().get(0).stream().map(value -> value.getId()).toList());
        assertEquals(List.of(secondGroupAttribute), command.getBffPayload().getAttributes()
                .getCertified().get(1).stream().map(value -> value.getId()).toList());
    }

    @Test
    void toAttributes_flattens_categories_and_preserves_group_indexes() {
        BffEServiceDescriptorMapper generatedMapper = Mappers.getMapper(BffEServiceDescriptorMapper.class);
        UUID firstCertifiedId = UUID.randomUUID();
        UUID secondCertifiedId = UUID.randomUUID();
        UUID declaredId = UUID.randomUUID();
        UUID verifiedId = UUID.randomUUID();
        DescriptorAttributes source = new DescriptorAttributes()
                .addCertifiedItem(List.of(descriptorAttribute(firstCertifiedId)))
                .addCertifiedItem(List.of(descriptorAttribute(secondCertifiedId)))
                .addDeclaredItem(List.of(descriptorAttribute(declaredId)))
                .addVerifiedItem(List.of(descriptorAttribute(verifiedId)));

        Attributes mapped = generatedMapper.toAttributes(source);

        assertThat(mapped.getCertified()).extracting("id")
                .containsExactly(firstCertifiedId, secondCertifiedId);
        assertThat(mapped.getCertified()).extracting("group")
                .containsExactly(0, 1);
        assertThat(mapped.getDeclared()).extracting("id").containsExactly(declaredId);
        assertThat(mapped.getDeclared().get(0).getGroup()).isZero();
        assertThat(mapped.getVerified()).extracting("id").containsExactly(verifiedId);
        assertThat(mapped.getVerified().get(0).getGroup()).isZero();
    }

    private static DescriptorAttribute descriptorAttribute(UUID id) {
        return new DescriptorAttribute()
                .id(id)
                .name("attribute")
                .description("attribute description")
                .explicitAttributeVerification(false)
                .kind(AttributeKind.CERTIFIED);
    }
}
