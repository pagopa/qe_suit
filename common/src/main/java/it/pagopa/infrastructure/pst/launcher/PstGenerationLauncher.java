package it.pagopa.infrastructure.pst.launcher;

import it.pagopa.infrastructure.fuzzing.FuzzingProfile;
import it.pagopa.infrastructure.openapi.GeneratedApiConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * JDK-typed entry point used by build tools (e.g. the PST Maven plugin) that load this class
 * reflectively from the project class loader. Only {@link String}/{@link Integer} values cross
 * the boundary, so the caller does not need a compile dependency on this module.
 */
public final class PstGenerationLauncher {

    public static final String CODEGEN_ID = "codegenId";
    public static final String OPENAPI_LOCATION = "openApiLocation";
    public static final String API_PACKAGE = "apiPackage";
    public static final String MODEL_PACKAGE = "modelPackage";
    public static final String CONFIG_PATH = "configPath";
    public static final String OUTPUT_PATH = "outputPath";
    public static final String TITLE = "title";
    public static final String FUZZING_PROFILE = "fuzzingProfile";

    public static final String RESULT_REPORT_PATH = "reportPath";
    public static final String RESULT_OPERATION_COUNT = "operationCount";
    public static final String RESULT_SCENARIO_COUNT = "scenarioCount";

    private PstGenerationLauncher() {
    }

    public static Map<String, Object> generate(Map<String, String> arguments) {
        Objects.requireNonNull(arguments, "arguments must not be null");
        ClassLoader classLoader = PstGenerationLauncher.class.getClassLoader();
        GeneratedApiConfiguration apiConfiguration = new GeneratedApiConfiguration(
                required(arguments, CODEGEN_ID),
                required(arguments, OPENAPI_LOCATION),
                required(arguments, API_PACKAGE),
                required(arguments, MODEL_PACKAGE)
        );
        PstGenerationRequest request = new PstGenerationRequest(
                apiConfiguration,
                Path.of(required(arguments, CONFIG_PATH)),
                Path.of(required(arguments, OUTPUT_PATH)),
                required(arguments, TITLE),
                loadProfile(required(arguments, FUZZING_PROFILE), classLoader)
        );
        PstGenerationResult result = new PstGenerationFacade(classLoader).generate(request);

        Map<String, Object> output = new LinkedHashMap<>();
        output.put(RESULT_REPORT_PATH, result.reportPath().toString());
        output.put(RESULT_OPERATION_COUNT, result.operationCount());
        output.put(RESULT_SCENARIO_COUNT, result.scenarioCount());
        return Map.copyOf(output);
    }

    static FuzzingProfile loadProfile(String className, ClassLoader classLoader) {
        Class<?> type;
        try {
            type = Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException exception) {
            throw new IllegalArgumentException(
                    "FuzzingProfile class not found on project classpath: " + className, exception);
        }
        if (!FuzzingProfile.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(
                    "Class " + className + " does not implement " + FuzzingProfile.class.getName());
        }
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            throw new IllegalArgumentException("FuzzingProfile " + className + " must be a concrete class");
        }
        try {
            Constructor<?> constructor = type.getConstructor();
            return (FuzzingProfile) constructor.newInstance();
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(
                    "FuzzingProfile " + className + " must expose a public no-arg constructor", exception);
        } catch (InvocationTargetException exception) {
            throw new IllegalStateException(
                    "FuzzingProfile " + className + " constructor failed: " + exception.getCause(), exception.getCause());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot instantiate FuzzingProfile " + className, exception);
        }
    }

    private static String required(Map<String, String> arguments, String key) {
        String value = arguments.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing PST launcher argument: " + key);
        }
        return value;
    }
}
