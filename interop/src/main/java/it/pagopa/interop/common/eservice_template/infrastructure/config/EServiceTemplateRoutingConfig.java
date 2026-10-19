package it.pagopa.interop.common.eservice_template.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateGateway;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceGateway;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionGateway;
import it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor;
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
@EnablePluginRegistries({
        EServiceTemplateGateway.class,
        EServiceTemplateInstanceGateway.class,
        EServiceTemplateVersionGateway.class,
        EServiceTemplateRequestFactory.class
})
public class EServiceTemplateRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public EServiceTemplateGateway transparentEServiceTemplateGateway(
            PluginRegistry<EServiceTemplateGateway, Channel> registry
    ) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(EServiceTemplateGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (EServiceTemplateGateway) proxyFactory.getProxy();
    }

    @Bean
    @Primary
    public EServiceTemplateRequestFactory transparentEServiceTemplateRequestFactory(
            PluginRegistry<EServiceTemplateRequestFactory, Channel> registry) {
        ProxyFactory factory = new ProxyFactory();
        factory.setInterfaces(EServiceTemplateRequestFactory.class);
        factory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));
        return (EServiceTemplateRequestFactory) factory.getProxy();
    }

    @Bean
    @Primary
    public EServiceTemplateInstanceGateway transparentEServiceTemplateInstanceGateway(
            PluginRegistry<EServiceTemplateInstanceGateway, Channel> registry
    ) {
        ProxyFactory factory = new ProxyFactory();
        factory.setInterfaces(EServiceTemplateInstanceGateway.class);
        factory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));
        return (EServiceTemplateInstanceGateway) factory.getProxy();
    }

    @Bean
    @Primary
    public EServiceTemplateVersionGateway transparentEServiceTemplateVersionGateway(
            PluginRegistry<EServiceTemplateVersionGateway, Channel> registry
    ) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(EServiceTemplateVersionGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (EServiceTemplateVersionGateway) proxyFactory.getProxy();
    }
}
