package it.frontend.e2e.framework.web.config;

import it.frontend.e2e.framework.core.capability.handler.ICapabilityHandler;
import it.frontend.e2e.framework.core.config.SuiteConfiguration;
import it.frontend.e2e.framework.core.model.location.resolver.ILocationResolver;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.model.location.Url;
import it.frontend.e2e.framework.web.model.WebPresentationElement;

import java.util.List;

public class WebSuiteConfiguration extends SuiteConfiguration<XPathSelector, Url, WebPresentationElement, IWebPresentationApiAdapter> {

    public WebSuiteConfiguration(List<ICapabilityHandler> capabilityHandlers, List<IWebPresentationApiAdapter> presentationApiAdapters) {
        super(capabilityHandlers, presentationApiAdapters, Url::of);
    }

    public WebSuiteConfiguration(List<ICapabilityHandler> capabilityHandlers, List<IWebPresentationApiAdapter> presentationApiAdapters, ILocationResolver<Url> locationResolver) {
        super(capabilityHandlers, presentationApiAdapters, locationResolver);
    }

}
