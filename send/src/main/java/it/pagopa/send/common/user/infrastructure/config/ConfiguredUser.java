package it.pagopa.send.common.user.infrastructure.config;

import it.pagopa.send.common.user.domain.OrganizationRole;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserType;

import java.util.List;

/**
 * Utenza letta dalla configurazione dell'ambiente attivo ({@link UsersProperties}): unica implementazione di
 * {@link User} risolta da {@link PropertiesUserProvider}.
 */
record ConfiguredUser(UserType type, String username, String password, String taxId, String organization,
                      String organizationId, String uid, String denomination, String familyName,
                      String personalTaxId, List<OrganizationRole> roles, String ipaCode, boolean hasGroups,
                      String email) implements User {

    @Override
    public UserType getType() {
        return type;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getOrganization() {
        return organization;
    }

    @Override
    public String getTaxId() {
        return taxId;
    }

    @Override
    public String getName() {
        return denomination;
    }

    @Override
    public String getDenomination() {
        return denomination;
    }

    @Override
    public String getFamilyName() {
        return familyName;
    }

    // come in Recipient: il codice fiscale personale se c'è, altrimenti il taxId (per una PF coincidono)
    @Override
    public String getFiscalNumber() {
        return personalTaxId != null ? personalTaxId : taxId;
    }

    @Override
    public String getPersonalTaxId() {
        return personalTaxId;
    }

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public List<OrganizationRole> getRoles() {
        return roles;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getIpaCode() {
        return ipaCode;
    }

    @Override
    public boolean isHasGroups() {
        return hasGroups;
    }
}
