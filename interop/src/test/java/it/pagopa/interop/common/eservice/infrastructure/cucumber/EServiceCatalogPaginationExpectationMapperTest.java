package it.pagopa.interop.common.eservice.infrastructure.cucumber;

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
    void convertsValidatedPageRowsToResultCounts() {
        List<EServiceCatalogPageExpectation> dtos = List.of(
                transform(Map.of("pagina", "1", "risultati", "2")),
                transform(Map.of("pagina", "2", "risultati", "1")));

        assertThat(mapper.toResultCounts(dtos)).containsExactly(2, 1);
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

        assertThatThrownBy(() -> mapper.toResultCounts(List.of(first, duplicate)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Duplicate");
        assertThatThrownBy(() -> mapper.toResultCounts(List.of(first, skipped)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("consecutive");
    }

    private EServiceCatalogPageExpectation transform(Map<String, String> row) {
        return (EServiceCatalogPageExpectation) transformer.transform(row, EServiceCatalogPageExpectation.class);
    }
}
