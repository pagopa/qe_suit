package it.pagopa.interop.common.infrastructure.auth;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.plugin.core.PluginRegistry;

@Configuration
public class AuthSessionRoutingConfig {
    @Bean
    PluginRegistry<AuthSession, Channel> authSessionRegistry(
            @Qualifier("userAuthSession") AuthSession userSession,
            @Qualifier("m2mV3AuthSession") AuthSession machineSession
    ) {
        return PluginRegistry.of(userSession, machineSession);
    }

    @Bean
    @Primary
    AuthSession transparentAuthSession(
            @Qualifier("authSessionRegistry") PluginRegistry<AuthSession, Channel> registry,
            ObjectProvider<CurrentChannel<Channel>> currentChannelProvider
    ) {
        ProxyFactory proxy = new ProxyFactory();
        proxy.setInterfaces(AuthSession.class);
        proxy.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));
        return (AuthSession) proxy.getProxy();
    }
}
