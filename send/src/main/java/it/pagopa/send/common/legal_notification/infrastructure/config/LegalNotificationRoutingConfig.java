package it.pagopa.send.common.legal_notification.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.send.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.application.LegalNotificationGateway;
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
@EnablePluginRegistries({LegalNotificationGateway.class})
public class LegalNotificationRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public LegalNotificationGateway transparentLegalNotificationGateway(
            PluginRegistry<LegalNotificationGateway, Channel> registry) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(LegalNotificationGateway.class);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));

        return (LegalNotificationGateway) proxyFactory.getProxy();
    }

    /**
     * Notifica in preparazione per lo scenario corrente: bean {@code @ScenarioScope} (ricreato
     * vuoto ad ogni scenario) iniettato nelle implementazioni del gateway, che lo popolano
     * progressivamente (vedi {@code BffLegalNotificationGateway}). Solo per Cucumber: lo scope
     * {@code cucumber-glue} non esiste fuori da uno scenario, per i test JUnit puri c'è
     * l'equivalente non scoped in {@code JunitContextConfig}.
     */
    @Bean
    @ScenarioScope
    @Profile("cucumber")
    public LegalNotificationCreationRequest legalNotificationCreationRequest() {
        return new LegalNotificationCreationRequest();
    }
}
