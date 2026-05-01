package it.frontend.e2e.framework.web.binder;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler;
import it.frontend.e2e.framework.core.binder.context.BindContext;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.core.utils.TypeUtils;
import it.frontend.e2e.framework.core.utils.XPathResolver;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.binder.context.WebBinderContext;
import it.frontend.e2e.framework.web.config.WebSuiteContext;
import it.frontend.e2e.framework.web.domain.Page;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected List<?> handleList(Method method, Object[] args) {
        Type listArgType = TypeUtils.extractListType(method);
        Class<?> innerType = TypeUtils.resolveClass(listArgType);
        if (innerType == null || !isBindableType(innerType)) {
            throw new IllegalStateException(
                    "List<T>: T deve essere un DomainElement bindable, trovato: " + innerType);
        }

        String childSel = XPathResolver.resolve(method, innerType);
        String fullSel = XPathResolver.compose(getCtx().getScope().selector(), childSel);

        IWebPresentationApiAdapter adapter = WebSuiteContext.getConfiguration()
                .getPresentationApiAdapters().get(0);
        int count = adapter.findElements(new XPathSelector(fullSel))
                .map(List::size)
                .orElse(0);

        List<Object> result = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String indexedSel = "(" + fullSel + ")[" + i + "]";
            CapabilityScope scope = new CapabilityScope(indexedSel, getCtx().getScope().location());
            Object proxy = Proxy.newProxyInstance(
                    innerType.getClassLoader(),
                    new Class<?>[]{innerType},
                    new WebBinderInvocationHandler(new BindContext(scope))
            );
            result.add(proxy);
        }
        return result;
    }

    private static String resolveUrl(Class<?> type) {
        Url onType = type.getAnnotation(Url.class);
        if (onType != null) return onType.value();

        return "";
    }
}
