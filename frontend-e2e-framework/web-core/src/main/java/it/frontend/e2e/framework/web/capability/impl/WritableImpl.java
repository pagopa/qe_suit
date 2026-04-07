package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.core.assertion.AssertionAction;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.model.WebPresentationElement;

public class WritableImpl<T> extends AbstractCapabilityImpl implements Writable<T> {

    public WritableImpl(IWebPresentationApiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void write(T value) {
        adapter.sendText(xPathSelector.get(), value.toString());
    }

    @Override
    public void writeAndAssert(T value) {
        adapter.sendTextAndAssert(xPathSelector.get(), value.toString());
    }

    @Override
    public void writeAndAssert(T value, AssertionAction<WebPresentationElement> assertionAction) {
        adapter.sendTextAndAssert(xPathSelector.get(), value.toString(), assertionAction);
    }
}
