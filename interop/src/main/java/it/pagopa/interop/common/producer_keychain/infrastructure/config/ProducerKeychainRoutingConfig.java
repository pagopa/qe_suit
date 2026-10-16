package it.pagopa.interop.common.producer_keychain.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainFactory;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainGateway;
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
@EnablePluginRegistries({ProducerKeychainGateway.class, ProducerKeychainFactory.class})
public class ProducerKeychainRoutingConfig {
    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public ProducerKeychainGateway transparentProducerKeychainGateway(
            PluginRegistry<ProducerKeychainGateway, Channel> registry) {
        ProxyFactory proxy = new ProxyFactory();
        proxy.setInterfaces(ProducerKeychainGateway.class);
        proxy.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));
        return (ProducerKeychainGateway) proxy.getProxy();
    }

    @Bean
    @Primary
    public ProducerKeychainFactory transparentProducerKeychainCommandFactory(
            PluginRegistry<ProducerKeychainFactory, Channel> registry) {
        ProxyFactory proxy = new ProxyFactory();
        proxy.setInterfaces(ProducerKeychainFactory.class);
        proxy.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));
        return (ProducerKeychainFactory) proxy.getProxy();
    }
}
