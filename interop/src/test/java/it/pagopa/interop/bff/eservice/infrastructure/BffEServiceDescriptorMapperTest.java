package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptorServerUrlsInner;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
}
