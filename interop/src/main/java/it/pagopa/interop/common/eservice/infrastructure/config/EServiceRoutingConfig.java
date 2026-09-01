package it.pagopa.interop.common.eservice.infrastructure.config;

import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceRiskAnalysisGateway;
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
@EnablePluginRegistries({EServiceGateway.class, EServiceDescriptorGateway.class, EServiceRequestFactory.class, EServiceRiskAnalysisGateway.class})
public class EServiceRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public EServiceGateway transparentEServiceGateway(
            PluginRegistry<EServiceGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(EServiceGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (EServiceGateway) proxyFactory.getProxy();
    }

    @Bean
    @Primary
    public EServiceDescriptorGateway transparentEServiceDescriptorGateway(
            PluginRegistry<EServiceDescriptorGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(EServiceDescriptorGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (EServiceDescriptorGateway) proxyFactory.getProxy();
    }

    @Bean
    @Primary
    public EServiceRequestFactory transparentEServiceRequestFactory(
            PluginRegistry<EServiceRequestFactory, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(EServiceRequestFactory.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (EServiceRequestFactory) proxyFactory.getProxy();
    }

    @Bean
    @Primary
    public EServiceRiskAnalysisGateway transparentEServiceRiskAnalysisGateway(
            PluginRegistry<EServiceRiskAnalysisGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(EServiceRiskAnalysisGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (EServiceRiskAnalysisGateway) proxyFactory.getProxy();
    }
}