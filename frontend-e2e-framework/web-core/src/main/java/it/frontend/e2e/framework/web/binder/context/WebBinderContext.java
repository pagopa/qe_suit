package it.frontend.e2e.framework.web.binder.context;

import it.frontend.e2e.framework.core.binder.context.BindContext;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import lombok.Getter;

@Getter
public class WebBinderContext extends BindContext {
    private final String url;

    public WebBinderContext(CapabilityScope scope) {
        super(scope);
        this.url = scope.location();
    }
    public WebBinderContext(BindContext bindContext) {
        super(bindContext.getScope());
        this.url = bindContext.getScope().location();
    }

    public static WebBinderContext root(String url) {
        CapabilityScope scope = new CapabilityScope("", url);
        return new WebBinderContext(scope);
    }

    @Override
    public String toString() {
        String parent = super.toString();
        String currentUrl = (url == null) ? "" : url.trim();

        if (currentUrl.isEmpty()) {
            return parent;
        }
        if (parent == null || parent.isBlank()) {
            return "URL: " + currentUrl;
        }
        return parent + ", URL: " + currentUrl;
    }
}
