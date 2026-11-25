package it.pagopa.interop.common.infrastructure.cucumber;

import com.fasterxml.jackson.annotation.JsonAlias;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplateInstanceConfiguration;
import it.pagopa.interop.common.kernel.dto.ConfigurableText;
import it.pagopa.interop.common.kernel.dto.ConfigurableTextValueResolver;
import it.pagopa.interop.common.kernel.dto.ConfigurableTextSize;
import it.pagopa.interop.common.kernel.dto.TextGenerationMode;
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

    public static class BoundedDto {
        @JsonAlias("nome")
        @Valid
        @ConfigurableTextSize(max = 12)
        public ConfigurableText name;
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

        var random = (TestDto) mapper.transform(Map.of("name (casuale )", "Anagrafe"), TestDto.class);
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

    @Test
    void generatesBoundedTextIncludingPrefixAndHyphen() {
        var dto = (BoundedDto) mapper.transform(Map.of("nome", "Milano"), BoundedDto.class);
        assertThat(dto.name).isEqualTo(new ConfigurableText("Milano", TextGenerationMode.RANDOM, 12));
        for (int i = 0; i < 20; i++) {
            assertThat(ConfigurableTextValueResolver.resolve(dto.name))
                    .hasSize(12).matches("Milano-[a-zA-Z0-9]{5}");
        }
    }

    @Test
    void bindsBoundedSuffixToTemplateInstanceConfiguration() {
        var dto = (EServiceTemplateInstanceConfiguration) mapper.transform(Map.of(
                "alias", "EI-1", "template", "TE-1", "suffisso", "Milano"),
                EServiceTemplateInstanceConfiguration.class);
        assertThat(dto.getInstanceLabel())
                .isEqualTo(new ConfigurableText("Milano", TextGenerationMode.RANDOM, 12));
        assertThat(ConfigurableTextValueResolver.resolve(dto.getInstanceLabel()))
                .hasSize(12).matches("Milano-[a-zA-Z0-9]{5}");
    }

    @Test
    void supportsJavaPropertyNameAndMinimumRandomSpace() {
        var dto = (BoundedDto) mapper.transform(Map.of("name(casuale)", "abcdefghij"), BoundedDto.class);
        assertThat(ConfigurableTextValueResolver.resolve(dto.name))
                .hasSize(12).matches("abcdefghij-[a-zA-Z0-9]");
    }

    @Test
    void rejectsLimitWithoutEnoughSpaceRatherThanTruncatingPrefix() {
        assertThatThrownBy(() -> mapper.transform(Map.of("nome", "abcdefghijk"), BoundedDto.class))
                .isInstanceOf(ConstraintViolationException.class).hasMessageContaining("name");
    }

    @Test
    void rejectsAllLengthModifiersInHeaders() {
        for (String modifier : new String[]{
                "casuale, max 0", "casuale, max -1", "casuale, max x", "casuale, max 2147483648",
                "max 12", "letterale, max 12", "max 12, casuale", "casuale, max 12, max 13",
                "casuale, max 12", "casuale,max12"}) {
            assertThatThrownBy(() -> mapper.transform(Map.of("nome (" + modifier + ")", "A"), TestDto.class))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        assertThatThrownBy(() -> mapper.transform(Map.of("descrizione (casuale, max 12)", "A"), TestDto.class))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> mapper.transform(Map.of("nome", "A", "nome (casuale)", "B"), BoundedDto.class))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Duplicate configuration");
    }

    @Test
    void preservesUnboundedAndLiteralResolutionAndDoesNotLeakLimitsAcrossRows() {
        mapper.transform(Map.of("nome", "Milano"), BoundedDto.class);
        var dto = (TestDto) mapper.transform(Map.of("nome", "Milano"), TestDto.class);
        assertThat(dto.name.maxLength()).isNull();
        assertThat(ConfigurableTextValueResolver.resolve(dto.name)).hasSize(22)
                .matches("Milano-[a-zA-Z0-9]{15}");
        assertThat(ConfigurableTextValueResolver.resolve(ConfigurableText.literal("Milano"))).isEqualTo("Milano");
    }

    @Test
    void preservesLiteralAtLimitAndRejectsLongerLiteral() {
        var dto = (BoundedDto) mapper.transform(Map.of("nome (letterale)", "abcdefghijkl"), BoundedDto.class);
        assertThat(ConfigurableTextValueResolver.resolve(dto.name)).isEqualTo("abcdefghijkl");
        assertThatThrownBy(() -> mapper.transform(Map.of("nome (letterale)", "abcdefghijklm"), BoundedDto.class))
                .isInstanceOf(ConstraintViolationException.class).hasMessageContaining("name");
    }

    @Test
    void optionalAnnotatedValueCanBeOmittedButNotBlank() {
        var dto = (BoundedDto) mapper.transform(Map.of(), BoundedDto.class);
        assertThat(dto.name).isNull();
        assertThatThrownBy(() -> mapper.transform(Map.of("nome", " "), BoundedDto.class))
                .isInstanceOf(ConstraintViolationException.class).hasMessageContaining("name.value");
    }

    @Test
    void validatesAnnotatedValuesOutsideDataTableMapping() {
        var dto = new BoundedDto();
        dto.name = ConfigurableText.random("abcdefghijk");
        assertThat(VALIDATORS.getValidator().validate(dto)).hasSize(1);
        dto.name = ConfigurableText.literal("abcdefghijkl");
        assertThat(VALIDATORS.getValidator().validate(dto)).isEmpty();
    }
}
