package it.pagopa.send.common.kernel.security;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FakeAuthenticator implements IAuthenticator{
    private String role;
    private String pa;

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
