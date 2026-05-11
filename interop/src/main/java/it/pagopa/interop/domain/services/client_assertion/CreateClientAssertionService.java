package it.pagopa.interop.domain.services.client_assertion;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.Purpose;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.utils.JwtBuilderUtils.JwtClaimOverride;
import static it.pagopa.interop.utils.JwtBuilderUtils.applyOverrides;
import static it.pagopa.interop.utils.JwtUtils.calculateKidFromPublicKey;

@Slf4j
@Component
public class CreateClientAssertionService {

    @Value("${interop.auth.client-assertion.audience}")
    private String clientAssertionAudience;

    @Value("${interop.auth.client-assertion.grant_type}")
    private String clientAssertionGrantType;

    @Value("${interop.auth.client-assertion.assertion_type}")
    private String clientAssertionType;


    public String createClientAssertion(Client client, Purpose purpose, List<JwtClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        return createClientAssertion(client, purpose, null, overrides);
    }

    public String createClientAssertion(Client client, Purpose purpose) throws NoSuchAlgorithmException, JsonProcessingException {
        return createClientAssertion(client, purpose, null, List.of());
    }

    public String createClientAssertion(Client client) throws NoSuchAlgorithmException, JsonProcessingException {
        return createClientAssertion(client, null, null, List.of());
    }

    public String createClientAssertion(Client client, List<JwtClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        return createClientAssertion(client, null, null, overrides);
    }

    public String createClientAssertion(Client client, Purpose purpose, KeyPair keyPair) throws NoSuchAlgorithmException, JsonProcessingException {
        return createClientAssertion(client, purpose, keyPair, List.of());
    }

    public String createClientAssertion(Client client, Purpose purpose, KeyPair keyPair, List<JwtClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {

        KeyPair kp = keyPair != null ? keyPair : client.getLastKeyPair();

        JwtBuilder clientAssertionBuilder = getValidClientAssertionBuilder(
                InteropClientType.valueOf(client.getKind().name()),
                client.getId().toString(),
                purpose != null ? purpose.getId().toString() : null,
                kp
        );

        if (!overrides.isEmpty())
            applyOverrides(clientAssertionBuilder, overrides);

        String clientAssertion = clientAssertionBuilder.signWith(kp.getPrivate()).compact();
        log.info("Client assertion: '{}'", clientAssertion);

        return clientAssertion;
    }

    private JwtBuilder getValidClientAssertionBuilder(InteropClientType clientType, String clientId, String purposeId, KeyPair keyPair) throws NoSuchAlgorithmException, JsonProcessingException {
        String rawKid = calculateKidFromPublicKey(keyPair.getPublic());

        JwtBuilder validJwt = Jwts.builder()
                .issuer(clientId)
                .subject(clientId)
                .audience().add(this.clientAssertionAudience).and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(43200)))
                .header().add("kid", rawKid).and();

        if (clientType.equals(InteropClientType.CONSUMER)) {
            validJwt.claim("purposeId", purposeId);
        }

        return validJwt;
    }
}
