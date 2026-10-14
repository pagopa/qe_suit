package it.pagopa.interop.common.tenant.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.tenant.application.TenantGateway;
import it.pagopa.interop.common.tenant.application.TenantRequestFactory;
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
@EnablePluginRegistries({TenantGateway.class, TenantRequestFactory.class})
public class TenantRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public TenantGateway transparentTenantGateway(PluginRegistry<TenantGateway, Channel> registry) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(TenantGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));
        return (TenantGateway) proxyFactory.getProxy();
    }

    @Bean
    @Primary
    public TenantRequestFactory transparentTenantRequestFactory(PluginRegistry<TenantRequestFactory, Channel> registry) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(TenantRequestFactory.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));
        return (TenantRequestFactory) proxyFactory.getProxy();
    }
}
