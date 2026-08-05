package it.pagopa.interop.common.infrastructure.channel;

import it.pagopa.interop.common.infrastructure.cucumber.context.ChannelContext;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;

import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor
public class ChannelRoutingInterceptor<T extends Plugin<Channel>> implements MethodInterceptor {

    private final PluginRegistry<T, Channel> registry;
    private final ObjectProvider<ChannelContext> channelContextProvider;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 1. Recupero il canale attivo a runtime
        Channel activeChannel = channelContextProvider.getObject().getCurrentChannel();
        if (activeChannel == null)
            throw new MissingActiveChannelException();
        
        // 2. Prendo il plugin reale dal registro di Spring
        T activePlugin = registry.getRequiredPluginFor(activeChannel);

        // 3. Eseguo il metodo sul plugin reale
        try {
            return invocation.getMethod().invoke(activePlugin, invocation.getArguments());
        } catch (InvocationTargetException e) {
            // CRITICO: spacchettiamo l'eccezione reale (es. un fallimento di test o asserzione)
            // altrimenti Cucumber vedrebbe sempre e solo una generica InvocationTargetException
            throw e.getTargetException();
        }
    }
}
