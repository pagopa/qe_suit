package it.pagopa.interop.common.journey.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor;
import it.pagopa.interop.common.journey.application.UserJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.plugin.core.PluginRegistry;

@Configuration
public class UserJourneyRoutingConfig {
    @Bean
    PluginRegistry<UserJourney<?>, Channel> userJourneyRegistry(
            @Qualifier("userChannelJourney") UserJourney<?> userJourney,
            @Qualifier("m2Mv3UserJourney") UserJourney<?> m2mJourney
    ) {
        return PluginRegistry.of(userJourney, m2mJourney);
    }

    @Bean
    @Primary
    UserJourney<?> routedUserJourney(
            @Qualifier("userJourneyRegistry") PluginRegistry<UserJourney<?>, Channel> registry,
            ObjectProvider<CurrentChannel<Channel>> currentChannelProvider
    ) {
        ProxyFactory proxy = new ProxyFactory();
        proxy.setInterfaces(UserJourney.class);
        var routing = new ChannelRoutingInterceptor<>(registry, currentChannelProvider);
        proxy.addAdvice((MethodInterceptor) invocation -> {
            Object result = routing.invoke(invocation);
            return result instanceof UserJourney<?>
                    ? ((ProxyMethodInvocation) invocation).getProxy()
                    : result;
        });
        return (UserJourney<?>) proxy.getProxy();
    }
}
