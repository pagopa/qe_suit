package it.pagopa.send.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.steps.login.page.OneIdPage;
import it.pagopa.send.steps.mittenti.DashboardPage;
import it.pagopa.send.steps.mittenti.*;
import it.pagopa.send.steps.supporto.BackstageProfilePage;

public class PageType {

    @ParameterType("LoginPage|Dashboard|APIKey|NewAPIKey|Statistics|PlatformStatus|NotificationDetails|CreateNotification|BackstageProfile")
    public Class<? extends Page> page(String page) {
        return switch (page) {
            case "LoginPage" -> OneIdPage.class;
            case "Dashboard" -> DashboardPage.class;
            case "NotificationDetails" -> NotificationDetailsPage.class;
            case "CreateNotification" -> CreateNotificationPage.class;
            case "APIKey" -> APIKeyPage.class;
            case "NewAPIKey" -> NewAPIKeyPage.class;
            case "Statistics" -> StatisticsPage.class;
            case "PlatformStatus" -> PlatformStatusPage.class;
            case "BackstageProfile" -> BackstageProfilePage.class;
            default -> throw new IllegalArgumentException("Invalid page");
        };
    }
}
