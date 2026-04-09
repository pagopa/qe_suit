package it.pagopa.send.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.steps.login.page.OneIdPage;
import it.pagopa.send.steps.mittenti.Dashboard;

public class PageType {

    @ParameterType("LoginPage|Dashboard")
    public Class<? extends Page> page(String page) {
        return switch (page) {
            case "LoginPage" -> OneIdPage.class;
            case "Dashboard" -> Dashboard.class;
            default -> throw new IllegalArgumentException("Invalid page");
        };
    }
}
