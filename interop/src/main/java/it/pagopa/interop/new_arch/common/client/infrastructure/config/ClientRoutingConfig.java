package it.pagopa.interop.new_arch.common.client.infrastructure.config;

import it.pagopa.interop.new_arch.common.client.application.ClientGateway;
import it.pagopa.interop.new_arch.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.ChannelContext;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
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
@EnablePluginRegistries({ClientGateway.class})
public class ClientRoutingConfig {

    private final ObjectProvider<ChannelContext> channelContextProvider;

    @Bean
    @Primary
    public ClientGateway transparentClientGateway(
            PluginRegistry<ClientGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(ClientGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, channelContextProvider));

        return (ClientGateway) proxyFactory.getProxy();
    }
}