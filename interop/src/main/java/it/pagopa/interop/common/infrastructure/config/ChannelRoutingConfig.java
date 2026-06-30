package it.pagopa.interop.common.infrastructure.config;

import it.pagopa.interop.common.cucumber.context.ChannelContext;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.interop.common.contract.client.ProducerKeychainClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@Configuration
@RequiredArgsConstructor
@EnablePluginRegistries({ProducerKeychainClient.class})
public class ChannelRoutingConfig {

    private final ObjectProvider<ChannelContext> channelContextProvider;

    @Bean
    @Primary
    public ProducerKeychainClient transparentProducerKeychainService(
            PluginRegistry<ProducerKeychainClient, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(ProducerKeychainClient.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, channelContextProvider));

        return (ProducerKeychainClient) proxyFactory.getProxy();
    }
}