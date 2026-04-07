package it.frontend.e2e.framework.core.binder;

import it.frontend.e2e.framework.core.binder.context.BindContext;
import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.capability.dispatcher.ICapabilityDispatcher;
import it.frontend.e2e.framework.core.logging.ILogger;
import it.frontend.e2e.framework.core.logging.Slf4jLogger;
import it.frontend.e2e.framework.core.model.DomainElement;
import it.frontend.e2e.framework.core.utils.FallbackUtils;
import it.frontend.e2e.framework.core.utils.TypeUtils;
import it.frontend.e2e.framework.core.utils.WrapperBinder;
import it.frontend.e2e.framework.core.utils.XPathResolver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DefaultBinderInvocationHandler implements InvocationHandler {

    protected final ICapabilityDispatcher dispatcher;
    private final BindContext ctx;
    private final boolean shouldSuppressExceptionForOptionalWrapper;
    private final ILogger logger = new Slf4jLogger();

    public DefaultBinderInvocationHandler(ICapabilityDispatcher dispatcher) {
        this(dispatcher, BindContext.root(), false);
    }

    public DefaultBinderInvocationHandler(ICapabilityDispatcher dispatcher, BindContext ctx) {
        this(dispatcher, ctx, false);
    }

    private DefaultBinderInvocationHandler(ICapabilityDispatcher dispatcher, BindContext ctx, boolean shouldSuppressExceptionForOptionalWrapper) {
        this.dispatcher = dispatcher;
        this.ctx = ctx;
        this.shouldSuppressExceptionForOptionalWrapper = shouldSuppressExceptionForOptionalWrapper;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return switch (method.getName()) {
                case "equals" -> proxy == args[0];
                case "hashCode" -> System.identityHashCode(proxy);
                case "toString" -> proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy));
                default -> throw new UnsupportedOperationException("Object method not supported: " + method.getName());
            };
        }
        try {
            if (method.isDefault()) return handleDefaultMethod(proxy, method, args);
            if (TypeUtils.isOptionalReturn(method)) return WrapperBinder.bindOptional(this, method, args);

            Class<?> rt = method.getReturnType();
            if (isBindableType(rt)) return bindRecursive(method, rt, shouldSuppressExceptionForOptionalWrapper);

            return resolveCapabilityMethod(method, args, ctx);
        } catch (RuntimeException ex) {
            if (!shouldSuppressExceptionForOptionalWrapper) {
                throw ex;
            }
            logger.logDebug("Best-effort Optional invocation failed for method: " + method.getName() + " -> fallback");
            return FallbackUtils.fallbackValue(method.getReturnType());
        }
    }

    protected Object handleDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        return InvocationHandler.invokeDefault(proxy, method, args);
    }

    protected InvocationHandler getInvocationHandlerFor(Method method, Class<?> returnType, BindContext bindContext) {
        return new DefaultBinderInvocationHandler(this.dispatcher, bindContext, shouldSuppressExceptionForOptionalWrapper);
    }

    public <T> T resolveCapabilityMethod(Method method, Object[] args, BindContext bindContext ) {
        logger.logDebug("Dispatching capability method: " + method.getName() + " | " + bindContext.toString());
        return dispatcher.dispatch(method, args, bindContext.getScope());
    }

    public Object bindRecursive(Method method, Class<?> returnType, boolean optionalBestEffort) {
        String childSel = XPathResolver.resolve(method, returnType);
        String fullSel = XPathResolver.compose(ctx.getScope().selector(), childSel);
        CapabilityScope scope = new CapabilityScope(fullSel, ctx.getScope().location());
        logger.logInfo("Binding recursive element: " + returnType.getSimpleName() +
                " | From: " + method.getDeclaringClass().getSimpleName() +
                " | Selector: " + fullSel);
        return Proxy.newProxyInstance(
                returnType.getClassLoader(),
                new Class<?>[]{returnType},
                new DefaultBinderInvocationHandler(this.dispatcher, new BindContext(scope), optionalBestEffort)
        );
    }

    public boolean isBindableType(Class<?> type) {
        return DomainElement.class.isAssignableFrom(type) || Capability.class.isAssignableFrom(type);
    }

    /** Getters  */

    public BindContext getCtx() { return ctx; }

    public ILogger getLogger() { return logger; }
}
