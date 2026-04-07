package it.frontend.e2e.framework.core.binder;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.binder.context.BindContext;
import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher;
import it.frontend.e2e.framework.core.capability.dispatcher.ICapabilityDispatcher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class DefaultBinder implements IBinder {

    private final ICapabilityDispatcher dispatcher;

    public DefaultBinder() {
        this.dispatcher = new DefaultCapabilityDispatcher();
    }

    public DefaultBinder(ICapabilityDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Capability> T bind(Class<T> type) {
        if (!type.isInterface())
            throw new IllegalArgumentException(type.getName() + " is not an interface.");

        BindContext bindContext = buildBindContext(type);

        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                getInvocationHandler(type, bindContext)
        );
    }

    protected InvocationHandler getInvocationHandler(Class<?> type, BindContext bindContext){
        return new DefaultBinderInvocationHandler(this.dispatcher, bindContext);
    }

    private BindContext buildBindContext(Class<?> type) {
        String rootXPath = resolveTypeXPath(type);
        String location = resolveTypeLocation(type);
        CapabilityScope scope = new CapabilityScope(rootXPath, location);

        return new BindContext(scope);
    }

    protected String resolveTypeXPath(Class<?> iface) {
        XPath onType = iface.getAnnotation(XPath.class);
        if (onType != null) return onType.value();
        return "";
    }

    protected String resolveTypeLocation(Class<?> iface) {
        return "";
    }
}

