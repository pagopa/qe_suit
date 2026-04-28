package it.frontend.e2e.framework.web.adapter.decorator;

import it.frontend.e2e.framework.core.adapter.decorator.AbstractAdapterLoggingDecorator;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;

public class WebAdapterLoggingDecorator extends AbstractAdapterLoggingDecorator<XPathSelector, Url, WebPresentationElement> implements  IWebPresentationApiAdapter {

    public WebAdapterLoggingDecorator(IWebPresentationApiAdapter wrappedAdapter) {
        super(wrappedAdapter);
    }
}
