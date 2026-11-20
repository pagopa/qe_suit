package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.datatable.DataTableTypeRegistry;
import io.cucumber.datatable.DataTableTypeRegistryTableConverter;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServicePreparationConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServicePreparationConfigurationMapper;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ConfiguredBoolean;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.journey.application.InteropJourney;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    void mapsBaseThresholdsFromItalianAliases() {
        var configuration = configuration(Map.of(
                "erogatore", "Comune di Milano",
                "sogliaFruitore", "10",
                "sogliaTotale", "20"
        ));

        assertThat(configuration.getConsumerThreshold()).isEqualTo(10);
        assertThat(configuration.getTotalThreshold()).isEqualTo(20);
    }

    @Test
    void mapsBaseThresholdsFromSpacedItalianAliases() {
        var configuration = configuration(Map.of(
                "erogatore", "Comune di Milano",
                "soglia fruitore", "10",
                "soglia totale", "20"
        ));

        assertThat(configuration.getConsumerThreshold()).isEqualTo(10);
        assertThat(configuration.getTotalThreshold()).isEqualTo(20);
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

    @Test
    void createsEServiceWithOnlyTheConfiguredBaseThresholds() {
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var factory = mock(EServiceRequestFactory.class);
        var useCase = mock(EServiceDescriptorUseCase.class);
        var configurationMapper = mock(EServicePreparationConfigurationMapper.class);
        var steps = new EServiceCreationJourneySteps(journey, useCase, factory, configurationMapper);
        var command = mock(UpdateEServiceDescriptorCommand.class, RETURNS_SELF);
        doAnswer(invocation -> {
            Consumer<UpdateEServiceDescriptorCommand> commandConfig = invocation.getArgument(0);
            commandConfig.accept(command);
            return journey;
        }).when(journey).updateDescriptor(any(Consumer.class), eq(EServiceDescriptorState.PUBLISHED));
        DataTable baseThresholds = DataTable.create(
                List.of(
                        List.of("consumerThreshold", "10"),
                        List.of("totalThreshold", "20")
                ),
                new DataTableTypeRegistryTableConverter(new DataTableTypeRegistry(Locale.ENGLISH))
        );

        steps.createEServiceWithBaseThresholds(
                EServiceDescriptorState.PUBLISHED,
                Tenant.COMUNE_DI_MILANO,
                baseThresholds
        );

        verify(journey).withProducer(Tenant.COMUNE_DI_MILANO);
        verify(journey).createEService(EServiceDescriptorState.DRAFT);
        verify(journey).updateDescriptor(any(Consumer.class), eq(EServiceDescriptorState.PUBLISHED));
        verify(command).dailyCallsPerConsumer(10);
        verify(command).dailyCallsTotal(20);
        verifyNoMoreInteractions(command);
    }

    public static class OtherConfiguration {
        public Tenant producer;
        public EServiceMode mode;
    }
}
