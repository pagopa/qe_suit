package it.pagopa.send.web.notification_details.infrastructure;

import it.pagopa.send.common.domain.UserType;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationDetailsPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationPFPage;
import it.pagopa.send.domain.web.pages.mittente.DashboardPage;
import it.pagopa.send.domain.web.pages.mittente.MittenteNotificationDetailsPage;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.AttachmentSection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationStatusSection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationSummarySection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.PaymentSection;
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
        return webBrowserContext.getCurrentUser().getType() == UserType.PA
                ? dashboardPage
                : notificationPFPage;
    }

    private NotificationDetailsPage currentDetailsPage() {
        return webBrowserContext.getCurrentUser().getType() == UserType.PA
                ? mittenteNotificationDetailsPage
                : notificationDetailsPFPage;
    }

    @Override
    public NotificationSummarySection notificationSummarySection() {
        return currentDetailsPage().notificationSummarySection();
    }

    @Override
    public PaymentSection paymentSection() {
        return currentDetailsPage().paymentSection();
    }

    @Override
    public AttachmentSection attachmentSection() {
        return currentDetailsPage().attachmentSection();
    }

    @Override
    public NotificationStatusSection notificationStatusSection() {
        return currentDetailsPage().notificationStatusSection();
    }

    @Override
    public void assertLoaded() {
        currentDetailsPage().assertLoaded();
    }

    @Override
    public void navigateTo(String... pathParams) {
        currentDetailsPage().navigateTo();
    }
}
