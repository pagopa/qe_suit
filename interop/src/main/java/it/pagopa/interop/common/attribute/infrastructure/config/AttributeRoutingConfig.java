package it.pagopa.interop.common.attribute.infrastructure.config;

import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.application.AttributeRequestFactory;
import it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor;
import it.pagopa.infrastructure.channel.CurrentChannel;
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
@EnablePluginRegistries({AttributeGateway.class, AttributeRequestFactory.class})
public class AttributeRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public AttributeGateway transparentAttributeGateway(
            PluginRegistry<AttributeGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(AttributeGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (AttributeGateway) proxyFactory.getProxy();
    }

    @Bean
    @Primary
    public AttributeRequestFactory transparentAttributeRequestFactory(
            PluginRegistry<AttributeRequestFactory, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(AttributeRequestFactory.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (AttributeRequestFactory) proxyFactory.getProxy();
    }
}

