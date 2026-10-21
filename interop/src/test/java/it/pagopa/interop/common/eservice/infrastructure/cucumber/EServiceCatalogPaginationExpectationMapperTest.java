package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogPageExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogPaginationExpectationMapper;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class EServiceCatalogPaginationExpectationMapperTest {
    private final DataTableMapper transformer;
    private final EServiceCatalogPaginationExpectationMapper mapper =
            Mappers.getMapper(EServiceCatalogPaginationExpectationMapper.class);
    private final AutoCloseable validators;

    EServiceCatalogPaginationExpectationMapperTest() {
        var validatorFactory = Validation.buildDefaultValidatorFactory();
        validators = validatorFactory;
        transformer = new DataTableMapper(validatorFactory.getValidator());
    }

    @AfterEach
    void clearTransformerContext() throws Exception {
        DataTableMapper.clearGherkinKeys();
        validators.close();
    }

    @Test
    void convertsFeatureTableRowsThroughValidatedDtoToApplicationExpectation() {
        List<EServiceCatalogPageExpectation> dtos = List.of(
                transform(Map.of("pagina", "1", "risultati", "2")),
                transform(Map.of("pagina", "2", "risultati", "1")));

        assertThat(mapper.toExpectations(dtos))
                .containsExactly(
                        EServiceCatalogPaginationExpectation.builder().pageNumber(1).resultCount(2).build(),
                        EServiceCatalogPaginationExpectation.builder().pageNumber(2).resultCount(1).build());
    }

    @Test
    void validatesPositivePagesAndNonnegativeResultCounts() {
        assertThatThrownBy(() -> transform(Map.of("pagina", "0", "risultati", "1")))
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> transform(Map.of("pagina", "1", "risultati", "-1")))
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> transform(Map.of("pagina", "1")))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void rejectsDuplicateAndNonconsecutivePageExpectations() {
        var first = transform(Map.of("pagina", "1", "risultati", "2"));
        var duplicate = transform(Map.of("pagina", "1", "risultati", "1"));
        var skipped = transform(Map.of("pagina", "3", "risultati", "1"));

        assertThatThrownBy(() -> mapper.toExpectations(List.of(first, duplicate)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Duplicate");
        assertThatThrownBy(() -> mapper.toExpectations(List.of(first, skipped)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("consecutive");
    }

    private EServiceCatalogPageExpectation transform(Map<String, String> row) {
        return (EServiceCatalogPageExpectation) transformer.transform(row, EServiceCatalogPageExpectation.class);
    }
}
