package it.pagopa.send.common.login.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.send.common.login.application.LoginGateway;
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
@EnablePluginRegistries({LoginGateway.class})
public class LoginRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public LoginGateway transparentLoginGateway(PluginRegistry<LoginGateway, Channel> registry) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(LoginGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (LoginGateway) proxyFactory.getProxy();
    }
}
