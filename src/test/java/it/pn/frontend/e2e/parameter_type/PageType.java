package it.pn.frontend.e2e.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;
import it.pn.frontend.e2e.steps.send.login.page.OneIdPage;
import it.pn.frontend.e2e.steps.send.mittenti.Dashboard;

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
