package it.pagopa.interop.domain.services.browser;

public interface WebBrowserService {
    boolean hasSessionToken();
    void setSessionToken(String token);
    String getSnackbarErrorMessage();
}
