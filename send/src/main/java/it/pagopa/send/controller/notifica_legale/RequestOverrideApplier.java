package it.pagopa.send.controller.notifica_legale;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Applica un insieme di override chiave/valore (es. da una Cucumber DataTable) su un oggetto che
 * espone metodi fluenti (nome metodo == chiave, un solo parametro). Supporta solo campi scalari
 * di primo livello: per override annidati (es. su un elemento di una lista) servirebbe estendere
 * {@link #applyOne} con un path puntato (es. "recipients[0].denomination") che risolva
 * ricorsivamente il sotto-oggetto prima di invocare il setter finale.
 */
public final class RequestOverrideApplier {

    /**
     * Valore convenzionale che, passato come override, forza il campo a {@code null} invece di
     * provare a convertirlo nel tipo del setter. Serve a richiedere esplicitamente l'omissione di
     * un nodo dal JSON (i DTO generati sono {@code @JsonInclude(NON_NULL)}).
     */
    public static final String NULL_TOKEN = "$NULL";

    private RequestOverrideApplier() {
    }

    public static <T> void apply(T target, Map<String, String> overrides) {
        overrides.forEach((key, value) -> applyOne(target, key, value));
    }

    private static <T> void applyOne(T target, String fieldName, String rawValue) {
        String methodName = "abstract".equals(fieldName) ? "_abstract" : fieldName;
        Method setter = findSetter(target.getClass(), methodName);
        if (setter == null) {
            throw new IllegalArgumentException(
                    "Nessun setter fluente trovato per la chiave di override '" + fieldName + "' su " + target.getClass().getSimpleName());
        }
        Object converted = convert(setter.getParameterTypes()[0], rawValue);
        try {
            setter.invoke(target, converted);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Override fallito per " + fieldName + "=" + rawValue, e);
        }
    }

    private static Method findSetter(Class<?> type, String methodName) {
        return Arrays.stream(type.getMethods())
                .filter(m -> m.getName().equals(methodName) && m.getParameterCount() == 1)
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object convert(Class<?> paramType, String rawValue) {
        if (NULL_TOKEN.equals(rawValue)) return null;
        if (paramType == String.class) return rawValue;
        if (paramType == Integer.class || paramType == int.class) return Integer.valueOf(rawValue);
        if (paramType == Boolean.class || paramType == boolean.class) return Boolean.valueOf(rawValue);
        if (paramType.isEnum()) return Enum.valueOf((Class<Enum>) paramType, rawValue);
        throw new IllegalArgumentException("Tipo di override non supportato: " + paramType);
    }
}
