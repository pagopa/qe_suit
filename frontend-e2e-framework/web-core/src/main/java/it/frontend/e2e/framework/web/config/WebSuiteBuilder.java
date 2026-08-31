package it.frontend.e2e.framework.web.config;

import it.frontend.e2e.framework.core.capability.handler.ICapabilityHandler;
import it.frontend.e2e.framework.core.model.location.resolver.ILocationResolver;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.core.model.selector.resolver.ISelectorResolver;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.binder.WebBinder;
import it.frontend.e2e.framework.web.capability.handler.factory.WebCapabilityHandlerFactory;
import it.frontend.e2e.framework.web.model.location.Url;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class WebSuiteBuilder {

    private Supplier<IWebPresentationApiAdapter> adapterSupplier = () -> { throw new IllegalStateException("Adapter supplier non configurato"); };
    private ILocationResolver<Url> locationResolver = Url::of;
    private ISelectorResolver<XPathSelector> selectorResolver = XPathSelector::of;
    private final List<ICapabilityHandler> handlers = new ArrayList<>();

    public static WebSuiteBuilder builder() {
        return new WebSuiteBuilder();
    }

    private WebSuiteBuilder() {}

    public WebSuiteBuilder withAdapter(Supplier<IWebPresentationApiAdapter> adapterSupplier) {
        this.adapterSupplier = Objects.requireNonNull(adapterSupplier);
        return this;
    }

    public WebSuiteBuilder addHandlers(ICapabilityHandler... handlers) {
        this.handlers.addAll(List.of(handlers));
        return this;
    }

    public WebSuiteBuilder withLocationResolver(ILocationResolver<Url> locationResolver) {
        this.locationResolver = Objects.requireNonNull(locationResolver);
        return this;
    }

    public WebSuiteBuilder withSelectorResolver(ISelectorResolver<XPathSelector> selectorResolver) {
        this.selectorResolver = Objects.requireNonNull(selectorResolver);
        return this;
    }

    public WebPresentationGateway build() {
        IWebPresentationApiAdapter adapter = Objects.requireNonNull(adapterSupplier.get());

        // Costruisco gli handlers
        var defaultHandlers = WebCapabilityHandlerFactory.defaults()
                .stream()
                .map(factory -> factory.create(adapter))
                .toList();

        handlers.addAll(defaultHandlers);

        // Costruisco la configurazione
        WebSuiteConfiguration configuration = new WebSuiteConfiguration(handlers, List.of(adapter), locationResolver, selectorResolver);

        // Costruisco il contesto globale
        WebSuiteContext.initialize(configuration);

        // Ritorno il gateway
        WebBinder binder = new WebBinder();

        return new WebPresentationGateway(adapter, binder);
    }
}



