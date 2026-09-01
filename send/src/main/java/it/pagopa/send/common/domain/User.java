package it.pagopa.send.common.domain;

public interface User {
    UserType getType();
    String getUsername();
    String getPassword();
    String getOrganization();
    String getTaxId();
    String getName();
    String getFiscalNumber();
    String getOrganizationId();
    String getUid();
}
