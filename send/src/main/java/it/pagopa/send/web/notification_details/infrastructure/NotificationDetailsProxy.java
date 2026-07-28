package it.pagopa.send.web.notification_details.infrastructure;

import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.common.kernel.domain.UserType;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationDetailsPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationPFPage;
import it.pagopa.send.domain.web.pages.mittente.DashboardPage;
import it.pagopa.send.domain.web.pages.mittente.MittenteNotificationDetailsPage;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationDetailsPage;
import it.pagopa.send.web.notification_search.infrastructure.suit.NotificationSearchPage;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class NotificationDetailsProxy implements NotificationDetailsPage, NotificationSearchPage {
    private final DashboardPage dashboardPage;
    private final MittenteNotificationDetailsPage mittenteNotificationDetailsPage;
    private final NotificationPFPage notificationPFPage;
    private final NotificationDetailsPFPage notificationDetailsPFPage;
    private final WebBrowserContext webBrowserContext;

    @Override
    public void goToNotificationDetails() {
        currentSearchPage().goToNotificationDetails();
        webBrowserContext.setCurrentPage(this);
    }

    @Override
    public void searchNotification(Map<String, String> searchParams) {
        currentSearchPage().searchNotification(searchParams);
    }

    private NotificationSearchPage currentSearchPage() {
        return webBrowserContext.getUser().getType() == UserType.PA
                ? dashboardPage
                : notificationPFPage;
    }

    private NotificationDetailsPage current() {
        return webBrowserContext.getUser().getType() == UserType.PA
                ? mittenteNotificationDetailsPage
                : notificationDetailsPFPage;
    }

    @Override
    public Component notificationSummarySection() {
        return current().notificationSummarySection();
    }

    @Override
    public Component paymentSection() {
        return current().paymentSection();
    }

    @Override
    public Component attachmentSection() {
        return current().attachmentSection();
    }

    @Override
    public Component notificationStatusSection() {
        return current().notificationStatusSection();
    }

    @Override
    public void assertLoaded() {
        current().assertLoaded();
    }

    @Override
    public void navigateTo() {
        current().navigateTo();
    }
}
