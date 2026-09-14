package it.pagopa.send.web.login.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.send.common.user.domain.OrganizationRole;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Costruisce l'oggetto che il portale SelfCare/cittadino si aspetta di trovare nel sessionStorage
 * per considerare l'utente già autenticato, usato dal login "veloce" che salta il flusso SPID.
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

    /**
     * Il portale cittadino (notifichedigitali.it) valida la sessione confrontando i claim del JWT
     * duplicati alla radice dell'oggetto in sessionStorage (non un {@code desired_exp} calcolato
     * a parte): {@link #basePayload} li spalma tutti, qui si aggiungono solo i campi anagrafici
     * SPID (cognome, livello, {@code from_aa}) che il JWT non porta con sé.
     */
    public String buildForRecipient(Recipient recipient, String sessionToken) {
        Map<String, Object> payload = basePayload(recipient, sessionToken);
        payload.put("family_name", recipient.getFamilyName());
        payload.put("level", "L2");
        payload.put("from_aa", false);

        if (recipient.getOrganizationId() != null) {
            Map<String, Object> organization = new LinkedHashMap<>();
            organization.put("id", recipient.getOrganizationId());
            organization.put("name", recipient.getOrganization());
            organization.put("fiscal_code", recipient.getTaxId());
            payload.put("organization", organization);
        }

        return serialize(payload, recipient.getUsername());
    }

    /**
     * Parte comune ai due tipi di sessione: prima i claim del JWT ({@code iat}/{@code exp}/
     * {@code uid}/{@code iss}/{@code aud}/{@code jti}, così come compaiono nel token), poi i campi
     * anagrafici di base, che sovrascrivono eventuali claim omonimi (es. {@code uid}) con il
     * valore anagrafico atteso.
     */
    private Map<String, Object> basePayload(User user, String sessionToken) {
        Map<String, Object> payload = new LinkedHashMap<>(extractClaims(sessionToken));
        payload.put("sessionToken", sessionToken);
        payload.put("name", user.getName());
        payload.put("fiscal_number", user.getFiscalNumber());
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
     * Decodifica il payload (secondo segmento) del sessionToken JWT configurato in
     * {@code token.session.<utente>} e ne restituisce tutti i claim, così da poterli duplicare
     * alla radice dell'oggetto sessionStorage esattamente come li valida il portale.
     */
    private Map<String, Object> extractClaims(String sessionToken) {
        String[] segments = sessionToken.split("\\.");
        if (segments.length < 2) {
            throw new IllegalArgumentException("sessionToken non è un JWT valido: " + sessionToken);
        }

        String payload = segments[1];
        payload += "=".repeat((4 - payload.length() % 4) % 4);

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(payload);
            return objectMapper.readValue(decoded, new TypeReference<LinkedHashMap<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Impossibile leggere i claim dal sessionToken", e);
        }
    }
}
