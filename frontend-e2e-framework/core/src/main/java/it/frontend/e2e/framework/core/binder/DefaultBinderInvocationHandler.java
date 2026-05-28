package it.frontend.e2e.framework.core.binder;

import it.frontend.e2e.framework.core.binder.context.BindContext;
import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.capability.dispatcher.ICapabilityDispatcher;
import it.frontend.e2e.framework.core.config.SuiteContext;
import it.frontend.e2e.framework.core.logging.ILogger;
import it.frontend.e2e.framework.core.logging.Slf4jLogger;
import it.frontend.e2e.framework.core.model.DomainElement;
import it.frontend.e2e.framework.core.model.collection.PresentationElementList;
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
                case "toString" ->
                        proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy));
                default -> throw new UnsupportedOperationException("Object method not supported: " + method.getName());
            };
        }

        try {
            if (method.isDefault()) return handleDefaultMethod(proxy, method, args);
            if (TypeUtils.isOptionalReturn(method)) return WrapperBinder.bindOptional(this, method, args);
            if (TypeUtils.isListReturn(method)) return bindListProxy(method);

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

    public <T> T resolveCapabilityMethod(Method method, Object[] args, BindContext bindContext) {
        logger.logDebug("Dispatching capability method: " + method.getName() + " | " + bindContext.toString());
        return dispatcher.dispatch(method, args, bindContext.getScope());
    }

    public Object bindRecursive(Method method, Class<?> returnType, boolean optionalBestEffort) {
        //SuiteContext.getConfiguration().getSelectorResolver().resolve(XPathResolver.resolve(method, returnType));
        String childSel = XPathResolver.resolve(method, returnType);

        if (childSel.startsWith("${")) {
            childSel = SuiteContext.getConfiguration().getSelectorResolver().resolve(XPathResolver.resolve(method, returnType)).toString();
        }

        String fullSel;
        logger.logInfo("childSel: " + childSel + " | isAbsolute: " + XPathResolver.isAbsolute(childSel));

        if (XPathResolver.isAbsolute(childSel)) {
            fullSel = childSel;
        } else {
            fullSel = XPathResolver.compose(ctx.getScope().selector(), childSel);
        }

        CapabilityScope scope = new CapabilityScope(fullSel, ctx.getScope().location(), false);
        logger.logInfo("Binding recursive element: " + returnType.getSimpleName() +
                " | From: " + method.getDeclaringClass().getSimpleName() +
                " | Selector: " + fullSel);
        return Proxy.newProxyInstance(
                returnType.getClassLoader(),
                new Class<?>[]{returnType},
                new DefaultBinderInvocationHandler(this.dispatcher, new BindContext(scope), optionalBestEffort)
        );
    }

    private Object bindListProxy(Method method) {
        Class<?> itemType = TypeUtils.getListGenericType(method);

        String childSel = XPathResolver.resolve(method, itemType);

        String fullSel = XPathResolver.isAbsolute(childSel)
                ? childSel
                : XPathResolver.compose(ctx.getScope().selector(), childSel);

        CapabilityScope collectionScope = new CapabilityScope(
                fullSel,
                ctx.getScope().location(),
                true
        );

        return new PresentationElementList<>(
                itemType,
                collectionScope,
                dispatcher
        );
    }

    public boolean isBindableType(Class<?> type) {
        return DomainElement.class.isAssignableFrom(type) || Capability.class.isAssignableFrom(type);
    }

    /**
     * Getters
     */

    public BindContext getCtx() {
        return ctx;
    }

    public ILogger getLogger() {
        return logger;
    }
}
