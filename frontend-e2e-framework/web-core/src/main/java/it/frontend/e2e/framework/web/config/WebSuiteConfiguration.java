package it.frontend.e2e.framework.web.config;

import it.frontend.e2e.framework.core.capability.handler.ICapabilityHandler;
import it.frontend.e2e.framework.core.config.SuiteConfiguration;
import it.frontend.e2e.framework.core.model.location.resolver.ILocationResolver;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.core.model.selector.resolver.ISelectorResolver;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;

import java.util.List;

public class WebSuiteConfiguration extends SuiteConfiguration<XPathSelector, Url, WebPresentationElement, IWebPresentationApiAdapter> {

    public WebSuiteConfiguration(List<ICapabilityHandler> capabilityHandlers, List<IWebPresentationApiAdapter> presentationApiAdapters) {
        super(capabilityHandlers, presentationApiAdapters, Url::of, XPathSelector::new);
    }

    public WebSuiteConfiguration(List<ICapabilityHandler> capabilityHandlers, List<IWebPresentationApiAdapter> presentationApiAdapters, ILocationResolver<Url> locationResolver, ISelectorResolver<XPathSelector> selectorResolver) {
        super(capabilityHandlers, presentationApiAdapters, locationResolver, selectorResolver);
    }

}
