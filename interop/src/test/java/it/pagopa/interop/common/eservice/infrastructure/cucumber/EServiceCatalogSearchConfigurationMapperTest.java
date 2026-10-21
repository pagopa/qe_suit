package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogSearchConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogSearchConfigurationMapper;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class EServiceCatalogSearchConfigurationMapperTest {
    private static final ValidatorFactory VALIDATORS = Validation.buildDefaultValidatorFactory();
    private final DataTableMapper transformer = new DataTableMapper(VALIDATORS.getValidator());
    private final EServiceCatalogSearchConfigurationMapper mapper =
            Mappers.getMapper(EServiceCatalogSearchConfigurationMapper.class);
    private final BffSearchEServiceCatalogCommand command =
            (BffSearchEServiceCatalogCommand) new BffEServiceRequestFactory().defaultSearchEServiceCatalogCommand();

    @AfterEach
    void clearTransformerContext() {
        DataTableMapper.clearGherkinKeys();
    }

    @AfterAll
    static void closeValidators() {
        VALIDATORS.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"sì", "si", "no"})
    void convertsFeatureRowToDtoAndAppliesItToCommand(String value) {
        EServiceCatalogSearchConfiguration dto = transform(Map.of("filtro", "solo e-service attivi", "valore", value));

        assertThat(dto.getFilter()).isEqualTo("solo e-service attivi");
        assertThat(dto.getValue()).isEqualTo(value);
        mapper.apply(List.of(dto), command);

        assertThat(command.getBffPayload().getOnlyActiveEservices()).isEqualTo(!value.equals("no"));
        assertThat(command.getBffPayload().getOffset()).isZero();
        assertThat(command.getBffPayload().getLimit()).isEqualTo(20);
    }

    @Test
    void mapsPaginationAndPreservesOmittedBoolean() {
        mapper.apply(List.of(
                transform(Map.of("filtro", "a partire dal risultato", "valore", "10")),
                transform(Map.of("filtro", "risultati per pagina", "valore", "2"))), command);

        assertThat(command.getBffPayload().getOffset()).isEqualTo(10);
        assertThat(command.getBffPayload().getLimit()).isEqualTo(2);
        assertThat(command.getBffPayload().getOnlyActiveEservices()).isNull();
    }

    @Test
    void rejectsMissingUnknownAndBlankFieldsThroughDefaultTransformer() {
        assertThatThrownBy(() -> transform(Map.of("filtro", "solo e-service attivi")))
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> transform(Map.of("filtro", "sconosciuto", "valore", "sì")))
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> transform(Map.of("filtro", "solo e-service attivi", "valore", " ")))
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> transform(Map.of("filter", "active", "value", "true")))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "201", "invalid"})
    void rejectsInvalidLimits(String value) {
        var dto = transform(Map.of("filtro", "risultati per pagina", "valore", value));

        assertThatThrownBy(() -> mapper.apply(List.of(dto), command))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void checksDuplicatesBeforeChangingCommand() {
        var dto = transform(Map.of("filtro", "solo e-service attivi", "valore", "sì"));

        assertThatThrownBy(() -> mapper.apply(List.of(dto, dto), command))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Duplicate");
        assertThat(command.getBffPayload().getOnlyActiveEservices()).isNull();
    }

    private EServiceCatalogSearchConfiguration transform(Map<String, String> row) {
        return (EServiceCatalogSearchConfiguration) transformer.transform(row, EServiceCatalogSearchConfiguration.class);
    }
}
