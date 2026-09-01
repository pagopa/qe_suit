package it.pagopa.send.legalnotification.infrastructure.config;

import it.pagopa.send.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.send.common.kernel.context.CurrentChannel;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.legalnotification.application.LegalNotificationGateway;
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
@EnablePluginRegistries({LegalNotificationGateway.class})
public class LegalNotificationRoutingConfig {

    private final ObjectProvider<CurrentChannel> currentChannelProvider;

    @Bean
    @Primary
    public LegalNotificationGateway transparentLegalNotificationGateway(
            PluginRegistry<LegalNotificationGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(LegalNotificationGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (LegalNotificationGateway) proxyFactory.getProxy();
    }
}
