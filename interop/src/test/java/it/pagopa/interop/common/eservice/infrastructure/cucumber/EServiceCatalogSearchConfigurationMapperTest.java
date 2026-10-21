package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogSearchConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogSearchConfigurationMapper;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import it.pagopa.interop.common.kernel.domain.Tenant;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
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
    void mapsCombinedPropertyFiltersToTheBffPayload() {
        mapper.apply(List.of(
                transform(Map.of("filtro", "modalità", "valore", "erogazione")),
                transform(Map.of("filtro", "scambio asincrono", "valore", "sì")),
                transform(Map.of("filtro", "Signal Hub", "valore", "non attivo"))), command);

        var payload = command.getBffPayload();
        assertThat(payload.getMode())
                .isEqualTo(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.DELIVER);
        assertThat(payload.getAsyncExchange()).isTrue();
        assertThat(payload.getOnlySignalHubEnabled()).isFalse();
        assertThat(payload.getOnlyActiveEservices()).isNull();
        assertThat(payload.getOffset()).isZero();
        assertThat(payload.getLimit()).isEqualTo(20);
    }

    @ParameterizedTest
    @CsvSource({"erogazione,DELIVER", "ricezione,RECEIVE"})
    void mapsBothModes(String value, String expected) {
        mapper.apply(List.of(transform(Map.of("filtro", "modalità", "valore", value))), command);

        assertThat(command.getBffPayload().getMode().name()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"sì,true", "si,true", "no,false"})
    void mapsAsyncExchange(String value, boolean expected) {
        mapper.apply(List.of(transform(Map.of("filtro", "scambio asincrono", "valore", value))), command);

        assertThat(command.getBffPayload().getAsyncExchange()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"attivo,true", "non attivo,false"})
    void mapsSignalHub(String value, boolean expected) {
        mapper.apply(List.of(transform(Map.of("filtro", "Signal Hub", "valore", value))), command);

        assertThat(command.getBffPayload().getOnlySignalHubEnabled()).isEqualTo(expected);
    }

    @Test
    void omittedPropertyFiltersRemainUnset() {
        mapper.apply(List.of(), command);

        assertThat(command.getBffPayload().getMode()).isNull();
        assertThat(command.getBffPayload().getAsyncExchange()).isNull();
        assertThat(command.getBffPayload().getOnlySignalHubEnabled()).isNull();
        assertThat(command.getBffPayload().getSortBy()).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "nome crescente,NAME_ASC",
            "nome decrescente,NAME_DESC",
            "data di creazione crescente,CREATED_AT_ASC",
            "data di creazione decrescente,CREATED_AT_DESC"
    })
    void mapsEverySupportedSortMode(String value, String expected) {
        mapper.apply(List.of(transform(Map.of("filtro", "ordinamento", "valore", value))), command);

        assertThat(command.getBffPayload().getSortBy().name()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"nome", "Nome crescente", "data crescente"})
    void rejectsUnsupportedSortValues(String value) {
        var dto = transform(Map.of("filtro", "ordinamento", "valore", value));

        assertThatThrownBy(() -> mapper.apply(List.of(dto), command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ordinamento catalogo non supportato");
    }

    @ParameterizedTest
    @CsvSource({"modalità,invalid", "scambio asincrono,forse", "Signal Hub,non impostato", "Signal Hub,no"})
    void rejectsInvalidPropertyFilterValues(String filter, String value) {
        var dto = transform(Map.of("filtro", filter, "valore", value));

        assertThatThrownBy(() -> mapper.apply(List.of(dto), command))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void mapsSingleProducerToItsOrganizationId() {
        mapper.apply(List.of(transform(Map.of("filtro", "erogatore", "valore", "Comune di Milano"))), command);

        assertThat(command.getBffPayload().getProducersIds())
                .containsExactly(Tenant.COMUNE_DI_MILANO.getOrganizationId());
        assertThat(command.getBffPayload().getOffset()).isZero();
        assertThat(command.getBffPayload().getLimit()).isEqualTo(20);
    }

    @Test
    void mapsMultipleProducersAsAnAlternativeList() {
        mapper.apply(List.of(transform(Map.of("filtro", "erogatore",
                "valore", "Comune di Milano e Comune di Pozzallo"))), command);

        assertThat(command.getBffPayload().getProducersIds()).containsExactly(
                Tenant.COMUNE_DI_MILANO.getOrganizationId(),
                Tenant.COMUNE_DI_POZZALLO.getOrganizationId());
    }

    @Test
    void mapsExplicitEmptyProducerList() {
        command.producersIds(List.of(Tenant.COMUNE_DI_MILANO.getOrganizationId()));

        mapper.apply(List.of(transform(Map.of("filtro", "erogatore", "valore", "elenco vuoto"))), command);

        assertThat(command.getBffPayload().getProducersIds()).isEmpty();
    }

    @Test
    void omittedProducerFilterPreservesFactoryDefaults() {
        var original = command.getBffPayload().getProducersIds();

        mapper.apply(List.of(), command);

        assertThat(command.getBffPayload().getProducersIds()).isSameAs(original);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Comune sconosciuto", "Comune di Milano e sconosciuto",
            "Comune di Milano e ", " e Comune di Milano"})
    void rejectsUnknownOrEmptyProducersWithoutSettingAPartialList(String value) {
        var original = command.getBffPayload().getProducersIds();
        var dto = transform(Map.of("filtro", "erogatore", "valore", value));

        assertThatThrownBy(() -> mapper.apply(List.of(dto), command))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(command.getBffPayload().getProducersIds()).isSameAs(original);
    }

    @Test
    void combinesProducerAndActiveFilters() {
        mapper.apply(List.of(
                transform(Map.of("filtro", "erogatore", "valore", "Comune di Pozzallo")),
                transform(Map.of("filtro", "solo e-service attivi", "valore", "sì"))), command);

        assertThat(command.getBffPayload().getProducersIds())
                .containsExactly(Tenant.COMUNE_DI_POZZALLO.getOrganizationId());
        assertThat(command.getBffPayload().getOnlyActiveEservices()).isTrue();
    }

    @Test
    void mapsSingleCategoryFromFeatureRowToBffPayload() {
        mapper.apply(List.of(transform(Map.of(
                "filtro", "categoria dell'erogatore", "valore", "COMUNI"))), command);

        assertThat(command.getBffPayload().getProducerCategories()).containsExactly(
                it.pagopa.interop.generated.openapi.clients.bff.model.EServiceProducerCategory.COMUNI);
    }

    @Test
    void mapsAlternativeCategoriesAndExplicitEmptyList() {
        mapper.apply(List.of(transform(Map.of("filtro", "categoria dell'erogatore",
                "valore", "COMUNI e SCUOLE"))), command);

        assertThat(command.getBffPayload().getProducerCategories()).containsExactly(
                it.pagopa.interop.generated.openapi.clients.bff.model.EServiceProducerCategory.COMUNI,
                it.pagopa.interop.generated.openapi.clients.bff.model.EServiceProducerCategory.SCUOLE);

        mapper.apply(List.of(transform(Map.of(
                "filtro", "categoria dell'erogatore", "valore", "elenco vuoto"))), command);
        assertThat(command.getBffPayload().getProducerCategories()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"CAT-1", "COMUNI e ", " e COMUNI", "COMUNI e UNKNOWN"})
    void rejectsInvalidCategoriesWithoutApplyingAPartialList(String value) {
        var original = command.getBffPayload().getProducerCategories();
        var dto = transform(Map.of("filtro", "categoria dell'erogatore", "valore", value));

        assertThatThrownBy(() -> mapper.apply(List.of(dto), command))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(command.getBffPayload().getProducerCategories()).isSameAs(original);
    }

    @Test
    void omittedCategoriesPreserveDefaultsAndAllDomainCategoriesMapToBff() {
        var original = command.getBffPayload().getProducerCategories();
        mapper.apply(List.of(), command);
        assertThat(command.getBffPayload().getProducerCategories()).isSameAs(original);

        var categories = List.of(it.pagopa.interop.common.tenant.domain.TenantCategory.values());
        command.producerCategories(categories);
        assertThat(command.getBffPayload().getProducerCategories())
                .extracting(Enum::name)
                .containsExactlyElementsOf(categories.stream().map(Enum::name).toList());
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
