package it.pagopa.send.common.informal_notification.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.send.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.send.common.informal_notification.application.InformalNotificationGateway;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationCreationRequest;
import it.pagopa.send.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@Configuration
@RequiredArgsConstructor
@EnablePluginRegistries({InformalNotificationGateway.class})
public class InformalNotificationRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public InformalNotificationGateway transparentInformalNotificationGateway(
            PluginRegistry<InformalNotificationGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(InformalNotificationGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (InformalNotificationGateway) proxyFactory.getProxy();
    }

    @Bean
    @ScenarioScope
    @Profile("cucumber")
    public InformalNotificationCreationRequest informalNotificationCreationRequest() {
        return new InformalNotificationCreationRequest();
    }
}
