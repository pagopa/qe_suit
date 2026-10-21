package it.pagopa.interop.bff.eservice.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogSearchConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogSearchConfigurationMapper;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffEServiceTemplateCatalogFilterTest {
    private final InMemoryEntityStore entities = new InMemoryEntityStore();
    private final EServiceCatalogSearchConfigurationMapper mapper =
            Mappers.getMapper(EServiceCatalogSearchConfigurationMapper.class);

    @ParameterizedTest
    @ValueSource(strings = {"sì", "si", "no"})
    void realDataTableValidationAndMapStructMappingReachCatalogTransportWithoutLocalFiltering(String value) {
        var template = EServiceTemplate.builder().id(UUID.randomUUID()).build();
        entities.upsert("TPL-1", template);
        var client = mock(ApiClient.class);
        var api = mock(EservicesApi.class);
        var operation = mock(EservicesApi.QueryEServicesCatalogOper.class);
        when(client.eservices()).thenReturn(api);
        when(api.queryEServicesCatalog()).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        UUID resultId = UUID.randomUUID();
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> response = invocation.getArgument(0);
            return response.apply(new ResponseBuilder().setStatusCode(200).setContentType(ContentType.JSON)
                    .setBody("{\"results\":[{\"id\":\"" + resultId
                            + "\",\"name\":\"server result\"}],\"pagination\":{\"offset\":0,\"limit\":20,\"totalCount\":1}}").build());
        });
        var factory = new BffEServiceRequestFactory();
        var command = (BffSearchEServiceCatalogCommand) factory.defaultSearchEServiceCatalogCommand();
        command.keyword("Sintesi");
        try (var validators = Validation.buildDefaultValidatorFactory()) {
            var tables = new DataTableMapper(validators.getValidator());
            mapper.apply(List.of(row(tables, "istanze di template", value),
                    row(tables, "istanza del template", "TPL-1"),
                    row(tables, "categoria dell'erogatore", "COMUNI")), command, entities);
        } finally {
            DataTableMapper.clearGherkinKeys();
        }
        var gateway = new BffEServiceCatalogGateway(new BffEServiceCatalogRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), entities), client),
                Mappers.getMapper(BffEServiceCatalogMapper.class));

        var results = new EServiceCatalogUseCase(gateway).search(command);

        verify(operation).body(same(command.getBffPayload()));
        assertThat(command.getBffPayload().getOnlyTemplateInstances()).isEqualTo(!value.equals("no"));
        assertThat(command.getBffPayload().getTemplateId()).isEqualTo(template.getId());
        assertThat(command.getBffPayload().getProducerCategories()).hasSize(1);
        assertThat(results.getResults()).extracting(entry -> entry.getId()).containsExactly(resultId);
        var copied = (BffSearchEServiceCatalogCommand) factory.searchEServiceCatalogCommandForOffset(command, 20);
        assertThat(copied.getBffPayload().getTemplateId()).isEqualTo(template.getId());
        assertThat(copied.getBffPayload().getOnlyTemplateInstances()).isEqualTo(!value.equals("no"));
    }

    @Test
    void omittedFiltersPreserveFactoryDefaults() {
        var command = (BffSearchEServiceCatalogCommand) new BffEServiceRequestFactory().defaultSearchEServiceCatalogCommand();
        mapper.apply(List.of(), command, entities);
        assertThat(command.getBffPayload().getOnlyTemplateInstances()).isNull();
        assertThat(command.getBffPayload().getTemplateId()).isNull();
    }

    @Test
    void unknownAliasesAndInvalidBooleansFailExplicitly() {
        var command = new BffSearchEServiceCatalogCommand();
        try (var validators = Validation.buildDefaultValidatorFactory()) {
            var tables = new DataTableMapper(validators.getValidator());
            assertThatThrownBy(() -> mapper.apply(List.of(row(tables, "istanza del template", "missing")), command, entities))
                    .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("missing");
            assertThatThrownBy(() -> mapper.apply(List.of(row(tables, "istanze di template", "forse")), command, entities))
                    .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid catalog boolean");
        } finally {
            DataTableMapper.clearGherkinKeys();
        }
    }

    private static EServiceCatalogSearchConfiguration row(DataTableMapper tables, String filter, String value) {
        return (EServiceCatalogSearchConfiguration) tables.transform(Map.of("filtro", filter, "valore", value),
                EServiceCatalogSearchConfiguration.class);
    }
}
