package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.infrastructure.cucumber.dto.AttributeAliasConfiguration;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.RequiredAttributeConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.RequiredAttributeConfigurationMapper;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequiredAttributeConfigurationMapperTest {
    private final EntityStore entityStore = mock(EntityStore.class);
    private final RequiredAttributeConfigurationMapper mapper = new RequiredAttributeConfigurationMapper(entityStore);

    @Test
    void mapsAllKindsFromRegisteredAliases() {
        var certified = register("CERT-1", AttributeKind.CERTIFIED);
        var declared = register("DECL-1", AttributeKind.DECLARED);
        var verified = register("VER-1", AttributeKind.VERIFIED);
        var discrete = register("DISC-1", AttributeKind.CERTIFIED_DISCRETE);
        var requests = mapper.toRequests(List.of(
                row("certificato", 1, "CERT-1"),
                row("dichiarato", 1, "DECL-1"),
                row("verificato", 1, "VER-1"),
                row("discreto", 1, "DISC-1")
        ));

        assertThat(requests).extracting("kind").containsExactly(
                AttributeKind.CERTIFIED, AttributeKind.DECLARED,
                AttributeKind.VERIFIED, AttributeKind.CERTIFIED_DISCRETE);
        assertThat(requests).extracting(RequiredAttributeCommand::getAttributeRef).containsExactly(
                certified.getRef(), declared.getRef(), verified.getRef(), discrete.getRef());
        assertThat(requests).extracting("group").containsExactly(1, 1, 1, 1);
    }

    @Test
    void rejectsLegacySelectorsEmptyTablesMissingAliasesAndNonContiguousGroups() {
        register("CERT-1", AttributeKind.CERTIFIED);
        register("CERT-2", AttributeKind.CERTIFIED);
        assertThatThrownBy(() -> mapper.toRequests(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        for (String value : List.of("qualunque", "un attributo posseduto da PagoPA",
                "un attributo non posseduto da PagoPA", "un attributo non posseduto e uno posseduto da PagoPA")) {
            assertThatThrownBy(() -> mapper.toRequests(List.of(row("certificato", 1, value))))
                    .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("registered alias");
        }
        when(entityStore.getByAliasOrThrow("CERT-missing", Attribute.class))
                .thenThrow(new IllegalArgumentException("Alias not found"));
        assertThatThrownBy(() -> mapper.toRequests(List.of(row("certificato", 1, "CERT-missing"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("not found");
        assertThatThrownBy(() -> mapper.toRequests(List.of(
                row("certificato", 1, "CERT-1"),
                row("certificato", 3, "CERT-2"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("contiguous");
    }

    @Test
    void convertsTheItalianDataTableHeadersAndValidatesTheDeclaredVocabulary() {
        var factory = Validation.buildDefaultValidatorFactory();
        try {
            var discrete = register("DISC-1", AttributeKind.CERTIFIED_DISCRETE);
            var dataTableMapper = new DataTableMapper(factory.getValidator());
            var configuration = (RequiredAttributeConfiguration) dataTableMapper.transform(Map.of(
                    "tipo", "discreto",
                    "gruppo", "1",
                    "attributo", "DISC-1"
            ), RequiredAttributeConfiguration.class);

            assertThat(mapper.toRequests(List.of(configuration))).singleElement().satisfies(request -> {
                assertThat(request.getKind()).isEqualTo(AttributeKind.CERTIFIED_DISCRETE);
                assertThat(request.getGroup()).isEqualTo(1);
                assertThat(request.getAttributeRef()).isEqualTo(discrete.getRef());
            });
            assertThatThrownBy(() -> dataTableMapper.transform(Map.of(
                    "tipo", "sconosciuto",
                    "gruppo", "1",
                    "attributo", "DISC-1"
            ), RequiredAttributeConfiguration.class)).isInstanceOf(ConstraintViolationException.class);
            assertThatThrownBy(() -> dataTableMapper.transform(Map.of(
                    "tipo", "certificato", "gruppo", "1",
                    "attributo", "un attributo posseduto da PagoPA"
            ), RequiredAttributeConfiguration.class)).isInstanceOf(ConstraintViolationException.class);
        } finally {
            DataTableMapper.clearGherkinKeys();
            factory.close();
        }
    }

    @Test
    void convertsCertifiedAttributeAliasTableRows() {
        var factory = Validation.buildDefaultValidatorFactory();
        try {
            var dataTableMapper = new DataTableMapper(factory.getValidator());
            var alias = (AttributeAliasConfiguration) dataTableMapper.transform(
                    Map.of("alias", "CERT-1"), AttributeAliasConfiguration.class);
            assertThat(alias.getAlias()).isEqualTo("CERT-1");
            assertThatThrownBy(() -> dataTableMapper.transform(
                    Map.of("alias", "existing tenant attribute"), AttributeAliasConfiguration.class))
                    .isInstanceOf(ConstraintViolationException.class);
        } finally {
            DataTableMapper.clearGherkinKeys();
            factory.close();
        }
    }

    @Test
    void resolvesAliasesAndKeepsDifferentAttributesInSeparateGroups() {
        Attribute first = certifiedAttribute("00000000-0000-0000-0000-000000000011");
        Attribute second = certifiedAttribute("00000000-0000-0000-0000-000000000012");
        when(entityStore.getByAliasOrThrow("CERT-1", Attribute.class)).thenReturn(first);
        when(entityStore.getByAliasOrThrow("CERT-2", Attribute.class)).thenReturn(second);

        var requests = mapper.toRequests(List.of(
                row("certificato", 1, "CERT-1"),
                row("certificato", 2, "CERT-2")));

        assertThat(requests).extracting(RequiredAttributeCommand::getAttributeRef)
                .containsExactly(first.getRef(), second.getRef());
        assertThat(requests).extracting(RequiredAttributeCommand::getGroup).containsExactly(1, 2);
        var alternatives = mapper.toRequests(List.of(
                row("certificato", 1, "CERT-1"),
                row("certificato", 1, "CERT-2")));
        assertThat(alternatives).extracting(RequiredAttributeCommand::getGroup).containsExactly(1, 1);
        verify(entityStore, times(2)).getByAliasOrThrow("CERT-1", Attribute.class);
        verify(entityStore, times(2)).getByAliasOrThrow("CERT-2", Attribute.class);
    }

    @Test
    void rejectsAliasedAttributesOfWrongKindAndDuplicateReferencesAcrossGroups() {
        Attribute certified = certifiedAttribute("00000000-0000-0000-0000-000000000013");
        when(entityStore.getByAliasOrThrow("CERT-1", Attribute.class)).thenReturn(certified);
        when(entityStore.getByAliasOrThrow("CERT-2", Attribute.class)).thenReturn(certified);
        when(entityStore.getByAliasOrThrow("CERT-3", Attribute.class)).thenReturn(
                certified.toBuilder().kind(AttributeKind.DECLARED).build());

        assertThatThrownBy(() -> mapper.toRequests(List.of(
                row("certificato", 1, "CERT-1"),
                row("certificato", 2, "CERT-2"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("only once");
        assertThatThrownBy(() -> mapper.toRequests(List.of(
                row("certificato", 1, "CERT-3"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("expected CERTIFIED");
    }

    @Test
    void certifiedAndDiscreteRequirementsShareContiguousGroups() {
        register("CERT-1", AttributeKind.CERTIFIED);
        register("DISC-1", AttributeKind.CERTIFIED_DISCRETE);

        assertThat(mapper.toRequests(List.of(row("certificato", 1, "CERT-1"), row("discreto", 2, "DISC-1"))))
                .extracting(RequiredAttributeCommand::getGroup).containsExactly(1, 2);
        assertThatThrownBy(() -> mapper.toRequests(List.of(
                row("certificato", 1, "CERT-1"), row("discreto", 3, "DISC-1"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("contiguous");
    }

    private static RequiredAttributeConfiguration row(String kind, int group, String attribute) {
        var row = new RequiredAttributeConfiguration();
        row.setKind(kind);
        row.setGroup(group);
        row.setAttribute(attribute);
        return row;
    }

    private static Attribute certifiedAttribute(String id) {
        return Attribute.builder().id(UUID.fromString(id)).kind(AttributeKind.CERTIFIED).build();
    }

    private Attribute register(String alias, AttributeKind kind) {
        Attribute attribute = Attribute.builder().id(UUID.randomUUID()).kind(kind).build();
        when(entityStore.getByAliasOrThrow(alias, Attribute.class)).thenReturn(attribute);
        return attribute;
    }
}
