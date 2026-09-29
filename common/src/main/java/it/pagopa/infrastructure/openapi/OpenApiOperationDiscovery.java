package it.pagopa.infrastructure.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public final class OpenApiOperationDiscovery {

    private static final String PATH_PARAMETER_KIND = "path parameter";
    private static final String QUERY_PARAMETER_KIND = "query parameter";
    private static final String FORM_FIELD_KIND = "form field";

    private final ClassLoader classLoader;

    public OpenApiOperationDiscovery() {
        this(defaultClassLoader());
    }

    public OpenApiOperationDiscovery(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader must not be null");
    }

    public List<DiscoveredOperation> discover(
            GeneratedApiConfiguration configuration,
            Set<String> operationIds
    ) {
        Objects.requireNonNull(configuration, "configuration must not be null");
        OpenAPI openApi = load(configuration.openApiLocation());
        List<ApiMethod> apiMethods = findApiMethods(configuration.apiPackage());
        Set<String> requested = operationIds == null ? Set.of() : new LinkedHashSet<>(operationIds);
        if (requested.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new IllegalArgumentException("operationIds must not contain null or blank values");
        }

        List<DiscoveredOperation> discovered = new ArrayList<>();
        Set<String> foundIds = new HashSet<>();
        Set<String> seenIds = new HashSet<>();
        if (openApi.getPaths() == null) {
            throw new IllegalStateException("OpenAPI spec contains no paths: " + configuration.openApiLocation());
        }

        openApi.getPaths().forEach((path, pathItem) -> {
            if (pathItem == null) return;
            for (Map.Entry<PathItem.HttpMethod, Operation> entry : pathItem.readOperationsMap().entrySet()) {
                Operation operation = entry.getValue();
                if (operation == null || operation.getOperationId() == null || operation.getOperationId().isBlank()) {
                    continue;
                }
                String operationId = operation.getOperationId();
                if (!seenIds.add(operationId)) {
                    throw new IllegalStateException("Duplicate operationId in OpenAPI spec: " + operationId);
                }
                if (!requested.isEmpty() && !requested.contains(operationId)) continue;
                foundIds.add(operationId);
                discovered.add(discoverOperation(
                        configuration,
                        openApi,
                        path,
                        pathItem,
                        entry.getKey(),
                        operation,
                        apiMethods
                ));
            }
        });

        if (!requested.isEmpty()) {
            Set<String> missing = new LinkedHashSet<>(requested);
            missing.removeAll(foundIds);
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException("OpenAPI operationId not found: " + missing.iterator().next());
            }
        }
        return List.copyOf(discovered);
    }

    private DiscoveredOperation discoverOperation(
            GeneratedApiConfiguration configuration,
            OpenAPI openApi,
            String path,
            PathItem pathItem,
            PathItem.HttpMethod method,
            Operation operation,
            List<ApiMethod> apiMethods
    ) {
        String operationId = operation.getOperationId();
        Class<?> operationClass = resolveOperationClass(operationId, apiMethods);
        ResolvedRequestBody requestBody = resolveRequestBody(
                configuration,
                openApi,
                operationId,
                operationClass,
                operation
        );
        List<PathParameterDescriptor> pathParameters = resolvePathParameters(
                configuration,
                openApi,
                operationId,
                pathItem,
                operation
        );
        List<QueryParameterDescriptor> queryParameters = resolveQueryParameters(
                configuration,
                openApi,
                operationId,
                pathItem,
                operation
        );
        return new DiscoveredOperation(
                operationId,
                method.name(),
                path,
                requestBody.jsonType(),
                requestBody.formFields(),
                pathParameters,
                queryParameters
        );
    }

    private ResolvedRequestBody resolveRequestBody(
            GeneratedApiConfiguration configuration,
            OpenAPI openApi,
            String operationId,
            Class<?> operationClass,
            Operation operation
    ) {
        RequestBody requestBody = operation.getRequestBody();
        if (requestBody == null) return ResolvedRequestBody.none();

        Content content = requestBody.getContent();
        Set<String> mediaTypes = content == null ? Set.of() : new LinkedHashSet<>(content.keySet());
        Optional<String> jsonMediaType = mediaTypes.stream().filter(OpenApiOperationDiscovery::isJsonMediaType).findFirst();
        if (jsonMediaType.isPresent() || mediaTypes.isEmpty()) {
            return ResolvedRequestBody.json(jsonRequestBodyType(operationId, operationClass, mediaTypes));
        }

        Optional<String> formMediaType = mediaTypes.stream().filter(OpenApiOperationDiscovery::isFormMediaType).findFirst();
        if (formMediaType.isEmpty()) {
            throw new IllegalStateException(
                    "Unsupported request body media type for operationId " + operationId + ": " + mediaTypes
            );
        }

        Schema<?> schema = content.get(formMediaType.get()).getSchema();
        if (schema == null) {
            throw new IllegalStateException(
                    "Missing request body schema for operationId " + operationId
                            + ", media type " + formMediaType.get()
            );
        }
        return ResolvedRequestBody.form(resolveFormFields(configuration, openApi, operationId, schema));
    }

    private List<FormFieldDescriptor> resolveFormFields(
            GeneratedApiConfiguration configuration,
            OpenAPI openApi,
            String operationId,
            Schema<?> schema
    ) {
        Schema<?> resolved = resolveSchemaReference(openApi, operationId, schema);
        Map<String, Schema> properties = resolved.getProperties();
        if (properties == null || properties.isEmpty()) {
            throw new IllegalStateException(
                    "Unsupported form request body without properties for operationId " + operationId
            );
        }
        List<FormFieldDescriptor> fields = new ArrayList<>();
        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            Schema<?> fieldSchema = entry.getValue();
            if (fieldSchema == null) {
                throw new IllegalStateException(
                        "Missing schema for form field '" + entry.getKey() + "' in operationId " + operationId
                );
            }
            // Binary fields carry no fuzzable value: the test supplies the file on the generated operation.
            if (isBinarySchema(fieldSchema)) continue;
            Class<?> javaType = resolveScalarType(
                    configuration,
                    openApi,
                    operationId,
                    FORM_FIELD_KIND,
                    entry.getKey(),
                    fieldSchema,
                    true
            );
            fields.add(new FormFieldDescriptor(entry.getKey(), javaType, schemaDescription(fieldSchema)));
        }
        return List.copyOf(fields);
    }

    private Schema<?> resolveSchemaReference(OpenAPI openApi, String operationId, Schema<?> schema) {
        if (schema.get$ref() == null) return schema;
        String componentName = lastReferenceToken(schema.get$ref());
        Schema<?> referenced = openApi.getComponents() == null || openApi.getComponents().getSchemas() == null
                ? null
                : openApi.getComponents().getSchemas().get(componentName);
        if (referenced == null) {
            throw new IllegalStateException(
                    "Cannot resolve request body schema reference in operationId " + operationId
                            + ": " + schema.get$ref()
            );
        }
        return referenced;
    }

    private static boolean isBinarySchema(Schema<?> schema) {
        return "string".equals(schema.getType())
                && ("binary".equals(schema.getFormat()) || "base64".equals(schema.getFormat()));
    }

    private static boolean isJsonMediaType(String mediaType) {
        String value = mediaType.toLowerCase(Locale.ROOT);
        return value.equals("application/json") || value.endsWith("+json") || value.equals("*/*");
    }

    private static boolean isFormMediaType(String mediaType) {
        String value = mediaType.toLowerCase(Locale.ROOT);
        return value.equals("multipart/form-data") || value.equals("application/x-www-form-urlencoded");
    }

    private Optional<Type> jsonRequestBodyType(String operationId, Class<?> operationClass, Set<String> mediaTypes) {
        List<Type> bodyTypes = Stream.of(operationClass.getMethods())
                .filter(method -> method.getName().equals("body") && method.getParameterCount() == 1)
                .map(method -> method.getGenericParameterTypes()[0])
                .distinct()
                .toList();

        if (bodyTypes.size() != 1) {
            throw new IllegalStateException(
                    "Cannot resolve generated request body type for operationId " + operationId
                            + " from " + operationClass.getName()
                            + " (media types " + mediaTypes + ")"
            );
        }
        return Optional.of(bodyTypes.get(0));
    }

    private record ResolvedRequestBody(Optional<Type> jsonType, List<FormFieldDescriptor> formFields) {
        static ResolvedRequestBody none() {
            return new ResolvedRequestBody(Optional.empty(), List.of());
        }

        static ResolvedRequestBody json(Optional<Type> type) {
            return new ResolvedRequestBody(type, List.of());
        }

        static ResolvedRequestBody form(List<FormFieldDescriptor> fields) {
            return new ResolvedRequestBody(Optional.empty(), fields);
        }
    }

    private Class<?> resolveOperationClass(String operationId, List<ApiMethod> apiMethods) {
        List<? extends Class<?>> matches = apiMethods.stream()
                .filter(apiMethod -> apiMethod.method().getName().equals(operationId))
                .map(apiMethod -> (Class<?>) apiMethod.method().getReturnType())
                .distinct()
                .toList();
        if (matches.size() != 1) {
            throw new IllegalStateException(
                    matches.isEmpty()
                            ? "Generated API method not found for operationId " + operationId
                            : "Ambiguous generated API method for operationId " + operationId
            );
        }
        return matches.get(0);
    }

    private List<PathParameterDescriptor> resolvePathParameters(
            GeneratedApiConfiguration configuration,
            OpenAPI openApi,
            String operationId,
            PathItem pathItem,
            Operation operation
    ) {
        Map<String, Parameter> parametersByName = new LinkedHashMap<>();
        addPathParameters(openApi, parametersByName, pathItem.getParameters(), operationId);
        addPathParameters(openApi, parametersByName, operation.getParameters(), operationId);
        List<PathParameterDescriptor> descriptors = new ArrayList<>();
        for (Parameter parameter : parametersByName.values()) {
            Schema<?> schema = parameter.getSchema();
            if (schema == null) {
                throw new IllegalStateException(
                        "Missing schema for path parameter '" + parameter.getName()
                                + "' in operationId " + operationId
                );
            }
            Class<?> javaType = resolveScalarType(
                    configuration,
                    openApi,
                    operationId,
                    PATH_PARAMETER_KIND,
                    parameter.getName(),
                    schema,
                    false
            );
            descriptors.add(new PathParameterDescriptor(parameter.getName(), javaType, schemaDescription(schema)));
        }
        return List.copyOf(descriptors);
    }

    private List<QueryParameterDescriptor> resolveQueryParameters(
            GeneratedApiConfiguration configuration,
            OpenAPI openApi,
            String operationId,
            PathItem pathItem,
            Operation operation
    ) {
        Map<String, Parameter> parametersByName = new LinkedHashMap<>();
        addParameters(openApi, parametersByName, pathItem.getParameters(), operationId, "query");
        addParameters(openApi, parametersByName, operation.getParameters(), operationId, "query");
        List<QueryParameterDescriptor> descriptors = new ArrayList<>();
        for (Parameter parameter : parametersByName.values()) {
            Schema<?> schema = parameter.getSchema();
            if (schema == null) {
                throw new IllegalStateException(
                        "Missing schema for query parameter '" + parameter.getName()
                                + "' in operationId " + operationId
                );
            }
            Type javaType = resolveQueryParameterType(configuration, openApi, operationId, parameter.getName(), schema);
            descriptors.add(new QueryParameterDescriptor(
                    parameter.getName(),
                    javaType,
                    Boolean.TRUE.equals(parameter.getRequired()),
                    schemaDescription(schema)
            ));
        }
        return List.copyOf(descriptors);
    }

    private Type resolveQueryParameterType(
            GeneratedApiConfiguration configuration,
            OpenAPI openApi,
            String operationId,
            String parameterName,
            Schema<?> schema
    ) {
        Schema<?> resolved = schema.get$ref() == null
                ? schema
                : resolveSchemaReference(openApi, operationId, schema);
        if ("array".equals(resolved.getType())) {
            Schema<?> items = resolved.getItems();
            if (items == null) {
                throw new IllegalStateException(
                        "Missing items schema for query parameter '" + parameterName
                                + "' in operationId " + operationId
                );
            }
            Class<?> elementType = resolveScalarType(
                    configuration,
                    openApi,
                    operationId,
                    QUERY_PARAMETER_KIND,
                    parameterName,
                    items,
                    true
            );
            return listOf(elementType);
        }
        return resolveScalarType(
                configuration,
                openApi,
                operationId,
                QUERY_PARAMETER_KIND,
                parameterName,
                resolved,
                true
        );
    }

    private static Type listOf(Class<?> elementType) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{elementType};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }

            @Override
            public String toString() {
                return "java.util.List<" + elementType.getName() + ">";
            }
        };
    }

    private void addPathParameters(
            OpenAPI openApi,
            Map<String, Parameter> destination,
            List<Parameter> parameters,
            String operationId
    ) {
        addParameters(openApi, destination, parameters, operationId, "path");
    }

    private void addParameters(
            OpenAPI openApi,
            Map<String, Parameter> destination,
            List<Parameter> parameters,
            String operationId,
            String in
    ) {
        if (parameters == null) return;
        for (Parameter parameter : parameters) {
            if (parameter == null) continue;
            if (parameter.get$ref() != null) {
                String reference = parameter.get$ref();
                String componentName = lastReferenceToken(reference);
                Parameter resolved = openApi.getComponents() == null || openApi.getComponents().getParameters() == null
                        ? null
                        : openApi.getComponents().getParameters().get(componentName);
                if (resolved == null) {
                    throw new IllegalStateException(
                            "Cannot resolve path parameter reference in operationId " + operationId + ": " + reference
                    );
                }
                parameter = resolved;
            }
            if (in.equals(parameter.getIn())) {
                destination.put(parameter.getName(), parameter);
            }
        }
    }

    private Class<?> resolveScalarType(
            GeneratedApiConfiguration configuration,
            OpenAPI openApi,
            String operationId,
            String kind,
            String parameterName,
            Schema<?> schema,
            boolean allowInlineEnum
    ) {
        return resolveScalarType(
                configuration,
                openApi,
                operationId,
                kind,
                parameterName,
                schema,
                allowInlineEnum,
                new HashSet<>()
        );
    }

    private Class<?> resolveScalarType(
            GeneratedApiConfiguration configuration,
            OpenAPI openApi,
            String operationId,
            String kind,
            String parameterName,
            Schema<?> schema,
            boolean allowInlineEnum,
            Set<String> referenceChain
    ) {
        if (schema.get$ref() != null) {
            String reference = schema.get$ref();
            String componentName = lastReferenceToken(reference);
            if (!referenceChain.add(componentName)) {
                throw new IllegalStateException(
                        "Cyclic " + kind + " schema reference in operationId " + operationId
                                + ", " + kind + " '" + parameterName + "': " + referenceChain
                );
            }
            Optional<Class<?>> refClass = loadModelClass(configuration.modelPackage(), componentName);
            if (refClass.isPresent() && refClass.get().isEnum()) return refClass.get();
            Schema<?> referencedSchema = openApi.getComponents() == null || openApi.getComponents().getSchemas() == null
                    ? null
                    : openApi.getComponents().getSchemas().get(componentName);
            if (referencedSchema != null && !reference.equals(referencedSchema.get$ref())) {
                if (referencedSchema.getEnum() != null && !referencedSchema.getEnum().isEmpty()
                        && !allowInlineEnum) {
                    throw unresolvedEnum(operationId, kind, parameterName, schema);
                }
                return resolveScalarType(
                        configuration,
                        openApi,
                        operationId,
                        kind,
                        parameterName,
                        referencedSchema,
                        allowInlineEnum,
                        referenceChain
                );
            }
        }

        if (schema.getEnum() != null && !schema.getEnum().isEmpty() && !allowInlineEnum) {
            throw unresolvedEnum(operationId, kind, parameterName, schema);
        }

        String type = schema.getType();
        String format = schema.getFormat();
        if ("string".equals(type)) {
            if ("uuid".equals(format)) return java.util.UUID.class;
            return String.class;
        }
        if ("integer".equals(type)) {
            if ("int64".equals(format)) return Long.class;
            return Integer.class;
        }
        if ("number".equals(type)) {
            if ("float".equals(format)) return Float.class;
            if ("double".equals(format)) return Double.class;
            return java.math.BigDecimal.class;
        }
        if ("boolean".equals(type)) return Boolean.class;

        if (schema.get$ref() != null) {
            String componentName = lastReferenceToken(schema.get$ref());
            Optional<Class<?>> refClass = loadModelClass(configuration.modelPackage(), componentName);
            if (refClass.isPresent() && refClass.get().isEnum()) return refClass.get();
        }
        throw new IllegalStateException(
                "Unsupported " + kind + " schema in operationId " + operationId
                        + ", " + kind + " '" + parameterName + "': " + schemaDescription(schema)
        );
    }

    private IllegalStateException unresolvedEnum(String operationId, String kind, String parameterName, Schema<?> schema) {
        return new IllegalStateException(
                "Cannot resolve generated Java enum for operationId " + operationId
                        + ", " + kind + " '" + parameterName + "', schema: " + schemaDescription(schema)
        );
    }

    private Optional<Class<?>> loadModelClass(String modelPackage, String simpleName) {
        try {
            return Optional.of(Class.forName(modelPackage + "." + simpleName, false, classLoader));
        } catch (ClassNotFoundException exception) {
            return Optional.empty();
        }
    }

    private String schemaDescription(Schema<?> schema) {
        if (schema.get$ref() != null) return schema.get$ref();
        return "type=" + schema.getType() + ", format=" + schema.getFormat() + ", enum=" + schema.getEnum();
    }

    private String lastReferenceToken(String reference) {
        int slash = reference.lastIndexOf('/');
        return slash < 0 ? reference : reference.substring(slash + 1);
    }

    private OpenAPI load(String location) {
        ParseOptions options = new ParseOptions();
        options.setResolve(false);
        SwaggerParseResult result = new OpenAPIV3Parser().readLocation(location, null, options);
        OpenAPI openApi = result == null ? null : result.getOpenAPI();
        if (openApi == null) {
            throw new IllegalStateException(
                    "Unable to load OpenAPI spec from " + location + ". Messages: "
                            + (result == null ? "[]" : result.getMessages())
            );
        }
        return openApi;
    }

    private List<ApiMethod> findApiMethods(String apiPackage) {
        String resourcePath = apiPackage.replace('.', '/');
        Set<String> classNames = new LinkedHashSet<>();
        try {
            Enumeration<URL> resources = classLoader.getResources(resourcePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    Path directory = Path.of(resource.toURI());
                    try (Stream<Path> paths = Files.walk(directory)) {
                        paths.filter(Files::isRegularFile)
                                .filter(path -> path.toString().endsWith(".class"))
                                .map(path -> directory.relativize(path).toString()
                                        .replace(path.getFileSystem().getSeparator(), "."))
                                .map(name -> name.substring(0, name.length() - ".class".length()))
                                .filter(name -> !name.contains("$"))
                                .forEach(name -> classNames.add(apiPackage + "." + name));
                    }
                } else if ("jar".equals(resource.getProtocol())) {
                    JarURLConnection connection = (JarURLConnection) resource.openConnection();
                    try (JarFile jar = connection.getJarFile()) {
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            String name = entries.nextElement().getName();
                            if (name.startsWith(resourcePath + "/") && name.endsWith(".class")
                                    && !name.substring(resourcePath.length() + 1).contains("/")) {
                                String simpleName = name.substring(resourcePath.length() + 1, name.length() - 6);
                                if (!simpleName.contains("$")) classNames.add(apiPackage + "." + simpleName);
                            }
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException("Cannot scan generated API package " + apiPackage, exception);
        }

        if (classNames.isEmpty()) {
            throw new IllegalStateException("No generated API classes found in package " + apiPackage);
        }

        List<ApiMethod> apiMethods = new ArrayList<>();
        for (String className : classNames) {
            try {
                Class<?> apiClass = Class.forName(className, false, classLoader);
                for (var method : apiClass.getMethods()) {
                    if (method.getParameterCount() == 0 && method.getDeclaringClass() == apiClass) {
                        apiMethods.add(new ApiMethod(apiClass, method));
                    }
                }
            } catch (LinkageError | ClassNotFoundException exception) {
                throw new IllegalStateException("Cannot load generated API class " + className, exception);
            }
        }
        return List.copyOf(apiMethods);
    }

    private static ClassLoader defaultClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader == null ? OpenApiOperationDiscovery.class.getClassLoader() : contextClassLoader;
    }

    private record ApiMethod(Class<?> apiClass, java.lang.reflect.Method method) {
    }
}
