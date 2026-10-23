package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.AttributeOwnershipSelection;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.RequiredAttributeConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.RequiredAttributeConfigurationMapper;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class RequiredAttributeConfigurationMapperTest {
    private final RequiredAttributeConfigurationMapper mapper = new RequiredAttributeConfigurationMapper();

    @Test
    void mapsAllKindsAndSupportedOwnershipSelectors() {
        var requests = mapper.toRequests(List.of(
                row("certificato", 1, "un attributo posseduto da PagoPA"),
                row("dichiarato", 1, "un attributo non posseduto da PagoPA"),
                row("verificato", 1, "un attributo non posseduto e uno posseduto da PagoPA"),
                row("discreto", 1, "un attributo non posseduto da PagoPA")
        ));

        assertThat(requests).extracting("kind").containsExactly(
                AttributeKind.CERTIFIED, AttributeKind.DECLARED,
                AttributeKind.VERIFIED, AttributeKind.CERTIFIED_DISCRETE);
        assertThat(requests).extracting("selection").containsExactly(
                AttributeOwnershipSelection.OWNED_BY_PAGOPA,
                AttributeOwnershipSelection.NOT_OWNED_BY_PAGOPA,
                AttributeOwnershipSelection.NOT_OWNED_AND_OWNED_BY_PAGOPA,
                AttributeOwnershipSelection.NOT_OWNED_BY_PAGOPA);
        assertThat(requests).extracting("group").containsExactly(1, 1, 1, 1);
    }

    @Test
    void rejectsUnknownSelectorsEmptyTablesAndNonContiguousGroups() {
        assertThatThrownBy(() -> mapper.toRequests(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> mapper.toRequests(List.of(row("certificato", 1, "qualunque"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Unsupported attribute selector");
        assertThatThrownBy(() -> mapper.toRequests(List.of(
                row("certificato", 1, "un attributo posseduto da PagoPA"),
                row("certificato", 3, "un attributo posseduto da PagoPA"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("contiguous");
    }

    @Test
    void convertsTheItalianDataTableHeadersAndValidatesTheDeclaredVocabulary() {
        var factory = Validation.buildDefaultValidatorFactory();
        try {
            var dataTableMapper = new DataTableMapper(factory.getValidator());
            var configuration = (RequiredAttributeConfiguration) dataTableMapper.transform(Map.of(
                    "tipo", "discreto",
                    "gruppo", "1",
                    "attributo", "un attributo non posseduto da PagoPA"
            ), RequiredAttributeConfiguration.class);

            assertThat(mapper.toRequests(List.of(configuration))).singleElement().satisfies(request -> {
                assertThat(request.getKind()).isEqualTo(AttributeKind.CERTIFIED_DISCRETE);
                assertThat(request.getGroup()).isEqualTo(1);
                assertThat(request.getSelection()).isEqualTo(AttributeOwnershipSelection.NOT_OWNED_BY_PAGOPA);
            });
            assertThatThrownBy(() -> dataTableMapper.transform(Map.of(
                    "tipo", "sconosciuto",
                    "gruppo", "1",
                    "attributo", "un attributo non posseduto da PagoPA"
            ), RequiredAttributeConfiguration.class)).isInstanceOf(ConstraintViolationException.class);
        } finally {
            DataTableMapper.clearGherkinKeys();
            factory.close();
        }
    }

    private static RequiredAttributeConfiguration row(String kind, int group, String attribute) {
        var row = new RequiredAttributeConfiguration();
        row.setKind(kind);
        row.setGroup(group);
        row.setAttribute(attribute);
        return row;
    }
}
