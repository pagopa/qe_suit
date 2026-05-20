package it.pagopa.interop.domain.services.browser;

import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.User;

public interface WebBrowserService {
    boolean hasSessionToken();
    void setSessionToken(String token);
    void login(User user, Tenant tenant);
    void logout();
    boolean isLoggedIn(User user, Tenant tenant);
    String getSnackbarErrorMessage();
}
