package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.web.capability.core.RequestCapturingClickable;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;

import java.util.List;

public class ClickableImpl extends AbstractCapabilityImpl implements RequestCapturingClickable {

    public ClickableImpl(IWebPresentationApiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void click() {
        adapter.click(xPathSelector.get());
    }

    @Override
    public List<String> clickAndGetRequestUrls() {
        return adapter.clickAndGetRequestUrls(xPathSelector.get());
    }
}