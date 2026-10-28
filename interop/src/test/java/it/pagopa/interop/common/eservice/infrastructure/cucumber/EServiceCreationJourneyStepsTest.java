package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServicePreparationConfiguration;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ConfiguredBoolean;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EServiceCreationJourneyStepsTest {
    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private final DataTableMapper mapper = new DataTableMapper(VALIDATOR_FACTORY.getValidator());

    @AfterAll
    static void closeValidatorFactory() {
        VALIDATOR_FACTORY.close();
    }

    private EServicePreparationConfiguration configuration(Map<String, String> entry) {
        try {
            return (EServicePreparationConfiguration) mapper
                    .transform(entry, EServicePreparationConfiguration.class);
        } finally {
            DataTableMapper.clearGherkinKeys();
        }
    }

    @Test
    void acceptsOnlyProducer() {
        var configuration = configuration(Map.of("erogatore", "Comune di Milano"));

        assertThat(configuration.getProducer()).isEqualTo(Tenant.COMUNE_DI_MILANO);
        assertThat(configuration.getAlias()).isNull();
        assertThat(configuration.getName()).isNull();
        assertThat(configuration.getDescription()).isNull();
        assertThat(configuration.getMode()).isNull();
        assertThat(configuration.getSignalHub()).isEqualTo(ConfiguredBoolean.DEFAULT);
        assertThat(configuration.getAsyncExchange()).isEqualTo(ConfiguredBoolean.DEFAULT);
    }

    @Test
    void mapsAllColumns() {
        var configuration = configuration(Map.of(
                "alias", "ES-1",
                "erogatore", "Sogecap",
                "nome", "Anagrafe",
                "descrizione", "Consultazione dei dati anagrafici.",
                "modalità", "ricezione",
                "Signal Hub", "attivo",
                "scambio asincrono", "no"
        ));

        assertThat(configuration.getAlias()).isEqualTo("ES-1");
        assertThat(configuration.getProducer()).isEqualTo(Tenant.SOGECAP);
        assertThat(configuration.getName()).isEqualTo("Anagrafe");
        assertThat(configuration.getDescription()).isEqualTo("Consultazione dei dati anagrafici.");
        assertThat(configuration.getMode()).isEqualTo(EServiceMode.RECEIVE);
        assertThat(configuration.getSignalHub()).isEqualTo(ConfiguredBoolean.ENABLED);
        assertThat(configuration.getAsyncExchange()).isEqualTo(ConfiguredBoolean.DISABLED);
    }

    @Test
    void preservesUnsetAndEmptyDescription() {
        var configuration = configuration(Map.of(
                "erogatore", "Comune di Milano",
                "descrizione", "",
                "Signal Hub", "non impostato",
                "scambio asincrono", "non impostato"
        ));

        assertThat(configuration.getDescription()).isEmpty();
        assertThat(configuration.getSignalHub()).isEqualTo(ConfiguredBoolean.UNSET);
        assertThat(configuration.getAsyncExchange()).isEqualTo(ConfiguredBoolean.UNSET);
    }

    @ParameterizedTest
    @ValueSource(strings = {"sì", "si"})
    void acceptsAsyncSpellingsUsedByFeatures(String value) {
        var configuration = configuration(Map.of(
                "erogatore", "Comune di Milano", "scambio asincrono", value
        ));
        assertThat(configuration.getAsyncExchange()).isEqualTo(ConfiguredBoolean.ENABLED);
    }

    @Test
    void mapsDeliveryAndDisabledSignalHub() {
        var configuration = configuration(Map.of(
                "erogatore", "Comune di Milano", "modalità", "erogazione", "Signal Hub", "non attivo"
        ));
        assertThat(configuration.getMode()).isEqualTo(EServiceMode.DELIVER);
        assertThat(configuration.getSignalHub()).isEqualTo(ConfiguredBoolean.DISABLED);
    }

    @Test
    void rejectsMissingProducerAndUnknownColumns() {
        assertThatThrownBy(() -> configuration(Map.of()))
                .isInstanceOf(ConstraintViolationException.class).hasMessageContaining("erogatore");
        assertThatThrownBy(() -> configuration(Map.of(
                "erogatore", "Comune di Milano", "unknown", "value"
        ))).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("unknown");
        assertThatThrownBy(() -> configuration(Map.of("erogatore", "unknown")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"erogatore", "alias", "nome", "modalità", "Signal Hub", "scambio asincrono"})
    void rejectsBlankOrInvalidValues(String column) {
        for (String value : new String[]{null, "", " ", "invalid"}) {
            Map<String, String> entry = new HashMap<>(Map.of("erogatore", "Comune di Milano"));
            entry.put(column, value);
            if (("alias".equals(column) || "nome".equals(column)) && "invalid".equals(value)) {
                continue;
            }
            assertThatThrownBy(() -> configuration(entry))
                    .isInstanceOfAny(IllegalArgumentException.class, ConstraintViolationException.class);
        }
    }

    @Test
    void reportsAllFieldViolationsDuringMapping() {
        assertThatThrownBy(() -> configuration(Map.of("alias", " ", "nome", "")))
                .isInstanceOfSatisfying(ConstraintViolationException.class, exception ->
                        assertThat(exception.getConstraintViolations())
                                .extracting(violation -> violation.getPropertyPath().toString())
                                .containsExactlyInAnyOrder("producer", "alias", "name"));
    }

    @Test
    void sharesProducerAndModeConversionsWithOtherDtos() {
        try {
            var result = (OtherConfiguration) mapper.transform(
                    Map.of("producer", "Sogecap", "mode", "ricezione"), OtherConfiguration.class
            );
            assertThat(result.producer).isEqualTo(Tenant.SOGECAP);
            assertThat(result.mode).isEqualTo(EServiceMode.RECEIVE);
        } finally {
            DataTableMapper.clearGherkinKeys();
        }
    }

    @Test
    void keepsFlagVocabulariesSeparate() {
        assertThatThrownBy(() -> configuration(Map.of(
                "erogatore", "Comune di Milano", "Signal Hub", "sì"
        ))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> configuration(Map.of(
                "erogatore", "Comune di Milano", "scambio asincrono", "attivo"
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void injectsValidatorIntoMapperWithSpringAutoConfiguration() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
                .withUserConfiguration(DataTableMapper.class)
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(DataTableMapper.class);
                    try {
                        assertThatThrownBy(() -> context.getBean(DataTableMapper.class)
                                .transform(Map.of(), EServicePreparationConfiguration.class))
                                .isInstanceOf(ConstraintViolationException.class);
                    } finally {
                        DataTableMapper.clearGherkinKeys();
                    }
                });
    }

    public static class OtherConfiguration {
        public Tenant producer;
        public EServiceMode mode;
    }
}
