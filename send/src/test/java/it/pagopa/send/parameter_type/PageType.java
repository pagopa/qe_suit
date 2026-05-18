package it.pagopa.send.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.steps.destinatari.page.ConfigureAddressSendPage;
import it.pagopa.send.steps.login.page.LogoutPage;
import it.pagopa.send.steps.login.page.OneIdPage;
import it.pagopa.send.steps.mittenti.APIKeyPage;
import it.pagopa.send.steps.mittenti.CreateNotificationPage;
import it.pagopa.send.steps.mittenti.DashboardPage;
import it.pagopa.send.steps.mittenti.NewAPIKeyPage;
import it.pagopa.send.steps.mittenti.NotificationDetailsPage;
import it.pagopa.send.steps.mittenti.PlatformStatusPage;
import it.pagopa.send.steps.mittenti.StatisticsPage;
import it.pagopa.send.steps.destinatari.pf.page.AddressPFPage;
import it.pagopa.send.steps.destinatari.pf.page.AppStatusPFPage;
import it.pagopa.send.steps.destinatari.pf.page.DelegationsPFPage;
import it.pagopa.send.steps.destinatari.pf.page.NotificationPFPage;
import it.pagopa.send.steps.destinatari.pg.page.AddressPage;
import it.pagopa.send.steps.destinatari.pg.page.ApiIntegrationPage;
import it.pagopa.send.steps.destinatari.pg.page.DelegatedNotificationPage;
import it.pagopa.send.steps.destinatari.pg.page.NewDelegationPage;
import it.pagopa.send.steps.destinatari.pg.page.NotificationPage;
import it.pagopa.send.steps.destinatari.pg.page.OrganizationAuthorizedRepresentativesPage;
import it.pagopa.send.steps.destinatari.pg.page.OrganizationDelegationsPage;
import it.pagopa.send.steps.supporto.BackstageProfilePage;

public class PageType {

    public enum PageEnum {
        LOGIN_PAGE("LoginPage", OneIdPage.class),
        LOGOUT_PAGE("LogoutPage", LogoutPage.class),
        DASHBOARD("Dashboard", DashboardPage.class),
        NOTIFICATION_DETAILS("NotificationDetails", NotificationDetailsPage.class),
        CREATE_NOTIFICATION("CreateNotification", CreateNotificationPage.class),
        API_KEY("APIKey", APIKeyPage.class),
        NEW_API_KEY("NewAPIKey", NewAPIKeyPage.class),
        STATISTICS("Statistics", StatisticsPage.class),
        PLATFORM_STATUS("PlatformStatus", PlatformStatusPage.class),
        BACKSTAGE_PROFILE("BackstageProfile", BackstageProfilePage.class),
        ADDRESS("Address", AddressPage.class),
        API_INTEGRATION("ApiIntegration", ApiIntegrationPage.class),
        DELEGATED_NOTIFICATION("DelegatedNotification", DelegatedNotificationPage.class),
        NEW_DELEGATION("NewDelegation", NewDelegationPage.class),
        NOTIFICATIONS("Notifications", NotificationPage.class),
        ORGANIZATION_AUTHORIZED_REPRESENTATIVES("OrganizationAuthorizedRepresentatives", OrganizationAuthorizedRepresentativesPage.class),
        ORGANIZATION_DELEGATIONS("OrganizationDelegations", OrganizationDelegationsPage.class),
        PLATFORM_STATUS_PAGE_PG("PlatformStatusPagePG", it.pagopa.send.steps.destinatari.pg.page.PlatformStatusPage.class),
        ADDRESS_PF("AddressPF", AddressPFPage.class),
        APP_STATUS_PF("AppStatusPF", AppStatusPFPage.class),
        DELEGATIONS_PF("DelegationsPF", DelegationsPFPage.class),
        NOTIFICATION_PF("NotificationPF", NotificationPFPage.class),
        CONFIGURE_ADDRESS_SEND_PAGE("ConfigureAddressSendPage",ConfigureAddressSendPage .class);

        private final String pageName;
        private final Class<? extends Page> pageClass;

        PageEnum(String pageName, Class<? extends Page> pageClass) {
            this.pageName = pageName;
            this.pageClass = pageClass;
        }

        public static Class<? extends Page> fromName(String name) {
            for (PageEnum page : values()) {
                if (page.pageName.equals(name)) {
                    return page.pageClass;
                }
            }
            throw new IllegalArgumentException("Unknown page: " + name);
        }
    }

    @ParameterType("[A-Za-z]+")
    public Class<? extends Page> page(String page) {
        return PageEnum.fromName(page);
    }
}
