package it.pagopa.interop.m2m.eservice_template.infrastructure;

import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInstance;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class M2MEServiceTemplateMapperTest {
    @Test
    void refreshKeepsLocalAssociationsAndUpdatesRemoteMetadata() {
        var mapper = Mappers.getMapper(M2MEServiceTemplateMapper.class);
        var instance = new EServiceTemplateInstance("label", EServiceRef.of(UUID.randomUUID()));
        var existing = EServiceTemplate.builder().id(UUID.randomUUID()).name("previous").instance(instance).build();
        var source = new it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceTemplate()
                .id(existing.getId()).name("fresh");

        var refreshed = mapper.toTemplate(source, existing);

        assertThat(refreshed.getName()).isEqualTo("fresh");
        assertThat(refreshed.getInstances()).containsExactly(instance);
        assertThat(mapper.toTemplate(source, null).getInstances()).isEmpty();
    }
}
