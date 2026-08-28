package it.pagopa.send.web.login.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.send.common.kernel.domain.OrganizationRole;
import it.pagopa.send.common.kernel.domain.Recipient;
import it.pagopa.send.common.kernel.domain.Tenant;
import it.pagopa.send.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Costruisce l'oggetto che il portale SelfCare si aspetta di trovare nel sessionStorage per
 * considerare l'utente già autenticato, usato dal login "veloce" che salta il flusso SPID.
 * La forma richiesta per un Tenant (organization con roles/ipaCode/hasGroups, email, family_name)
 * non è ancora stata verificata per un Recipient: finché non lo sarà, {@link #buildForRecipient}
 * resta un sottoinsieme più semplice.
 */
@Component
@RequiredArgsConstructor
public class SelfCareSessionPayloadFactory {

    private final ObjectMapper objectMapper;

    public String buildForTenant(Tenant tenant, String sessionToken) {
        Map<String, Object> payload = basePayload(tenant, sessionToken);
        payload.put("email", tenant.getEmail());
        payload.put("family_name", tenant.getFamilyName());

        Map<String, Object> organization = new LinkedHashMap<>();
        organization.put("id", tenant.getOrganizationId());
        organization.put("name", tenant.getOrganization());
        organization.put("roles", tenant.getRoles().stream().map(this::toRoleMap).toList());
        organization.put("fiscal_code", tenant.getTaxId());
        organization.put("ipaCode", tenant.getIpaCode());
        organization.put("hasGroups", tenant.isHasGroups());
        payload.put("organization", organization);

        return serialize(payload, tenant.getUsername());
    }

    public String buildForRecipient(Recipient recipient, String sessionToken) {
        Map<String, Object> payload = basePayload(recipient, sessionToken);

        if (recipient.getOrganizationId() != null) {
            Map<String, Object> organization = new LinkedHashMap<>();
            organization.put("id", recipient.getOrganizationId());
            organization.put("name", recipient.getOrganization());
            organization.put("fiscal_code", recipient.getTaxId());
            payload.put("organization", organization);
        }

        return serialize(payload, recipient.getUsername());
    }

    private Map<String, Object> basePayload(User user, String sessionToken) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("desired_exp", extractExpiry(sessionToken));
        payload.put("name", user.getName());
        payload.put("fiscal_number", user.getFiscalNumber());
        payload.put("sessionToken", sessionToken);
        payload.put("uid", user.getUid());
        return payload;
    }

    private Map<String, String> toRoleMap(OrganizationRole role) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("partyRole", role.partyRole());
        map.put("role", role.role());
        return map;
    }

    private String serialize(Map<String, Object> payload, String username) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Impossibile serializzare la sessione self-care per " + username, e);
        }
    }

    /**
     * Il portale considera la sessione scaduta se manca {@code desired_exp}: lo ricaviamo dal claim
     * {@code exp} del sessionToken configurato, invece di tenerlo fisso, così resta coerente ogni
     * volta che il token viene rigenerato/rinnovato in application-<profilo>.yaml.
     */
    private long extractExpiry(String sessionToken) {
        String[] segments = sessionToken.split("\\.");
        if (segments.length < 2) {
            throw new IllegalArgumentException("sessionToken non è un JWT valido: " + sessionToken);
        }

        String payload = segments[1];
        payload += "=".repeat((4 - payload.length() % 4) % 4);

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(payload);
            JsonNode claims = objectMapper.readTree(decoded);
            return claims.get("exp").asLong();
        } catch (IOException e) {
            throw new RuntimeException("Impossibile leggere il claim exp dal sessionToken", e);
        }
    }
}
