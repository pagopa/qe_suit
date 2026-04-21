package it.pagopa.send.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.steps.login.page.OneIdPage;
import it.pagopa.send.steps.mittenti.DashboardPage;
import it.pagopa.send.steps.mittenti.*;
import it.pagopa.send.steps.pf.page.AddressPFPage;
import it.pagopa.send.steps.pf.page.AppStatusPFPage;
import it.pagopa.send.steps.pf.page.DelegationsPFPage;
import it.pagopa.send.steps.pf.page.NotificationPFPage;
import it.pagopa.send.steps.pg.page.AddressPage;
import it.pagopa.send.steps.pg.page.ApiIntegrationPage;
import it.pagopa.send.steps.pg.page.DelegatedNotificationPage;
import it.pagopa.send.steps.pg.page.NewDelegationPage;
import it.pagopa.send.steps.pg.page.NotificationPage;
import it.pagopa.send.steps.pg.page.OrganizationAuthorizedRepresentativesPage;
import it.pagopa.send.steps.pg.page.OrganizationDelegationsPage;
import it.pagopa.send.steps.supporto.BackstageProfilePage;

import java.util.HashMap;
import java.util.Map;

public class PageType {

    protected static final Map<String, Class<? extends Page>> PAGE_MAP = new HashMap<>(Map.ofEntries(
            Map.entry("LoginPage", OneIdPage.class),
            Map.entry("Dashboard", DashboardPage.class),
            Map.entry("NotificationDetails", NotificationDetailsPage.class),
            Map.entry("CreateNotification", CreateNotificationPage.class),
            Map.entry("APIKey", APIKeyPage.class),
            Map.entry("NewAPIKey", NewAPIKeyPage.class),
            Map.entry("Statistics", StatisticsPage.class),
            Map.entry("PlatformStatus", PlatformStatusPage.class),
//            SUPPORTO PAGES
            Map.entry("BackstageProfile", BackstageProfilePage.class),
//            PG PAGES
            Map.entry("Address", AddressPage.class),
            Map.entry("ApiIntegration", ApiIntegrationPage.class),
            Map.entry("DelegatedNotification", DelegatedNotificationPage.class),
            Map.entry("NewDelegation", NewDelegationPage.class),
            Map.entry("Notifications", NotificationPage.class),
            Map.entry("OrganizationAuthorizedRepresentatives", OrganizationAuthorizedRepresentativesPage.class),
            Map.entry("OrganizationDelegations", OrganizationDelegationsPage.class),
            Map.entry("PlatformStatusPagePG", it.pagopa.send.steps.pg.page.PlatformStatusPage.class),
//            PF PAGES
            Map.entry("AddressPF", AddressPFPage.class),
            Map.entry("AppStatusPF", AppStatusPFPage.class),
            Map.entry("DelegationsPF", DelegationsPFPage.class),
            Map.entry("NotificationPF", NotificationPFPage.class)
    ));

    private static final String PAGE_REGEX =
            "LoginPage|Dashboard|NotificationDetails|CreateNotification|APIKey|NewAPIKey|Statistics|PlatformStatus|" +
                    "BackstageProfile|Address|ApiIntegration|DelegatedNotification|NewDelegation|Notifications|" +
                    "OrganizationAuthorizedRepresentatives|OrganizationDelegations|PlatformStatusPagePG" +
                    "|AddressPF|AppStatusPF|DelegationsPF|NotificationPF";

    @ParameterType(PAGE_REGEX)
    public Class<? extends Page> page(String page) {
        return PAGE_MAP.get(page);
    }
}
