package it.pagopa.interop.common.infrastructure;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.channel.ChannelPluginRegistry;
import it.pagopa.infrastructure.channel.ChannelRouter;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public final class ChannelRoutingInterceptor<
        T extends Plugin<Channel>>
        implements MethodInterceptor {

    private final ChannelRouter<T, Channel> router;

    public ChannelRoutingInterceptor(
            PluginRegistry<T, Channel> registry,
            ObjectProvider<CurrentChannel<Channel>> currentChannelProvider
    ) {
        this.router = new ChannelRouter<>(
                adapt(registry),
                currentChannelProvider::getObject
        );
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        if (isSupportsMethod(invocation.getMethod())) {
            return false;
        }

        return router.invoke(
                invocation.getThis(),
                invocation.getMethod(),
                invocation.getArguments()
        );
    }

    private static boolean isSupportsMethod(Method method) {
        return method.getName().equals("supports")
                && method.getParameterCount() == 1;
    }

    private static <T extends Plugin<Channel>>
    ChannelPluginRegistry<T, Channel> adapt(
            PluginRegistry<T, Channel> registry
    ) {
        return new ChannelPluginRegistry<>() {

            @Override
            public Optional<T> getPluginFor(Channel channel) {
                return registry.getPluginFor(channel);
            }

            @Override
            public List<T> getPlugins() {
                return registry.getPlugins();
            }
        };
    }
}