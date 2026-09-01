package it.pagopa.send.common.infrastructure.config;


import it.pagopa.send.common.journey.application.JourneyModule;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class JourneyInvocationHandler implements InvocationHandler {
    private final List<JourneyModule> delegates;

    public JourneyInvocationHandler(List<JourneyModule> delegates) {
        this.delegates = delegates;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        for (Object delegate : delegates) {
            try {
                // Controlla se questo delegato ha il metodo richiesto
                Method targetMethod = delegate.getClass().getMethod(method.getName(), method.getParameterTypes());

                // Esegue il metodo sul delegato reale
                Object result = targetMethod.invoke(delegate, args);

                // Se il metodo restituisce il delegato stesso (return this) o se il tipo di ritorno
                // è una delle interfacce del Journey, restituiamo il PROXY GLOBALE
                if (result == delegate || method.getReturnType().isAssignableFrom(proxy.getClass())) {
                    return proxy;
                }

                return result;
            } catch (NoSuchMethodException e) {
                // Il delegato corrente non ha questo metodo, passa al prossimo
            }
        }
        throw new UnsupportedOperationException("Nessun delegato implementa il metodo: " + method.getName());
    }
}
