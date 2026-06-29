package it.pagopa.interop.common.controller;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.common.cucumber.context.ChannelContext;
import it.pagopa.interop.common.cucumber.context.UserContext;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;
import it.pagopa.interop.common.contract.model.shared.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationController {
    private final UserContext userContext;
    private final ChannelContext channelContext;

    @And("un {userRole} di {tenant}")
    @And("un {userRole} del {tenant}")
    public void loginWith(UserRole userRole, Tenant tenant) {
        User user = User.getTenantUser(tenant, userRole);

        if (!userContext.isLoggedIn(user, tenant))
            userContext.set(user, tenant);
    }

    @Given("una sessione HTTP autenticata su {channel}")
    public void setChannel(Channel channel) {
        channelContext.setCurrentChannel(channel);
    }

    @Before("BFF")
    public void setBffChannel() {
        channelContext.setCurrentChannel(Channel.BFF);
    }

}
