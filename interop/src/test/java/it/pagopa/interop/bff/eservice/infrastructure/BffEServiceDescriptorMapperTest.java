package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptorServerUrlsInner;
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

        assertDoesNotThrow(() -> mapped.addDescriptor(
                EServiceDescriptor.builder()
                        .id(UUID.randomUUID())
                        .version("2")
                        .state(EServiceDescriptorState.DRAFT)
                        .build()
        ));
        assertEquals(2, mapped.getDescriptors().size());
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
