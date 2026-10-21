package it.pagopa.interop.common.purpose_template.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateGateway;
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
@EnablePluginRegistries({PurposeTemplateGateway.class})
public class PurposeTemplateRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public PurposeTemplateGateway transparentPurposeTemplateGateway(
            PluginRegistry<PurposeTemplateGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(PurposeTemplateGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (PurposeTemplateGateway) proxyFactory.getProxy();
    }
}

