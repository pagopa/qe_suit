package it.pagopa.send.common.legal_notification.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.send.common.infrastructure.channel.ChannelRoutingInterceptor;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.PrepareNotificationGateway;
import it.pagopa.send.common.legal_notification.application.AddRecipientGateway;
import it.pagopa.send.common.legal_notification.application.SendNotificationGateway;
import it.pagopa.send.common.legal_notification.application.WaitForNotificationStatusGateway;
import it.pagopa.send.common.legal_notification.application.DeleteNotificationGateway;
import it.pagopa.send.common.legal_notification.application.ReadNotificationGateway;
import it.pagopa.send.common.legal_notification.application.SearchNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;

/**
 * Instradamento per canale dei gateway della notifica legale: per ogni operazione un gateway "trasparente",
 * {@code @Primary}, che delega all'implementazione del canale attivo tramite {@link ChannelRoutingInterceptor}.
 * <p>
 * Decisione: un {@code @Bean} esplicito per interfaccia che richiama un metodo di supporto locale, invece di una
 * registrazione automatica dei bean; il metodo si estrae in un componente comune quando un altro dominio applicherà
 * la stessa scomposizione per operazione.
 */
@Configuration
@RequiredArgsConstructor
@EnablePluginRegistries({
        PrepareNotificationGateway.class,
        AddRecipientGateway.class,
        SendNotificationGateway.class,
        WaitForNotificationStatusGateway.class,
        DeleteNotificationGateway.class,
        ReadNotificationGateway.class,
        SearchNotificationGateway.class
})
public class LegalNotificationRoutingConfig {

    private final ObjectProvider<CurrentChannel<Channel>> currentChannelProvider;

    @Bean
    @Primary
    public PrepareNotificationGateway transparentPrepareNotificationGateway(PluginRegistry<PrepareNotificationGateway, Channel> registry) {
        return transparent(PrepareNotificationGateway.class, registry);
    }

    @Bean
    @Primary
    public AddRecipientGateway transparentAddRecipientGateway(PluginRegistry<AddRecipientGateway, Channel> registry) {
        return transparent(AddRecipientGateway.class, registry);
    }

    @Bean
    @Primary
    public SendNotificationGateway transparentSendNotificationGateway(PluginRegistry<SendNotificationGateway, Channel> registry) {
        return transparent(SendNotificationGateway.class, registry);
    }

    @Bean
    @Primary
    public WaitForNotificationStatusGateway transparentWaitForNotificationStatusGateway(PluginRegistry<WaitForNotificationStatusGateway, Channel> registry) {
        return transparent(WaitForNotificationStatusGateway.class, registry);
    }

    @Bean
    @Primary
    public DeleteNotificationGateway transparentDeleteNotificationGateway(PluginRegistry<DeleteNotificationGateway, Channel> registry) {
        return transparent(DeleteNotificationGateway.class, registry);
    }

    @Bean
    @Primary
    public ReadNotificationGateway transparentReadNotificationGateway(PluginRegistry<ReadNotificationGateway, Channel> registry) {
        return transparent(ReadNotificationGateway.class, registry);
    }

    @Bean
    @Primary
    public SearchNotificationGateway transparentSearchNotificationGateway(PluginRegistry<SearchNotificationGateway, Channel> registry) {
        return transparent(SearchNotificationGateway.class, registry);
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

    @SuppressWarnings("unchecked")
    private <T extends Plugin<Channel>> T transparent(Class<T> type, PluginRegistry<T, Channel> registry) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(type);
        proxyFactory.addAdvice(new ChannelRoutingInterceptor<>(registry, currentChannelProvider));
        return (T) proxyFactory.getProxy();
    }
}
