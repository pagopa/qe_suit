package it.frontend.e2e.framework.web;

import it.frontend.e2e.framework.core.IPresentationGateway;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.binder.WebBinder;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class WebPresentationGateway implements IPresentationGateway<XPathSelector, Url, WebPresentationElement> {
    @Delegate
    private final IWebPresentationApiAdapter adapter;

    @Delegate
    private final WebBinder binder;
}
