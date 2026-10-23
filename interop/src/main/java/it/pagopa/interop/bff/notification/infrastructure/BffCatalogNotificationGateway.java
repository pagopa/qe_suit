package it.pagopa.interop.bff.notification.infrastructure;

import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.notification.application.CatalogNotificationGateway;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.utils.async.PollingUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
public class BffCatalogNotificationGateway implements CatalogNotificationGateway {
    private static final Duration TIMEOUT = Duration.ofSeconds(45);
    private static final Duration INTERVAL = Duration.ofSeconds(1);
    private final BffCatalogNotificationRestClient rest;
    private final CurrentUserSession session;
    public BffCatalogNotificationGateway(BffCatalogNotificationRestClient rest, CurrentUserSession session) {
        this.rest = rest;
        this.session = session;
    }
    @Override
    public void ensureEnabled(TenantRef tenant) {
        requireTenant(tenant);
        var config = rest.getTenantConfig().withoutPolling().assertStatusCode(200).get();
        if (config == null || config.getEnabled() == null) {
            throw new IllegalStateException("Configurazione notifiche tenant mancante o non valida: " + tenant.id());
        }
        if (config.getEnabled()) return;
        var response = rest.enableTenantConfig().withoutPolling();
        if (!response.getRaw().isSuccess()) {
            throw new IllegalStateException("Impossibile abilitare le notifiche per il tenant " + tenant.id());
        }
        rest.getTenantConfig().withPolling(raw -> {
            if (!raw.isSuccess()) throw new IllegalStateException("Lettura configurazione notifiche fallita: " + tenant.id());
            var updated = raw.as(it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfig.class);
            return updated != null && Boolean.TRUE.equals(updated.getEnabled());
        }, TIMEOUT, INTERVAL).get();
    }
    @Override
    public void awaitUnreadNotification(TenantRef tenant, EServiceRef eService) {
        requireTenant(tenant);
        AtomicReference<String> lastObserved = new AtomicReference<>("no notification response observed");
        try {
            PollingUtils.pollUntil(
                    () -> rest.getUnreadNotifications(0, 100).withoutPolling().assertStatusCode(200).get(),
                    page -> {
                        if (page == null || page.getResults() == null) {
                            throw new IllegalStateException("Risposta notifiche priva della lista dei risultati");
                        }
                        List<Notification> unread = page.getResults().stream()
                                .filter(notification -> tenant.id().equals(notification.getTenantId())
                                        && notification.getReadAt() == null)
                                .toList();
                        List<String> links = unread.stream().map(Notification::getDeepLink)
                                .filter(link -> link != null && !link.isBlank()).toList();
                        lastObserved.set("first page (offset 0, limit 100): " + unread.size()
                                + " unread notifications for tenant, " + (unread.size() - links.size())
                                + " without a deepLink, " + links.size()
                                + " deepLinks not yet correlated by standalone e-service UUID token");
                        return links.stream().anyMatch(link -> containsEServiceIdToken(link, eService.id()));
                    },
                    TIMEOUT, INTERVAL);
        } catch (IllegalStateException failure) {
            if (!(failure.getCause() instanceof ConditionTimeoutException)) throw failure;
            throw new IllegalStateException("Unable to correlate an unread notification with e-service "
                    + eService.id() + " for tenant " + tenant.id() + "; " + lastObserved.get(), failure);
        }
    }

    static boolean containsEServiceIdToken(String deepLink, java.util.UUID eServiceId) {
        return deepLink != null && eServiceId != null && Pattern.compile(
                "(?i)(?<![a-z0-9_-])" + Pattern.quote(eServiceId.toString()) + "(?![a-z0-9_-])")
                .matcher(deepLink).find();
    }

    private void requireTenant(TenantRef tenant) {
        if (tenant == null || !tenant.id().equals(session.getTenant().getOrganizationId())) {
            throw new IllegalStateException("Notification tenant must match the authenticated tenant");
        }
    }
    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}
