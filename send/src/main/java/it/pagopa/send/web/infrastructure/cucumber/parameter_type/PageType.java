package it.pagopa.send.web.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.campagne.infrastructure.page.CampaignsPage;
import it.pagopa.send.web.infrastructure.page.ConfigureAddressSendPage;
import it.pagopa.send.web.login.infrastructure.page.LogoutPage;
import it.pagopa.send.web.login.infrastructure.page.OneIdPage;
import it.pagopa.send.web.mittente.infrastructure.page.APIKeyPage;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.mittente.infrastructure.page.NewAPIKeyPage;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.send.web.mittente.infrastructure.page.PlatformStatusPage;
import it.pagopa.send.web.mittente.infrastructure.page.StatisticsPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AddressPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AppStatusPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DelegationsPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationPFPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.AddressPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.ApiIntegrationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DelegatedNotificationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NewDelegationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.OrganizationAuthorizedRepresentativesPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.OrganizationDelegationsPage;
import it.pagopa.send.web.supporto.infrastructure.page.BackstageProfilePage;

public class PageType {

    public enum PageEnum {
        LOGIN_PAGE("LoginPage", OneIdPage.class),
        CAMPAIGNS_PAGE("CampaignsPage", CampaignsPage.class),
        LOGOUT_PAGE("LogoutPage", LogoutPage.class),
        DASHBOARD("Dashboard", DashboardPage.class),
        NOTIFICATION_DETAILS("NotificationDetails", MittenteNotificationDetailsPage.class),
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
        PLATFORM_STATUS_PAGE_PG("PlatformStatusPagePG", it.pagopa.send.web.destinatario_pg.infrastructure.page.PlatformStatusPage.class),
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
