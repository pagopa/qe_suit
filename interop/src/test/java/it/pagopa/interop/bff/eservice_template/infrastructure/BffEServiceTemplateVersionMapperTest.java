package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorMapperImpl;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceMapperImpl;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers {@link BffEServiceTemplateVersionMapper#toEServiceTemplateVersion(EServiceTemplateVersionDetails)},
 * in particular that the {@code attributes} field of {@link EServiceTemplateVersionDetails} (typed
 * {@link DescriptorAttributes} on the currently generated BFF client) is preserved end-to-end via
 * {@code BffEServiceDescriptorMapper#toAttributes(DescriptorAttributes)}, following the same
 * flatten + group-index contract already covered for descriptors.
 */
class BffEServiceTemplateVersionMapperTest {

    /**
     * The real MapStruct-generated implementation, with its Spring-injected collaborator mappers
     * wired manually (no Spring context needed for this unit test).
     */
    private final BffEServiceTemplateVersionMapper mapper = createMapper();

    private static BffEServiceTemplateVersionMapper createMapper() {
        BffEServiceTemplateVersionMapperImpl impl = new BffEServiceTemplateVersionMapperImpl();
        ReflectionTestUtils.setField(impl, "bffEServiceMapper", new BffEServiceMapperImpl());
        ReflectionTestUtils.setField(impl, "bffEServiceDescriptorMapper", new BffEServiceDescriptorMapperImpl());
        return impl;
    }

    private static DescriptorAttribute attribute(UUID id, String name) {
        return new DescriptorAttribute()
                .id(id)
                .name(name)
                .description("description-" + name)
                .explicitAttributeVerification(false)
                .kind(AttributeKind.DECLARED);
    }

    private static EServiceTemplateVersionDetails baseSource(DescriptorAttributes attributes) {
        return new EServiceTemplateVersionDetails()
                .id(UUID.randomUUID())
                .version(3)
                .voucherLifespan(60)
                .state(EServiceTemplateVersionState.PUBLISHED)
                .docs(List.of())
                .attributes(attributes)
                ._interface(new EServiceDoc().id(UUID.randomUUID()));
    }

    @Test
    void toEServiceTemplateVersion_preserves_declared_and_certified_attributes() {
        UUID certifiedA = UUID.randomUUID();
        UUID certifiedB = UUID.randomUUID();
        UUID declaredA = UUID.randomUUID();

        DescriptorAttributes attributes = new DescriptorAttributes()
                .certified(List.of(List.of(attribute(certifiedA, "certified-a"), attribute(certifiedB, "certified-b"))))
                .declared(List.of(List.of(attribute(declaredA, "declared-a"))))
                .verified(List.of());

        EServiceTemplateVersionDetails source = baseSource(attributes);

        EServiceTemplateVersion mapped = mapper.toEServiceTemplateVersion(source);

        assertNotNull(mapped.getAttributes());

        List<Attribute> certified = mapped.getAttributes().getCertified();
        assertEquals(2, certified.size());
        assertTrue(certified.stream().allMatch(a -> a.getGroup() == 0));
        assertEquals(certifiedA, certified.get(0).getId());
        assertEquals("certified-a", certified.get(0).getName());
        assertEquals(certifiedB, certified.get(1).getId());
        assertEquals("certified-b", certified.get(1).getName());

        List<Attribute> declared = mapped.getAttributes().getDeclared();
        assertEquals(1, declared.size());
        assertEquals(0, declared.get(0).getGroup());
        assertEquals(declaredA, declared.get(0).getId());
        assertEquals("declared-a", declared.get(0).getName());

        assertNotNull(mapped.getAttributes().getVerified());
        assertTrue(mapped.getAttributes().getVerified().isEmpty());
    }

    @Test
    void toEServiceTemplateVersion_flattens_multiple_declared_groups_preserving_outer_index() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();

        DescriptorAttributes attributes = new DescriptorAttributes()
                .certified(List.of())
                .declared(List.of(
                        List.of(attribute(a, "a"), attribute(b, "b")),
                        List.of(attribute(c, "c"))
                ))
                .verified(List.of());

        EServiceTemplateVersionDetails source = baseSource(attributes);

        EServiceTemplateVersion mapped = mapper.toEServiceTemplateVersion(source);

        List<Attribute> declared = mapped.getAttributes().getDeclared();
        assertEquals(3, declared.size());
        assertEquals(0, declared.get(0).getGroup());
        assertEquals(0, declared.get(1).getGroup());
        assertEquals(1, declared.get(2).getGroup());
    }

    @Test
    void toEServiceTemplateVersion_handles_null_attribute_lists_without_throwing() {
        DescriptorAttributes attributes = new DescriptorAttributes();
        attributes.setCertified(null);
        attributes.setDeclared(null);
        attributes.setVerified(null);

        EServiceTemplateVersionDetails source = baseSource(attributes);

        EServiceTemplateVersion mapped = mapper.toEServiceTemplateVersion(source);

        assertNotNull(mapped.getAttributes());
        assertNotNull(mapped.getAttributes().getCertified());
        assertTrue(mapped.getAttributes().getCertified().isEmpty());
        assertNotNull(mapped.getAttributes().getDeclared());
        assertTrue(mapped.getAttributes().getDeclared().isEmpty());
        assertNotNull(mapped.getAttributes().getVerified());
        assertTrue(mapped.getAttributes().getVerified().isEmpty());
    }

    @Test
    void toEServiceTemplateVersion_still_maps_version_and_interfaceDocument() {
        DescriptorAttributes attributes = new DescriptorAttributes()
                .certified(List.of())
                .declared(List.of())
                .verified(List.of());

        UUID interfaceId = UUID.randomUUID();
        EServiceTemplateVersionDetails source = baseSource(attributes)
                .version(7)
                ._interface(new EServiceDoc().id(interfaceId));

        EServiceTemplateVersion mapped = mapper.toEServiceTemplateVersion(source);

        assertEquals("7", mapped.getVersion());
        assertNotNull(mapped.getInterfaceDocument());
        assertEquals(interfaceId, mapped.getInterfaceDocument().id());
    }
}

