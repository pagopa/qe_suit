package it.pagopa.interop.common.infrastructure.channel;

import it.pagopa.interop.common.infrastructure.context.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;

import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor
public class ChannelRoutingInterceptor<T extends Plugin<Channel>>
        implements MethodInterceptor {

    private final PluginRegistry<T, Channel> registry;
    private final ObjectProvider<CurrentChannel> currentChannelProvider;

    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {

        if (isSupportsMethod(invocation)) return false;
        if (isObjectMethod(invocation)) return handleObjectMethod(invocation);

        Channel activeChannel = currentChannelProvider
                .getObject()
                .getCurrentChannel();

        if (activeChannel == null) throw new MissingActiveChannelException();

        T activePlugin = registry.getPluginFor(activeChannel)
                .orElseThrow(() -> new IllegalStateException(
                        buildMissingPluginMessage(invocation, activeChannel)
                ));

        try {
            return invocation.getMethod().invoke(activePlugin, invocation.getArguments());
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private String buildMissingPluginMessage(MethodInvocation invocation, Channel activeChannel) {

        String requestedType = invocation.getMethod()
                .getDeclaringClass()
                .getName();

        String method = invocation.getMethod().getName();

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
                requestedType,
                method,
                activeChannel,
                registeredPlugins
        );
    }

    private static boolean isSupportsMethod(MethodInvocation invocation) {
        return invocation.getMethod().getName().equals("supports")
                && invocation.getMethod().getParameterCount() == 1;
    }

    private static boolean isObjectMethod(MethodInvocation invocation) {
        return switch (invocation.getMethod().getName()) {
            case "toString", "hashCode", "equals" -> true;
            default -> false;
        };
    }

    private Object handleObjectMethod(MethodInvocation invocation) {
        return switch (invocation.getMethod().getName()) {
            case "toString" -> "ChannelRoutingProxy";
            case "hashCode" -> System.identityHashCode(this);
            case "equals" ->
                    invocation.getThis() == invocation.getArguments()[0];
            default -> throw new IllegalStateException(
                    "Unsupported Object method: " + invocation.getMethod()
            );
        };
    }
}