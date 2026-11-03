package it.pagopa.send.common.delegation.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.send.common.delegation.application.DelegationGateway;
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
@EnablePluginRegistries({DelegationGateway.class})
public class DelegationRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public DelegationGateway transparentDelegationGateway(PluginRegistry<DelegationGateway, Channel> registry) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(DelegationGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (DelegationGateway) proxyFactory.getProxy();
    }
}
