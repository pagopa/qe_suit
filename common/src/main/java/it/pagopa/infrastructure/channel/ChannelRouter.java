package it.pagopa.infrastructure.channel;

import it.pagopa.application.ChannelKind;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Supplier;

public final class ChannelRouter<T, C extends Enum<C> & ChannelKind> {

    private final ChannelPluginRegistry<T, C> registry;
    private final Supplier<CurrentChannel<C>> currentChannelProvider;

    public ChannelRouter(
            ChannelPluginRegistry<T, C> registry,
            Supplier<CurrentChannel<C>> currentChannelProvider
    ) {
        this.registry = Objects.requireNonNull(registry);
        this.currentChannelProvider = Objects.requireNonNull(currentChannelProvider);
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {

        if (isObjectMethod(method)) {
            return handleObjectMethod(proxy, method, arguments);
        }

        C activeChannel = currentChannelProvider
                .get()
                .getCurrentChannel();

        if (activeChannel == null) {
            throw new MissingActiveChannelException();
        }

        T activePlugin = registry.getPluginFor(activeChannel)
                .orElseThrow(() -> new IllegalStateException(
                        missingPluginMessage(method, activeChannel)
                ));

        try {
            return method.invoke(activePlugin, arguments);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private static boolean isObjectMethod(Method method) {
        return switch (method.getName()) {
            case "toString", "hashCode", "equals" -> true;
            default -> false;
        };
    }

    private static Object handleObjectMethod(
            Object proxy,
            Method method,
            Object[] arguments
    ) {
        return switch (method.getName()) {
            case "toString" -> "ChannelRoutingProxy";
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" ->
                    arguments != null
                            && arguments.length == 1
                            && proxy == arguments[0];
            default -> throw new IllegalStateException(
                    "Unsupported Object method: " + method
            );
        };
    }

    private String missingPluginMessage(Method method, C activeChannel) {
        String registeredPlugins = registry.getPlugins()
                .stream()
                .map(plugin -> plugin.getClass().getName())
                .toList()
                .toString();

        return """
                No channel plugin found.
                Requested interface: %s
                Requested method: %s
                Active channel: %s
                Registered plugins: %s
                """.formatted(
                method.getDeclaringClass().getName(),
                method.getName(),
                activeChannel,
                registeredPlugins
        );
    }
}