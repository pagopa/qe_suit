package it.pagopa.send.common.user.domain;

import java.util.List;

public interface User {
    UserType getType();
    String getUsername();
    String getPassword();
    String getOrganization();
    String getTaxId();
    String getName();
    String getDenomination();
    String getFamilyName();
    String getFiscalNumber();
    String getPersonalTaxId();
    String getOrganizationId();
    String getUid();
    List<OrganizationRole> getRoles();
    String getEmail();
    String getIpaCode();
    boolean isHasGroups();

    /**
     * Chiave del session token dell'utenza ({@code token.session.<chiave>}): di norma lo username, oppure una chiave
     * propria quando lo stesso utente SPID accede con un altro ente e ha quindi un token diverso.
     */
    String getSessionTokenKey();
}
