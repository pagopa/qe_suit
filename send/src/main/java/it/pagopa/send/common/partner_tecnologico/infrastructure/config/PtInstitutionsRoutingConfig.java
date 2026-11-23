package it.pagopa.send.common.partner_tecnologico.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.send.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.partner_tecnologico.application.PtInstitutionsGateway;
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
@EnablePluginRegistries({PtInstitutionsGateway.class})
public class PtInstitutionsRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public PtInstitutionsGateway transparentPtInstitutionsGateway(PluginRegistry<PtInstitutionsGateway, Channel> registry) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(PtInstitutionsGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (PtInstitutionsGateway) proxyFactory.getProxy();
    }
}
