package it.pagopa.infrastructure.openapi;

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiOperationDiscoveryTest {

    private final OpenApiOperationDiscovery discovery = new OpenApiOperationDiscovery();
    private final GeneratedApiConfiguration configuration = configuration();

    @Test
    void discovers_every_operation_with_method_path_body_and_path_parameters() {
        List<DiscoveredOperation> operations = discovery.discover(configuration, Set.of());

        assertEquals(
                List.of("createWidget", "createWidgetBatch", "getWidget", "getWidgetBySlug", "getWidgetByKind",
                        "uploadWidgetDocument", "submitWidgetForm", "getWidgetMetrics"),
                operations.stream().map(DiscoveredOperation::operationId).toList()
        );
        DiscoveredOperation create = operations.get(0);
        assertEquals("POST", create.httpMethod());
        assertEquals("/widgets", create.path());
        assertEquals(WidgetPayload.class, create.requestBodyType().orElseThrow());

        DiscoveredOperation createBatch = operations.get(1);
        ParameterizedType batchBodyType = (ParameterizedType) createBatch.requestBodyType().orElseThrow();
        assertEquals(List.class, batchBodyType.getRawType());
        assertEquals(WidgetPayload.class, batchBodyType.getActualTypeArguments()[0]);
        Object batchSeed = new it.pagopa.infrastructure.openapi.seed.OperationSeedFactory(
                new it.pagopa.infrastructure.openapi.seed.DeterministicSeedFactory()
        ).create(createBatch).requestBody().orElseThrow();
        assertTrue(batchSeed instanceof List<?>);
        assertEquals(WidgetPayload.class, ((List<?>) batchSeed).get(0).getClass());

        DiscoveredOperation get = operations.get(2);
        assertEquals("GET", get.httpMethod());
        assertEquals("/widgets/{widgetId}/{revision}", get.path());
        assertEquals(List.of("widgetId", "revision"), get.pathParameters().stream().map(PathParameterDescriptor::name).toList());
        assertEquals(List.of(java.util.UUID.class, Integer.class), get.pathParameters().stream().map(PathParameterDescriptor::javaType).toList());
        assertTrue(get.requestBodyType().isEmpty());
    }

    @Test
    void filters_by_operation_ids_and_fails_for_missing_ids() {
        List<DiscoveredOperation> selected = discovery.discover(
                configuration,
                new LinkedHashSet<>(List.of("getWidgetBySlug", "createWidget"))
        );

        assertEquals(List.of("createWidget", "getWidgetBySlug"), selected.stream().map(DiscoveredOperation::operationId).toList());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> discovery.discover(configuration, Set.of("doesNotExist"))
        );
        assertTrue(exception.getMessage().contains("doesNotExist"));
    }

    @Test
    void resolves_enum_references_but_rejects_inline_enums_with_context() {
        DiscoveredOperation referenced = discovery.discover(configuration, Set.of("getWidgetByKind")).get(0);
        assertEquals(WidgetKind.class, referenced.pathParameters().get(0).javaType());

        GeneratedApiConfiguration inlineEnumConfiguration = configuration("/openapi/inline-enum-path.yaml");
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> discovery.discover(inlineEnumConfiguration, Set.of("getWidgetByInlineKind"))
        );
        assertTrue(exception.getMessage().contains("getWidgetByInlineKind"));
        assertTrue(exception.getMessage().contains("kind"));
        assertTrue(exception.getMessage().contains("enum"));
    }

    @Test
    void creates_typed_deterministic_path_parameter_map() {
        DiscoveredOperation operation = discovery.discover(configuration, Set.of("getWidget")).get(0);
        var factory = new it.pagopa.infrastructure.openapi.seed.OperationSeedFactory(
                new it.pagopa.infrastructure.openapi.seed.DeterministicSeedFactory()
        );

        var first = factory.create(operation);
        var second = factory.create(operation);
        assertEquals(java.util.UUID.fromString("11111111-1111-4111-8111-111111111111"), first.pathParameters().get("widgetId"));
        assertEquals(1, first.pathParameters().get("revision"));
        assertEquals(first.pathParameters(), second.pathParameters());

        DiscoveredOperation metrics = discovery.discover(configuration, Set.of("getWidgetMetrics")).get(0);
        var metricsSeed = factory.create(metrics).pathParameters();
        assertEquals(Float.class, metricsSeed.get("ratio").getClass());
        assertEquals(Boolean.class, metricsSeed.get("active").getClass());
        assertEquals(Long.class, metricsSeed.get("large").getClass());
        assertEquals(Double.class, metricsSeed.get("threshold").getClass());
    }

    @Test
    void discovers_multipart_form_fields_and_skips_binary_ones() {
        DiscoveredOperation operation = discovery.discover(configuration, Set.of("uploadWidgetDocument")).get(0);

        assertTrue(operation.requestBodyType().isEmpty());
        assertEquals(
                List.of("name", "revision", "kind"),
                operation.requestBodyFormFields().stream().map(FormFieldDescriptor::name).toList()
        );
        assertEquals(
                List.of(String.class, Integer.class, String.class),
                operation.requestBodyFormFields().stream().map(FormFieldDescriptor::javaType).toList()
        );
        assertEquals(List.of("widgetId"), operation.pathParameters().stream().map(PathParameterDescriptor::name).toList());

        var seed = new it.pagopa.infrastructure.openapi.seed.OperationSeedFactory(
                new it.pagopa.infrastructure.openapi.seed.DeterministicSeedFactory()
        ).create(operation);
        java.util.Map<?, ?> formBody = (java.util.Map<?, ?>) seed.requestBody().orElseThrow();
        assertEquals(List.of("name", "revision", "kind"), List.copyOf(formBody.keySet()));
        assertEquals("seed", formBody.get("name"));
        assertEquals(1, formBody.get("revision"));
    }

    @Test
    void discovers_url_encoded_form_fields_through_schema_reference() {
        DiscoveredOperation operation = discovery.discover(configuration, Set.of("submitWidgetForm")).get(0);

        assertTrue(operation.requestBodyType().isEmpty());
        assertEquals(
                List.of("code", "active"),
                operation.requestBodyFormFields().stream().map(FormFieldDescriptor::name).toList()
        );
        assertEquals(
                List.of(String.class, Boolean.class),
                operation.requestBodyFormFields().stream().map(FormFieldDescriptor::javaType).toList()
        );
    }

    @Test
    void rejects_unsupported_request_body_media_types() {
        GeneratedApiConfiguration unsupported = configuration("/openapi/unsupported-body-media-type.yaml");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> discovery.discover(unsupported, Set.of("uploadWidgetRaw"))
        );
        assertTrue(exception.getMessage().contains("uploadWidgetRaw"));
        assertTrue(exception.getMessage().contains("application/octet-stream"));
    }

    private GeneratedApiConfiguration configuration() {
        return configuration("/openapi/operation-discovery.yaml");
    }

    private GeneratedApiConfiguration configuration(String resource) {
        URL spec = getClass().getResource(resource);
        if (spec == null) throw new IllegalStateException("Missing operation discovery fixture");
        return new GeneratedApiConfiguration(
                "fixture-codegen",
                Path.of(spec.getPath()).toString(),
                DiscoveryFixtureApi.class.getPackageName(),
                WidgetKind.class.getPackageName()
        );
    }
}
