package it.pagopa.infrastructure.pst;

import it.pagopa.infrastructure.contract.http.RequestScope;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.NodePath;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class PstConfig {
    private final int successStatus;
    private final Map<FuzzScenario, Integer> scenarioStatus;
    private final Set<String> operations;
    private final List<PstOverride> overrides;

    private PstConfig(
            int successStatus,
            Map<FuzzScenario, Integer> scenarioStatus,
            Set<String> operations,
            List<PstOverride> overrides
    ) {
        validateStatus(successStatus, "successStatus");
        this.successStatus = successStatus;
        this.scenarioStatus = Map.copyOf(scenarioStatus);
        this.operations = Set.copyOf(operations);
        this.overrides = List.copyOf(overrides);
    }

    public static PstConfig load(Path path) {
        Objects.requireNonNull(path, "path must not be null");
        try (InputStream input = Files.newInputStream(path)) {
            LoaderOptions options = new LoaderOptions();
            options.setAllowDuplicateKeys(false);
            Object parsed = new Yaml(new SafeConstructor(options)).load(input);
            return fromYaml(parsed);
        } catch (IOException exception) {
            throw new PstConfigurationException("Cannot read PST configuration " + path, exception);
        } catch (org.yaml.snakeyaml.error.YAMLException exception) {
            throw new PstConfigurationException("Invalid PST YAML in " + path + ": " + exception.getMessage(), exception);
        }
    }

    public int successStatus() {
        return successStatus;
    }

    public Map<FuzzScenario, Integer> scenarioStatus() {
        return scenarioStatus;
    }

    public int statusFor(FuzzScenario scenario) {
        Integer status = scenarioStatus.get(Objects.requireNonNull(scenario, "scenario must not be null"));
        if (status == null) {
            throw new PstConfigurationException("Missing scenarioStatus for " + scenario);
        }
        return status;
    }

    public Set<String> operations() {
        return operations;
    }

    public List<PstOverride> overrides() {
        return overrides;
    }

    private static PstConfig fromYaml(Object parsed) {
        Map<String, Object> root = stringKeyMap(parsed, "PST configuration");
        checkKeys(root, Set.of("successStatus", "scenarioStatus", "operations", "overrides"), "PST configuration");
        int successStatus = requiredStatus(root, "successStatus", "PST configuration");

        Map<String, Object> configuredStatuses = stringKeyMap(
                required(root, "scenarioStatus", "PST configuration"),
                "scenarioStatus"
        );
        EnumMap<FuzzScenario, Integer> scenarioStatuses = new EnumMap<>(FuzzScenario.class);
        for (Map.Entry<String, Object> entry : configuredStatuses.entrySet()) {
            FuzzScenario scenario = parseEnum(FuzzScenario.class, entry.getKey(), "scenarioStatus key");
            scenarioStatuses.put(scenario, status(entry.getValue(), "scenarioStatus." + entry.getKey()));
        }
        EnumSet<FuzzScenario> missing = EnumSet.allOf(FuzzScenario.class);
        missing.removeAll(scenarioStatuses.keySet());
        if (!missing.isEmpty()) {
            throw new PstConfigurationException("scenarioStatus is incomplete. Missing scenarios: " + missing);
        }

        Set<String> operations = root.containsKey("operations")
                ? readOperations(root.get("operations"))
                : Set.of();
        List<PstOverride> overrides = root.containsKey("overrides")
                ? readOverrides(root.get("overrides"))
                : List.of();
        return new PstConfig(successStatus, scenarioStatuses, operations, overrides);
    }

    private static Set<String> readOperations(Object value) {
        if (!(value instanceof List<?> entries)) {
            throw new PstConfigurationException("operations must be a YAML list");
        }
        Set<String> operations = new LinkedHashSet<>();
        for (Object entry : entries) {
            if (!(entry instanceof String operationId) || operationId.isBlank()) {
                throw new PstConfigurationException("operations entries must be non-blank strings");
            }
            if (!operations.add(operationId)) {
                throw new PstConfigurationException("Duplicate operationId in operations: " + operationId);
            }
        }
        return operations;
    }

    private static List<PstOverride> readOverrides(Object value) {
        if (!(value instanceof List<?> entries)) {
            throw new PstConfigurationException("overrides must be a YAML list");
        }
        List<PstOverride> overrides = new ArrayList<>();
        Set<String> keys = new LinkedHashSet<>();
        for (int index = 0; index < entries.size(); index++) {
            String context = "overrides[" + index + "]";
            Map<String, Object> entry = stringKeyMap(entries.get(index), context);
            checkKeys(entry, Set.of("operationId", "scope", "target", "scenario", "status"), context);
            String operationId = requiredString(entry, "operationId", context);
            RequestScope scope = parseEnum(RequestScope.class, requiredString(entry, "scope", context), context + ".scope");
            String targetPointer = requiredTarget(entry, context);
            NodePath target;
            try {
                target = NodePath.fromPointer(targetPointer);
            } catch (IllegalArgumentException exception) {
                throw new PstConfigurationException(context + ".target is not a valid JSON Pointer: " + targetPointer, exception);
            }
            FuzzScenario scenario = parseEnum(
                    FuzzScenario.class,
                    requiredString(entry, "scenario", context),
                    context + ".scenario"
            );
            int status = requiredStatus(entry, "status", context);
            PstOverride override = new PstOverride(operationId, scope, target, scenario, status);
            String key = operationId + "|" + scope + "|" + target + "|" + scenario;
            if (!keys.add(key)) {
                throw new PstConfigurationException("Duplicate PST override: " + key);
            }
            overrides.add(override);
        }
        return List.copyOf(overrides);
    }

    private static Object required(Map<String, Object> values, String key, String context) {
        if (!values.containsKey(key) || values.get(key) == null) {
            throw new PstConfigurationException("Missing required " + context + "." + key);
        }
        return values.get(key);
    }

    private static int requiredStatus(Map<String, Object> values, String key, String context) {
        return status(required(values, key, context), context + "." + key);
    }

    private static int status(Object value, String context) {
        int status;
        if (value instanceof Byte || value instanceof Short || value instanceof Integer) {
            status = ((Number) value).intValue();
        } else if (value instanceof Long number) {
            if (number < 100 || number > 599) {
                throw new PstConfigurationException(context + " must be between 100 and 599");
            }
            status = number.intValue();
        } else if (value instanceof BigInteger number) {
            try {
                status = number.intValueExact();
            } catch (ArithmeticException exception) {
                throw new PstConfigurationException(context + " must be between 100 and 599", exception);
            }
        } else {
            throw new PstConfigurationException(context + " must be an integer HTTP status");
        }
        validateStatus(status, context);
        return status;
    }

    static void validateStatus(int status, String context) {
        if (status < 100 || status > 599) {
            throw new PstConfigurationException(context + " must be between 100 and 599");
        }
    }

    private static String requiredString(Map<String, Object> values, String key, String context) {
        Object value = required(values, key, context);
        if (!(value instanceof String string) || string.isBlank()) {
            throw new PstConfigurationException(context + "." + key + " must be a non-blank string");
        }
        return string;
    }

    private static String requiredTarget(Map<String, Object> values, String context) {
        Object value = required(values, "target", context);
        if (!(value instanceof String target) || (!target.isEmpty() && target.isBlank())) {
            throw new PstConfigurationException(context + ".target must be a JSON Pointer string");
        }
        return target;
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String name, String context) {
        try {
            return Enum.valueOf(type, name);
        } catch (IllegalArgumentException exception) {
            throw new PstConfigurationException("Unknown " + context + ": " + name, exception);
        }
    }

    private static Map<String, Object> stringKeyMap(Object value, String context) {
        if (!(value instanceof Map<?, ?> map)) {
            throw new PstConfigurationException(context + " must be a YAML mapping");
        }
        java.util.LinkedHashMap<String, Object> result = new java.util.LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String key)) {
                throw new PstConfigurationException(context + " keys must be strings");
            }
            result.put(key, entry.getValue());
        }
        return result;
    }

    private static void checkKeys(Map<String, Object> map, Set<String> allowed, String context) {
        Set<String> unknown = new LinkedHashSet<>(map.keySet());
        unknown.removeAll(allowed);
        if (!unknown.isEmpty()) {
            throw new PstConfigurationException("Unknown keys in " + context + ": " + unknown);
        }
    }
}
