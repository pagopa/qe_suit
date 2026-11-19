package it.pagopa.interop.common.kernel.context;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.InteropActor;
import it.pagopa.interop.common.kernel.domain.InteropRole;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.springframework.plugin.core.Plugin;

import java.util.Objects;

public interface AuthSession extends Plugin<Channel> {
    void set(InteropActor actor, Tenant tenant);

    void set(Tenant tenant);

    void set(InteropRole role, Tenant tenant);

    InteropActor getActor();

    InteropRole getRole();

    Tenant getTenant();

    /**
     * Captures the current channel's session, including unset fields. Close the snapshot
     * within the same scenario and thread to restore that session without reprovisioning.
     * It does not capture the active channel or other channels' sessions.
     */
    Snapshot snapshot();

    @FunctionalInterface
    interface Snapshot extends AutoCloseable {
        @Override
        void close();

        static Snapshot forCurrentThread(Runnable restore) {
            Objects.requireNonNull(restore, "restore must not be null");
            Thread owner = Thread.currentThread();
            return () -> {
                if (Thread.currentThread() != owner) {
                    throw new IllegalStateException("Auth session snapshot must be restored on its originating thread");
                }
                restore.run();
            };
        }
    }
}
