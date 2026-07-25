package it.pagopa.interop.web.login.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.infrastructure.suit.component.Button;

@Url("https://uat.selfcare.pagopa.it/dashboard")
public interface DashboardPage extends Page {
    @XPath("//*[@id=\"forward_prod-interop-coll\"]")
    Button interopButton();

    @XPath("/html/body/div[3]/div[3]/div/div/div[2]/div[2]/div/label[3]/span[1]")
    Button devEnvironmentButton();

    @XPath("/html/body/div[3]/div[3]/div/div/div[2]/div[2]/div/label[2]/span[1]")
    Button qaEnvironmentButton();

    @XPath("/html/body/div[3]/div[3]/div/div/div[2]/div[3]/div/div[2]/button")
    Button joinButton();


    default void openInterop(String env){
        interopButton().click();

        switch(env){
            case "dev" -> devEnvironmentButton().click();
            case "qa" -> qaEnvironmentButton().click();
            default -> throw new IllegalArgumentException("Ambiente non supportato: " + env);
        }

        joinButton().click();
    }

}
