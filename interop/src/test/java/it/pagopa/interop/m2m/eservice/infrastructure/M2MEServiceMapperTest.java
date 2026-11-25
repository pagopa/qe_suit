package it.pagopa.interop.m2m.eservice.infrastructure;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class M2MEServiceMapperTest {
    @Test
    void refreshUpdatesMetadataAndKeepsExistingDescriptors() {
        var mapper = Mappers.getMapper(M2MEServiceMapper.class);
        var descriptor = EServiceDescriptor.builder().id(UUID.randomUUID()).version("1").build();
        var existing = EService.builder().id(UUID.randomUUID()).descriptors(List.of(descriptor)).build();
        var source = new it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EService()
                .id(existing.getId()).producerId(UUID.randomUUID()).name("fresh");

        var refreshed = mapper.toEServicePreservingDescriptors(source, existing);
        assertThat(refreshed.getName()).isEqualTo("fresh");
        assertThat(refreshed.getDescriptors()).containsExactly(descriptor);
        assertThat(mapper.toEServicePreservingDescriptors(source, null).getDescriptors()).isEmpty();
    }
}
