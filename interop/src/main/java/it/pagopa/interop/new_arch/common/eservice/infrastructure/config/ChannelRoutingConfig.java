package it.pagopa.interop.new_arch.common.eservice.infrastructure.config;

import it.pagopa.interop.new_arch.common.agreement.application.AgreementGateway;
import it.pagopa.interop.new_arch.common.eservice.application.EServiceGateway;
import it.pagopa.interop.new_arch.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.new_arch.common.infrastructure.ChannelRoutingInterceptor;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.ChannelContext;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
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
@EnablePluginRegistries({EServiceGateway.class, EServiceRequestFactory.class})
public class ChannelRoutingConfig {

    private final ObjectProvider<ChannelContext> channelContextProvider;

    @Bean
    @Primary
    public EServiceGateway transparentEServiceGateway(
            PluginRegistry<AgreementGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(EServiceGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, channelContextProvider));

        return (EServiceGateway) proxyFactory.getProxy();
    }

    @Bean
    @Primary
    public EServiceRequestFactory transparentEServiceRequestFactory(
            PluginRegistry<AgreementGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(EServiceRequestFactory.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, channelContextProvider));

        return (EServiceRequestFactory) proxyFactory.getProxy();
    }
}