package it.frontend.e2e.framework.core.binder;

import it.frontend.e2e.framework.core.capability.Capability;

public interface IBinder {
    <T extends Capability> T bind(Class<T> type);
}
