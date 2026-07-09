package it.pagopa.interop.new_arch.common.risk_analysis.infrastructure.config;

import it.pagopa.interop.new_arch.common.infrastructure.ChannelRoutingInterceptor;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.ChannelContext;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.purpose.application.PurposeGateway;
import it.pagopa.interop.new_arch.common.risk_analysis.application.RiskAnalysisGateway;
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
@EnablePluginRegistries({RiskAnalysisGateway.class})
public class ChannelRoutingConfig {

    private final ObjectProvider<ChannelContext> channelContextProvider;

    @Bean
    @Primary
    public RiskAnalysisGateway transparentRiskAnalysisGateway(
            PluginRegistry<RiskAnalysisGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(RiskAnalysisGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, channelContextProvider));

        return (RiskAnalysisGateway) proxyFactory.getProxy();
    }
}