package it.pagopa.interop.domain.services.client_assertion;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import it.pagopa.interop.domain.enums.InteropClientType;
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
public class ClientAssertionService {

    @Value("${interop.auth.client-assertion.audience}")
    private String clientAssertionAudience;

    @Value("${interop.auth.client-assertion.grant_type}")
    private String clientAssertionGrantType;

    @Value("${interop.auth.client-assertion.assertion_type}")
    private String clientAssertionType;

    public record CreateClientAssertionRequest(InteropClientType clientType, String clientId, String purposeId,
                                               KeyPair keyPair, List<JwtClaimOverride> overrides) {
    }

    public String createClientAssertion(CreateClientAssertionRequest clientAssertionRequest) throws NoSuchAlgorithmException, JsonProcessingException {

        JwtBuilder clientAssertionBuilder = getValidClientAssertionBuilder(
                clientAssertionRequest.clientType,
                clientAssertionRequest.clientId,
                clientAssertionRequest.purposeId,
                clientAssertionRequest.keyPair
        );

        if (!clientAssertionRequest.overrides.isEmpty())
            applyOverrides(clientAssertionBuilder, clientAssertionRequest.overrides);

        String clientAssertion = clientAssertionBuilder.signWith(clientAssertionRequest.keyPair.getPrivate()).compact();
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
