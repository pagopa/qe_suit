package it.pagopa.interop.common.infrastructure.channel;

import it.pagopa.infrastructure.channel.ChannelPluginRegistry;
import it.pagopa.infrastructure.channel.ChannelRouter;
import it.pagopa.interop.common.kernel.context.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;

public final class ChannelRoutingInterceptor<T extends Plugin<Channel>> implements MethodInterceptor {

    private final ChannelRouter<T, Channel> router;

    public ChannelRoutingInterceptor(ChannelRouter<T, Channel> router) {
        this.router = router;
    }

    public ChannelRoutingInterceptor(PluginRegistry<T, Channel> registry, ObjectProvider<CurrentChannel> currentChannelProvider) {
        this(new ChannelRouter<>(
                adapt(registry),
                currentChannelProvider::getObject
        ));
    }

    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {
        return router.invoke(
                invocation.getThis(),
                invocation.getMethod(),
                invocation.getArguments()
        );
    }

    private static <T extends Plugin<Channel>> ChannelPluginRegistry<T, Channel> adapt(
            PluginRegistry<T, Channel> registry
    ) {
        return new ChannelPluginRegistry<>() {

            @Override
            public java.util.Optional<T> getPluginFor(Channel channel) {
                return registry.getPluginFor(channel);
            }

            @Override
            public java.util.List<T> getPlugins() {
                return registry.getPlugins();
            }
        };
    }
}