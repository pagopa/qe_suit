package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceTemplateSOAPInterfaceFailureReason;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EServiceTemplateParameterTypesTest {
    @Test
    void rejection_reasons_are_mapped_exactly_without_url_substring_heuristics() {
        var types = new EServiceTemplateParameterTypes(new InMemoryEntityStore());
        assertThat(types.soapInterfaceFailureReason("numero di URL non corrispondente"))
                .isEqualTo(EServiceTemplateSOAPInterfaceFailureReason.URL_COUNT_MISMATCH);
        assertThat(types.soapInterfaceFailureReason("URL non ammesso"))
                .isEqualTo(EServiceTemplateSOAPInterfaceFailureReason.URL_NOT_ALLOWED);
        assertThatThrownBy(() -> types.soapInterfaceFailureReason("URL sconosciuto"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void resolves_latest_template_version_by_numeric_version_and_explicit_version() {
        var store = new InMemoryEntityStore();
        UUID versionTwo = UUID.randomUUID();
        UUID versionTen = UUID.randomUUID();
        store.upsert(EServiceTemplate.builder().id(UUID.randomUUID())
                .versions(List.of(versionTen("10", versionTen), versionTen("2", versionTwo)))
                .build());
        var types = new EServiceTemplateParameterTypes(store);

        assertThat(types.templateVersion("del template").getId()).isEqualTo(versionTen);
        assertThat(types.templateVersion("versione del template").getId()).isEqualTo(versionTen);
        assertThat(types.templateVersion("versione più recente del template").getId()).isEqualTo(versionTen);
        assertThat(types.templateVersion("della versione 10 del template").getId()).isEqualTo(versionTen);
        assertThat(types.templateVersion("versione 2 del template").getId()).isEqualTo(versionTwo);
        assertThat(types.templateVersion("della versione 2 del template").getId()).isEqualTo(versionTwo);
    }

    @Test
    void resolves_latest_descriptor_aliases_and_explicit_versions_numerically() {
        var store = new InMemoryEntityStore();
        UUID versionTwo = UUID.randomUUID();
        UUID versionTen = UUID.randomUUID();
        EService service = EService.builder().id(UUID.randomUUID())
                .descriptors(List.of(
                        descriptor("10", versionTen, EServiceDescriptorState.DRAFT),
                        descriptor("2", versionTwo, EServiceDescriptorState.PUBLISHED)
                )).build();
        store.upsert(service);
        var types = new EServiceTemplateParameterTypes(store);

        assertThat(types.eServiceDescriptor("versione 2 dell'e-service").getId()).isEqualTo(versionTwo);
        assertThat(types.eServiceDescriptor("versione 10 dell'e-service").getId()).isEqualTo(versionTen);
        assertThat(types.eServiceDescriptor("dell'e-service").getId()).isEqualTo(versionTen);
        assertThat(types.eServiceDescriptor("versione dell'e-service").getId()).isEqualTo(versionTen);
        assertThat(types.eServiceDescriptor("versione più recente dell'e-service").getId()).isEqualTo(versionTen);
    }

    @Test
    void just_created_template_version_aliases_resolve_a_single_version() {
        var store = new InMemoryEntityStore();
        UUID versionId = UUID.randomUUID();
        store.upsert(EServiceTemplate.builder().id(UUID.randomUUID())
                .versions(List.of(versionTen("1", versionId)))
                .build());
        var types = new EServiceTemplateParameterTypes(store);

        assertThat(types.templateVersion("appena creata").getId()).isEqualTo(versionId);
        assertThat(types.templateVersion("versione appena creata").getId()).isEqualTo(versionId);
    }

    @Test
    void just_created_descriptor_aliases_resolve_the_only_draft_and_reject_multiple_descriptors() {
        var store = new InMemoryEntityStore();
        UUID descriptorId = UUID.randomUUID();
        store.upsert(EService.builder().id(UUID.randomUUID()).descriptors(List.of(
                descriptor("1", descriptorId, EServiceDescriptorState.DRAFT)
        )).build());
        var types = new EServiceTemplateParameterTypes(store);

        assertThat(types.eServiceDescriptor("appena creato").getId()).isEqualTo(descriptorId);
        assertThat(types.eServiceDescriptor("appena creata").getId()).isEqualTo(descriptorId);
        assertThat(types.eServiceDescriptor("versione appena creata").getId()).isEqualTo(descriptorId);

        store.upsert(EService.builder().id(UUID.randomUUID()).descriptors(List.of(
                descriptor("1", UUID.randomUUID(), EServiceDescriptorState.PUBLISHED),
                descriptor("2", UUID.randomUUID(), EServiceDescriptorState.DRAFT)
        )).build());

        assertThatThrownBy(() -> types.eServiceDescriptor("appena creato"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("trovati 2 descriptor");
    }

    @Test
    void just_created_template_version_alias_rejects_multiple_versions() {
        var store = new InMemoryEntityStore();
        store.upsert(EServiceTemplate.builder().id(UUID.randomUUID())
                .versions(List.of(
                        versionTen("1", UUID.randomUUID()),
                        versionTen("2", UUID.randomUUID())
                )).build());

        assertThatThrownBy(() -> new EServiceTemplateParameterTypes(store).templateVersion("appena creata"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("trovate 2 versioni");
    }

    @Test
    void numeric_version_resolution_rejects_duplicate_numbers() {
        var templateStore = new InMemoryEntityStore();
        templateStore.upsert(EServiceTemplate.builder().id(UUID.randomUUID())
                .versions(List.of(
                        versionTen("2", UUID.randomUUID()),
                        versionTen("02", UUID.randomUUID())
                )).build());
        var descriptorStore = new InMemoryEntityStore();
        descriptorStore.upsert(EService.builder().id(UUID.randomUUID()).descriptors(List.of(
                descriptor("10", UUID.randomUUID(), EServiceDescriptorState.DRAFT),
                descriptor("010", UUID.randomUUID(), EServiceDescriptorState.PUBLISHED)
        )).build());

        assertThatThrownBy(() -> new EServiceTemplateParameterTypes(templateStore)
                .templateVersion("del template"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Versione numerica duplicata 2");
        assertThatThrownBy(() -> new EServiceTemplateParameterTypes(descriptorStore)
                .eServiceDescriptor("dell'e-service"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Versione numerica duplicata 10");
    }

    private static EServiceTemplateVersion versionTen(String number, UUID id) {
        return EServiceTemplateVersion.builder().id(id).version(number)
                .state(EServiceTemplateVersionState.PUBLISHED).build();
    }

    private static EServiceDescriptor descriptor(String number, UUID id, EServiceDescriptorState state) {
        return EServiceDescriptor.builder().id(id).version(number).state(state).build();
    }
}
