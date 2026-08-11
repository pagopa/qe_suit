package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BffEServiceMapperTest {

    private final BffEServiceMapper mapper = Mappers.getMapper(BffEServiceMapper.class);

    @Test
    void preservingDescriptors_keeps_descriptor_list_mutable() {
        EServiceDescriptor existingDescriptor = EServiceDescriptor.builder()
                .id(UUID.randomUUID())
                .version("1")
                .state(EServiceDescriptorState.DRAFT)
                .build();

        EService existingEService = EService.builder()
                .id(UUID.randomUUID())
                .name("existing-eservice")
                .descriptors(List.of(existingDescriptor))
                .build();

        ProducerEServiceDetails source = new ProducerEServiceDetails()
                .id(existingEService.getId())
                .name("updated-eservice")
                .description("updated-description")
                .technology(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology.REST)
                .mode(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.DELIVER);

        EService mapped = mapper.toEServicePreservingDescriptors(source, existingEService);

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
