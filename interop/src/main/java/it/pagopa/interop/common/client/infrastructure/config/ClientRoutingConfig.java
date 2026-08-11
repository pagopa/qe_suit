package it.pagopa.interop.common.client.infrastructure.config;

import it.pagopa.interop.common.client.application.ClientCommandFactory;
import it.pagopa.interop.common.client.application.ClientGateway;
import it.pagopa.interop.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.interop.common.kernel.context.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@Configuration
@RequiredArgsConstructor
@EnablePluginRegistries({ClientGateway.class, ClientCommandFactory.class})
public class ClientRoutingConfig {

    private final ObjectProvider<CurrentChannel> currentChannelProvider;

    @Bean
    @Primary
    public ClientGateway transparentClientGateway(
            PluginRegistry<ClientGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(ClientGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (ClientGateway) proxyFactory.getProxy();
    }

    @Bean
    @Primary
    public ClientCommandFactory transparentClientCommandFactory(
            PluginRegistry<ClientCommandFactory, Channel> registry) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(ClientCommandFactory.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (ClientCommandFactory) proxyFactory.getProxy();
    }
}