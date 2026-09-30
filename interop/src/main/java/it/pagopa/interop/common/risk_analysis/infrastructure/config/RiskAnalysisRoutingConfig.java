package it.pagopa.interop.common.risk_analysis.infrastructure.config;

import it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
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
public class RiskAnalysisRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public RiskAnalysisGateway transparentRiskAnalysisGateway(
            PluginRegistry<RiskAnalysisGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(RiskAnalysisGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (RiskAnalysisGateway) proxyFactory.getProxy();
    }
}