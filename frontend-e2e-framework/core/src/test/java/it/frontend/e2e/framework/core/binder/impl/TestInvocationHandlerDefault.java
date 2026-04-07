package it.frontend.e2e.framework.core.binder.impl;

import it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler;
import it.frontend.e2e.framework.core.capability.dispatcher.ICapabilityDispatcher;

public class TestInvocationHandlerDefault extends DefaultBinderInvocationHandler {
    public TestInvocationHandlerDefault(ICapabilityDispatcher dispatcher) {
        super(dispatcher);
    }
}
