package it.pagopa.interop.common.client.infrastructure;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import it.pagopa.interop.common.client.application.ClientAssertionGateway;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientAssertion;
import it.pagopa.interop.common.client.domain.ClientAssertionClaimOverride;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;
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

import static it.pagopa.interop.common.client.domain.ClientKind.CONSUMER;
import static it.pagopa.interop.common.infrastructure.utils.jwt.JwtBuilder.applyOverrides;
import static it.pagopa.interop.common.infrastructure.utils.jwt.JwtUtils.calculateKidFromPublicKey;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommonClientAssertionGateway implements ClientAssertionGateway {

    @Value("${interop.auth.client-assertion.audience}")
    private String clientAssertionAudience;

    private final ClientAssertionClaimOverrideMapper mapper;

    public ClientAssertion createClientAssertion(Client client, Purpose purpose, KeyPair keyPair, List<ClientAssertionClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        KeyPair kp = Optional.ofNullable(keyPair).orElseGet(() -> client.getLastKey().pair());
        JwtBuilder builder = buildJwt(client, purpose, kp);

        if (overrides != null && !overrides.isEmpty()) {
            applyOverrides(builder, mapper.map(overrides));
        }

        String clientAssertion = builder.signWith(kp.getPrivate()).compact();
        log.info("Client assertion: '{}'", clientAssertion);

        return ClientAssertion.builder()
                .clientAssertion(clientAssertion)
                .build();
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

        if (client.getKind() == CONSUMER && purpose != null) {
            jwt.claim("purposeId", purpose.getId().toString());
        }

        return jwt;
    }
}
