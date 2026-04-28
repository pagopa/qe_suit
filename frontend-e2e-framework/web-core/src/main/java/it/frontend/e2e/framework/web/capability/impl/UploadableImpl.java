package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;

public class UploadableImpl extends AbstractCapabilityImpl implements Uploadable {

    public UploadableImpl(IWebPresentationApiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void upload(String absolutePath) {
        //adapter.sendText(xPathSelector.get(), absolutePath);
        adapter.sendFile(xPathSelector.get(), absolutePath);
    }
}
