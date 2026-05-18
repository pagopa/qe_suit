package it.frontend.e2e.framework.core.utils;

import it.frontend.e2e.framework.annotation.selector.Property;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Resolver per annotazioni @Property.
 * Segue lo stesso pattern di XPathResolver ma risolve i valori da properties esterne.
 *
 * Uso:
 * PropertyResolver.setProvider(key -> environment.getProperty(key));
 */
public class PropertyResolver {

    private static Function<String, String> provider;

    /**
     * Inizializza il provider di properties.
     * Deve essere chiamato una volta all'inizio dei test.
     */
    public static void setProvider(Function<String, String> propertyProvider) {
        PropertyResolver.provider = propertyProvider;
    }

    /**
     * Risolve il valore della property seguendo lo stesso pattern di XPathResolver:
     * 1. Controlla annotazione @Property sul metodo
     * 2. Se non trovato, controlla annotazione @Property sul tipo di ritorno
     * 3. Se non trovato, ritorna stringa vuota
     */
    public static String resolve(Method method, Class<?> returnType) {
        Property onMethod = method.getAnnotation(Property.class);
        if (onMethod != null) return resolvePropertyValue(onMethod.value());
        Property onType = returnType.getAnnotation(Property.class);
        if (onType != null) return resolvePropertyValue(onType.value());
        return "";
    }

    private static String resolvePropertyValue(String propertyKey) {
        if (provider == null) {
            throw new IllegalStateException("PropertyResolver not initialized. Call PropertyResolver.setProvider() first.");
        }

        String value = provider.apply(propertyKey);
        if (value == null) {
            throw new IllegalArgumentException("Property '" + propertyKey + "' not found");
        }
        return value;
    }
}
