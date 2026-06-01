package it.pagopa.interop.common.service;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import it.pagopa.interop.common.domain.enums.InteropClientType;
import it.pagopa.interop.common.domain.model.Client;
import it.pagopa.interop.common.domain.model.Purpose;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.pagopa.interop.common.utils.JwtBuilderUtils.JwtClaimOverride;
import static it.pagopa.interop.common.utils.JwtBuilderUtils.applyOverrides;
import static it.pagopa.interop.common.utils.JwtUtils.calculateKidFromPublicKey;

@Slf4j
@Service
public class CreateClientAssertionService {

    @Value("${interop.auth.client-assertion.audience}")
    private String clientAssertionAudience;

    public String createClientAssertion(Client client) throws NoSuchAlgorithmException, JsonProcessingException {
        return createClientAssertion(client, null, null, List.of());
    }

    public String createClientAssertion(Client client, Purpose purpose) throws NoSuchAlgorithmException, JsonProcessingException {
        return createClientAssertion(client, purpose, null, List.of());
    }

    public String createClientAssertion(Client client, List<JwtClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        return createClientAssertion(client, null, null, overrides);
    }

    public String createClientAssertion(Client client, Purpose purpose, List<JwtClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        return createClientAssertion(client, purpose, null, overrides);
    }

    public String createClientAssertion(Client client, Purpose purpose, KeyPair keyPair) throws NoSuchAlgorithmException, JsonProcessingException {
        return createClientAssertion(client, purpose, keyPair, List.of());
    }

    public String createClientAssertion(Client client, Purpose purpose, KeyPair keyPair, List<JwtClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        KeyPair kp = Optional.ofNullable(keyPair).orElseGet(client::getLastKeyPair);

        JwtBuilder builder = buildJwt(client, purpose, kp);

        if (overrides != null && !overrides.isEmpty()) {
            applyOverrides(builder, overrides);
        }

        String clientAssertion = builder.signWith(kp.getPrivate()).compact();
        log.info("Client assertion: '{}'", clientAssertion);

        return clientAssertion;
    }

    private JwtBuilder buildJwt(Client client, Purpose purpose, KeyPair keyPair) throws NoSuchAlgorithmException, JsonProcessingException {
        String clientId = client.getId().toString();
        String rawKid = calculateKidFromPublicKey(keyPair.getPublic());

        JwtBuilder jwt = Jwts.builder()
                .issuer(clientId)
                .subject(clientId)
                .audience().add(this.clientAssertionAudience).and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(43200)))
                .header().add("kid", rawKid).and();

        if (InteropClientType.valueOf(client.getKind().name()) == InteropClientType.CONSUMER && purpose != null) {
            jwt.claim("purposeId", purpose.getId().toString());
        }

        return jwt;
    }
}