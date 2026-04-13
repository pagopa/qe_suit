package it.pagopa.send.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.steps.login.page.OneIdPage;
import it.pagopa.send.steps.mittenti.Dashboard;
import it.pagopa.send.steps.mittenti.*;

public class PageType {

    @ParameterType("LoginPage|Dashboard|APIKey|NewAPIKey|Statistics|PlatformStatus|NotificationDetails|CreateNotification")
    public Class<? extends Page> page(String page) {
        return switch (page) {
            case "LoginPage" -> OneIdPage.class;
            case "Dashboard" -> Dashboard.class;
            case "NotificationDetails" -> NotificationDetails.class;
            case "CreateNotification" -> CreateNotification.class;
            case "APIKey" -> APIKey.class;
            case "NewAPIKey" -> NewAPIKey.class;
            case "Statistics" -> Statistics.class;
            case "PlatformStatus" -> PlatformStatus.class;
            default -> throw new IllegalArgumentException("Invalid page");
        };
    }
}
