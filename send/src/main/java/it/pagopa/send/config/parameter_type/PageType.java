package it.pagopa.send.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.domain.web.pages.destinatario.ConfigureAddressSendPage;
import it.pagopa.send.domain.web.commons.pages.login.LogoutPage;
import it.pagopa.send.domain.web.commons.pages.login.OneIdPage;
import it.pagopa.send.domain.web.pages.mittente.APIKeyPage;
import it.pagopa.send.domain.web.pages.mittente.CreateNotificationPage;
import it.pagopa.send.domain.web.pages.mittente.DashboardPage;
import it.pagopa.send.domain.web.pages.mittente.NewAPIKeyPage;
import it.pagopa.send.domain.web.pages.mittente.NotificationDetailsPage;
import it.pagopa.send.domain.web.pages.mittente.PlatformStatusPage;
import it.pagopa.send.domain.web.pages.mittente.StatisticsPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.AddressPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.AppStatusPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.DelegationsPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.AddressPGPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.ApiIntegrationPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.DelegatedNotificationPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.NewDelegationPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.NotificationPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.OrganizationAuthorizedRepresentativesPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.OrganizationDelegationsPage;
import it.pagopa.send.domain.web.pages.supporto.BackstageProfilePage;

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
        ADDRESS("Address", AddressPGPage.class),
        API_INTEGRATION("ApiIntegration", ApiIntegrationPage.class),
        DELEGATED_NOTIFICATION("DelegatedNotification", DelegatedNotificationPage.class),
        NEW_DELEGATION("NewDelegation", NewDelegationPage.class),
        NOTIFICATIONS("Notifications", NotificationPage.class),
        ORGANIZATION_AUTHORIZED_REPRESENTATIVES("OrganizationAuthorizedRepresentatives", OrganizationAuthorizedRepresentativesPage.class),
        ORGANIZATION_DELEGATIONS("OrganizationDelegations", OrganizationDelegationsPage.class),
        PLATFORM_STATUS_PAGE_PG("PlatformStatusPagePG", it.pagopa.send.domain.web.pages.destinatario.pg.PlatformStatusPage.class),
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
