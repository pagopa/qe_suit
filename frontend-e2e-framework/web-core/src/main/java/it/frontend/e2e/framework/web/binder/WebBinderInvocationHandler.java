package it.frontend.e2e.framework.web.binder;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler;
import it.frontend.e2e.framework.core.binder.context.BindContext;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher;
import it.frontend.e2e.framework.web.binder.context.WebBinderContext;
import it.frontend.e2e.framework.web.domain.Page;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class WebBinderInvocationHandler extends DefaultBinderInvocationHandler {


    public WebBinderInvocationHandler(BindContext ctx) {
        super(new DefaultCapabilityDispatcher(), ctx);
    }

    @Override
    protected InvocationHandler getInvocationHandlerFor(Method method, Class<?> returnType, BindContext bindContext) {
        BindContext context = buildWebBindContext(method, bindContext);
        return new WebBinderInvocationHandler(context);
    }

    private BindContext buildWebBindContext(Method method, BindContext bindContext) {
        Class<?> returnType = method.getReturnType();

        // Cambio pagina: il return type e una Page, quindi il nuovo contesto deve puntare alla nuova URL.
        if (Page.class.isAssignableFrom(returnType)) {
            return new WebBinderContext(new CapabilityScope(bindContext.getScope().selector(), resolveUrl(returnType)));
        }

        return bindContext;
    }

    private static String resolveUrl(Class<?> type) {
        Url onType = type.getAnnotation(Url.class);
        if (onType != null) return onType.value();

        return "";
    }
}
