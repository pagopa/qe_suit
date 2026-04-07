package it.frontend.e2e.framework.web.binder;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.core.binder.DefaultBinder;
import it.frontend.e2e.framework.core.binder.context.BindContext;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.web.binder.context.WebBinderContext;
import it.frontend.e2e.framework.web.config.WebSuiteContext;

import java.lang.reflect.InvocationHandler;

public class WebBinder extends DefaultBinder {
    @Override
    protected InvocationHandler getInvocationHandler(Class<?> type, BindContext bindContext) {
        WebBinderContext webBinderContext = buildWebBindContext(type, bindContext);
        return new WebBinderInvocationHandler(webBinderContext);
    }

    private WebBinderContext buildWebBindContext(Class<?> type, BindContext bindContext) {
        final String encodedUrl = bindContext.getScope().location();
        final String resolvedUrl = resolveUrl(encodedUrl);

        CapabilityScope newScope = new CapabilityScope(bindContext.getScope().selector(), resolvedUrl);
        WebBinderContext webBinderContext = new WebBinderContext(new BindContext(newScope));

        return new WebBinderContext(webBinderContext);
    }

    @Override
    protected String resolveTypeLocation(Class<?> iface) {
        Url onType = iface.getAnnotation(Url.class);
        if (onType != null) return onType.value();

        return "";
    }

    private String resolveUrl(String url){
        return WebSuiteContext.getConfiguration().getLocationResolver().resolve(url).getUrl();
    }
}
