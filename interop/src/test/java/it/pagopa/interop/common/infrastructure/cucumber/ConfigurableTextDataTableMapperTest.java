package it.pagopa.interop.common.infrastructure.cucumber;

import com.fasterxml.jackson.annotation.JsonAlias;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ConfigurableText;
import it.pagopa.interop.common.infrastructure.cucumber.dto.TextGenerationMode;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ConfigurableTextDataTableMapperTest {
    private static final ValidatorFactory VALIDATORS = Validation.buildDefaultValidatorFactory();
    private final DataTableMapper mapper = new DataTableMapper(VALIDATORS.getValidator());

    public static class TestDto {
        @JsonAlias("nome")
        @Valid
        public ConfigurableText name;
        @JsonAlias("descrizione")
        public String description;
    }

    @AfterAll
    static void close() {
        VALIDATORS.close();
    }

    @Test
    void defaultsToRandomAndKeepsOrdinaryProperties() {
        var dto = (TestDto) mapper.transform(Map.of(
                "nome", "Anagrafe", "descrizione", "descrizione originale"), TestDto.class);
        assertThat(dto.name).isEqualTo(ConfigurableText.random("Anagrafe"));
        assertThat(dto.description).isEqualTo("descrizione originale");
    }

    @Test
    void handlesLiteralAndExplicitRandomAndJavaPropertyName() {
        var literal = (TestDto) mapper.transform(Map.of("nome (letterale)", "Anagrafe"), TestDto.class);
        assertThat(literal.name).isEqualTo(ConfigurableText.literal("Anagrafe"));

        var random = (TestDto) mapper.transform(Map.of("name (casuale)", "Anagrafe"), TestDto.class);
        assertThat(random.name).isEqualTo(ConfigurableText.random("Anagrafe"));
    }

    @Test
    void rejectsTwoHeadersForTheSameProperty() {
        assertThatThrownBy(() -> mapper.transform(Map.of(
                "nome", "A", "nome (letterale)", "B"), TestDto.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate configuration");
    }

    @Test
    void rejectsUnknownModifierAndUnsupportedPropertyModifier() {
        assertThatThrownBy(() -> mapper.transform(Map.of("nome (fisso)", "A"), TestDto.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported text modifier");
        assertThatThrownBy(() -> mapper.transform(Map.of("descrizione (letterale)", "A"), TestDto.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsBlankAndNullValuesViaNestedValidation() {
        assertThatThrownBy(() -> mapper.transform(Map.of("nome (letterale)", " "), TestDto.class))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("name.value");
        Map<String, String> nullValue = new HashMap<>();
        nullValue.put("nome", null);
        assertThatThrownBy(() -> mapper.transform(nullValue, TestDto.class))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void differentRowsDoNotShareTheMode() {
        var first = (TestDto) mapper.transform(Map.of("nome (letterale)", "A"), TestDto.class);
        var second = (TestDto) mapper.transform(Map.of("nome", "B"), TestDto.class);
        assertThat(first.name.mode()).isEqualTo(TextGenerationMode.EXACT);
        assertThat(second.name.mode()).isEqualTo(TextGenerationMode.RANDOM);
    }
}
