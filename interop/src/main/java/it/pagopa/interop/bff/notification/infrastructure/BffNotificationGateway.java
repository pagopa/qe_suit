package it.pagopa.interop.bff.notification.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.notification.application.NotificationGateway;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notifications;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfigUpdateSeed;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BffNotificationGateway implements NotificationGateway {
    private static final Duration TIMEOUT = Duration.ofSeconds(45);
    private static final Duration INTERVAL = Duration.ofSeconds(1);
    private final BffNotificationRestClient rest;
    private final BffNotificationRequestFactory requestFactory;

    @Override
    public void enableNotification(Tenant tenant) {
        if (!hasTeantInAppNotificationAllEnabled(tenant))
            enableAllTenantInAppNotification(tenant);
    }

    @Override
    public void enableAllInAppNotification(Tenant tenant, User user) {
        if (!hasUserInAppNotificationAllEnabled(tenant, user))
            enableAllUserInAppNotification(tenant, user);
    }

    @Override
    public void awaitUnreadNotification(Tenant tenant, User user, EServiceRef eService) {
        AtomicReference<String> lastObserved = new AtomicReference<>("no notification response observed");
        try {
            PollingUtils.pollUntil(
                    () -> getUnreadNotifications(tenant, user),
                    page -> {
                        if (page == null || page.getResults() == null)
                            throw new IllegalStateException("Risposta notifiche priva della lista dei risultati");

                        List<Notification> unread = page.getResults().stream()
                                .filter(notification -> tenant.getOrganizationId().equals(notification.getTenantId()) && user.getUserId().equals(notification.getUserId()) && notification.getReadAt() == null)
                                .toList();
                        List<String> links = unread.stream().map(Notification::getDeepLink)
                                .filter(link -> link != null && !link.isBlank()).toList();
                        lastObserved.set("first page (offset 0, limit 50): " + unread.size()
                                + " unread notifications for tenant, " + (unread.size() - links.size())
                                + " without a deepLink, " + links.size()
                                + " deepLinks not yet correlated by standalone e-service UUID token");
                        return links.stream().anyMatch(link -> containsEServiceIdToken(link, eService.id()));
                    },
                    TIMEOUT, INTERVAL);
        } catch (IllegalStateException failure) {
            if (!(failure.getCause() instanceof ConditionTimeoutException)) throw failure;
            throw new IllegalStateException("Unable to correlate an unread notification with e-service "
                    + eService.id() + " for tenant " + tenant.getAlias() + " and user " + user.getUsername() + "; " + lastObserved.get(), failure);
        }
    }

    static boolean containsEServiceIdToken(String deepLink, java.util.UUID eServiceId) {
        return deepLink != null && eServiceId != null && Pattern.compile(
                        "(?i)(?<![a-z0-9_-])" + Pattern.quote(eServiceId.toString()) + "(?![a-z0-9_-])")
                .matcher(deepLink).find();
    }

    private Notifications getUnreadNotifications(Tenant tenant, User user) {
        return rest.getNotifications(true, 0, 50).withPolling(PollingStrategy.UNTIL_SUCCESS).get();
    }

    private void enableAllUserInAppNotification(Tenant tenant, User user) {

        var config = rest.getUserNotificationConfig()
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        if (config == null) {
            throw new IllegalStateException(
                    "Configurazione notifiche utente mancante: " + user);
        }

        var enabled = requestFactory.allEnabledInAppConfig();

        //TODO questo deve passare allo use case
        if(user.getRole() != UserRole.REVIEWER){
            enabled.purposeRiskAnalysisAssignmentStatusToReviewer(false);
            enabled.purposePublishedWithRiskAnalysisToReviewer(false);
        }

        if (Boolean.TRUE.equals(config.getInAppNotificationPreference()) && enabled.equals(config.getInAppConfig()))
            return;


        var seed = new UserNotificationConfigUpdateSeed()
                .inAppNotificationPreference(true)
                .inAppConfig(enabled)
                .emailNotificationPreference(config.getEmailNotificationPreference())
                .emailDigestPreference(config.getEmailDigestPreference())
                .emailConfig(config.getEmailConfig());

        rest.updateUserNotificationConfig(seed)
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        PollingUtils.pollUntil(() -> hasUserInAppNotificationAllEnabled(tenant, user),
                Boolean.TRUE::equals, TIMEOUT, INTERVAL);

    }

    private void enableAllTenantInAppNotification(Tenant tenant) {
        rest.updateTenantNotificationConfig(new TenantNotificationConfigUpdateSeed().enabled(true))
                .withPolling(PollingStrategy.UNTIL_SUCCESS).get();

        PollingUtils.pollUntil(() -> hasTeantInAppNotificationAllEnabled(tenant),
                Boolean.TRUE::equals, TIMEOUT, INTERVAL);
    }

    private boolean hasUserInAppNotificationAllEnabled(Tenant tenant, User user) {
        var config = rest.getUserNotificationConfig()
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        if (config == null)
            throw new IllegalStateException(
                    "Configurazione notifiche utente mancante: " + user + " per tenant " + tenant.getAlias());

        var allEnabledInAppConfig = requestFactory.allEnabledInAppConfig();

        //TODO : da portare in use case
        if(user.getRole() != UserRole.REVIEWER){
            allEnabledInAppConfig.purposeRiskAnalysisAssignmentStatusToReviewer(false);
            allEnabledInAppConfig.purposePublishedWithRiskAnalysisToReviewer(false);
        }

        return Boolean.TRUE.equals(config.getInAppNotificationPreference())
                && allEnabledInAppConfig.equals(config.getInAppConfig());
    }

    private boolean hasTeantInAppNotificationAllEnabled(Tenant tenant) {
        var config = rest.getTenantNotificationConfig().withPolling(PollingStrategy.UNTIL_SUCCESS).get();
        if (config == null)
            throw new IllegalStateException("Configurazione notifiche tenant mancante o non valida: " + tenant.getAlias());

        return Boolean.TRUE.equals(config.getEnabled());
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}
