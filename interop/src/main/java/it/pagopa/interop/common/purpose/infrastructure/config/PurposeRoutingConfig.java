package it.pagopa.interop.common.purpose.infrastructure.config;

import it.pagopa.interop.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.interop.common.infrastructure.context.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.purpose.application.PurposeGateway;
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
@EnablePluginRegistries({PurposeGateway.class})
public class PurposeRoutingConfig {

    private final ObjectProvider<CurrentChannel> currentChannelProvider;

    @Bean
    @Primary
    public PurposeGateway transparentPurposeGateway(
            PluginRegistry<PurposeGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(PurposeGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (PurposeGateway) proxyFactory.getProxy();
    }
}