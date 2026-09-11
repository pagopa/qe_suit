package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BffAttributeMapperTest {

    private final BffAttributeMapper mapper = Mappers.getMapper(BffAttributeMapper.class);

    @Test
    void toDomain_maps_all_fields_except_group() {
        UUID id = UUID.randomUUID();

        it.pagopa.interop.generated.openapi.clients.bff.model.Attribute source =
                new it.pagopa.interop.generated.openapi.clients.bff.model.Attribute()
                        .id(id)
                        .code("code-123")
                        .kind(it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind.DECLARED)
                        .description("attribute description")
                        .origin("IPA")
                        .name("attribute-name")
                        .creationTime("2026-01-01T00:00:00Z");

        Attribute mapped = mapper.toDomain(source);

        assertEquals(id, mapped.getId());
        assertEquals("code-123", mapped.getCode());
        assertEquals(AttributeKind.DECLARED, mapped.getKind());
        assertEquals("attribute description", mapped.getDescription());
        assertEquals("attribute-name", mapped.getName());
        assertNull(mapped.getGroup());
    }
}

