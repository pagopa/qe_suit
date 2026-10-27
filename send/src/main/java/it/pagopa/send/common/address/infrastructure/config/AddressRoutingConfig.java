package it.pagopa.send.common.address.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.send.common.address.application.AddressGateway;
import it.pagopa.send.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.send.common.kernel.domain.Channel;
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
@EnablePluginRegistries({AddressGateway.class})
public class AddressRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public AddressGateway transparentAddressGateway(PluginRegistry<AddressGateway, Channel> registry) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(AddressGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (AddressGateway) proxyFactory.getProxy();
    }
}
