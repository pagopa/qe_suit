package it.pagopa.interop.common.debug_client_assertion.infrastructure.config;

import it.pagopa.interop.common.debug_client_assertion.application.DebugClientAssertionGateway;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceRiskAnalysisGateway;
import it.pagopa.interop.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.interop.common.infrastructure.cucumber.context.ChannelContext;
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
@EnablePluginRegistries({DebugClientAssertionGateway.class})
public class DebugClientAssertionRoutingConfig {

    private final ObjectProvider<ChannelContext> channelContextProvider;

    @Bean
    @Primary
    public DebugClientAssertionGateway transparentDebugClientAssertionGateway(
            PluginRegistry<DebugClientAssertionGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(DebugClientAssertionGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, channelContextProvider));

        return (DebugClientAssertionGateway) proxyFactory.getProxy();
    }
}