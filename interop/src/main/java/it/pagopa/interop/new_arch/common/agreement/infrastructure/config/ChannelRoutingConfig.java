package it.pagopa.interop.new_arch.common.agreement.infrastructure.config;

import it.pagopa.interop.new_arch.common.agreement.application.AgreementGateway;
import it.pagopa.interop.new_arch.common.agreement.application.AgreementRequestFactory;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.ChannelContext;
import it.pagopa.interop.new_arch.common.infrastructure.interceptor.ChannelRoutingInterceptor;
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
@EnablePluginRegistries({AgreementGateway.class, AgreementRequestFactory.class})
public class ChannelRoutingConfig {

    private final ObjectProvider<ChannelContext> channelContextProvider;

    @Bean
    @Primary
    public AgreementGateway transparentAgreementGateway(
            PluginRegistry<AgreementGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(AgreementGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, channelContextProvider));

        return (AgreementGateway) proxyFactory.getProxy();
    }

    @Bean
    @Primary
    public AgreementRequestFactory transparentAgreementRequestFactory(
            PluginRegistry<AgreementRequestFactory, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(AgreementRequestFactory.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, channelContextProvider));

        return (AgreementRequestFactory) proxyFactory.getProxy();
    }
}