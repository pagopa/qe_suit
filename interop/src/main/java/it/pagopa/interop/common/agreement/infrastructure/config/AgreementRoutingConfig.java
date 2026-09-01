package it.pagopa.interop.common.agreement.infrastructure.config;

import it.pagopa.interop.common.agreement.application.AgreementGateway;
import it.pagopa.interop.common.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.infrastructure.channel.ChannelRoutingInterceptor;
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
@EnablePluginRegistries({AgreementGateway.class})
public class AgreementRoutingConfig {

    private final ObjectProvider<CurrentChannel> currentChannelProvider;

    @Bean
    @Primary
    public AgreementGateway transparentAgreementGateway(
            PluginRegistry<AgreementGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(AgreementGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (AgreementGateway) proxyFactory.getProxy();
    }
}