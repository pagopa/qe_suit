package it.pagopa.send.common.api_key.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.send.common.api_key.application.ApiKeyGateway;
import it.pagopa.send.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.send.common.kernel.domain.Channel;
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
@EnablePluginRegistries({ApiKeyGateway.class})
public class ApiKeyRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public ApiKeyGateway transparentApiKeyGateway(PluginRegistry<ApiKeyGateway, Channel> registry) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(ApiKeyGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (ApiKeyGateway) proxyFactory.getProxy();
    }
}
