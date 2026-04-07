package it.frontend.e2e.framework.core.capability.dispatcher;

import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.capability.handler.ICapabilityHandler;
import it.frontend.e2e.framework.core.config.SuiteContext;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;

@Getter
public class DefaultCapabilityDispatcher implements ICapabilityDispatcher {

    protected final List<ICapabilityHandler> handlers;

    @SuppressWarnings("unchecked")
    public DefaultCapabilityDispatcher() {
        this.handlers = SuiteContext.getConfiguration().getCapabilityHandlers();
    }

    public DefaultCapabilityDispatcher(List<ICapabilityHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public <T> T dispatch(Method method, Object[] args, CapabilityScope scope) {
        return handlers.stream()
                .filter(h -> h.canHandle(method))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No handler for " + method.getDeclaringClass().getName())
                )
                .handle(method, args, scope);
    }
}
