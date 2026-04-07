package it.frontend.e2e.framework.core.binder.impl;

import it.frontend.e2e.framework.core.binder.DefaultBinder;
import it.frontend.e2e.framework.core.capability.dispatcher.impl.TestDefaultCapabilityDispatcher;

public class TestDefaultBinder extends DefaultBinder {

    public TestDefaultBinder() {
        super(new TestDefaultCapabilityDispatcher());
    }
}
