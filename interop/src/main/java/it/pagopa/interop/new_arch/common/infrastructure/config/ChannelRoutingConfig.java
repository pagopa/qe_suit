package it.pagopa.interop.new_arch.common.infrastructure.config;

import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.service.IProducerKeychainTestService;
import it.pagopa.interop.common.cucumber.context.ChannelContext;
import it.pagopa.interop.common.infrastructure.channel.ChannelRoutingInterceptor;
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
@EnablePluginRegistries({IProducerKeychainTestService.class})
public class ChannelRoutingConfig {

    private final ObjectProvider<ChannelContext> channelContextProvider;

    @Bean
    @Primary
    public IProducerKeychainTestService transparentProducerKeychainService(
            PluginRegistry<IProducerKeychainTestService, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(IProducerKeychainTestService.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, channelContextProvider));

        return (IProducerKeychainTestService) proxyFactory.getProxy();
    }
}