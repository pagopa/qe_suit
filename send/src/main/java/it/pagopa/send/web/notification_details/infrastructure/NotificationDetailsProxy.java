package it.pagopa.send.web.notification_details.infrastructure;

import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.common.kernel.domain.UserType;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationDetailsPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationPFPage;
import it.pagopa.send.domain.web.pages.mittente.DashboardPage;
import it.pagopa.send.domain.web.pages.mittente.MittenteNotificationDetailsPage;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationDetailsPage;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class NotificationDetailsProxy implements NotificationDetailsPage {
    private final DashboardPage dashboardPage;
    private final MittenteNotificationDetailsPage mittenteNotificationDetailsPage;
    private final NotificationPFPage notificationPFPage;
    private final NotificationDetailsPFPage notificationDetailsPFPage;
    private final WebBrowserContext webBrowserContext;

    public void goToNotificationDetails() {
        switch (webBrowserContext.getUser().getType()) {
            case PA -> dashboardPage.goToNotificationDetails();
            case PF, PG -> notificationPFPage.goToNotificationDetails();
        }

        webBrowserContext.setCurrentPage(this);
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
