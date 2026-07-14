package it.pagopa.interop.new_arch.bff.infrastructure.security.bearer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.UserContext;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.SignRequest;
import software.amazon.awssdk.services.kms.model.SignResponse;
import software.amazon.awssdk.services.kms.model.SigningAlgorithmSpec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class BearerAuthProvider {

    private final ObjectMapper objectMapper;
    private final KmsClient kmsClient;
    private final BearerTokenProperties properties;
    private final ObjectProvider<UserContext> userContextProvider;

    @Cacheable(cacheNames = "sessionToken", key = "@bearerAuthProvider.cacheKey()")
    public String getToken() {
        try {
            UserContext userContext = getUserContextOrThrow();
            User user = userContext.getUser();
            Tenant tenant = userContext.getTenant();
            String role = user.getRole().name().toLowerCase();

            long now = Instant.now().getEpochSecond();
            long exp = now + properties.durationSec();

            Map<String, String> jwk = fetchKidAlg(properties.wellKnownUrl());
            String kid = jwk.get("kid");
            String alg = jwk.get("alg");

            Map<String, Object> header = Map.of(
                    "typ", "at+jwt",
                    "alg", alg,
                    "kid", kid
            );

            Map<String, Object> payload = new HashMap<>();
            payload.put("iss", properties.issuer());
            payload.put("aud", properties.audience());
            payload.put("uid", user.getUserId().toString());
            payload.put("organizationId", tenant.getOrganizationId().toString());
            payload.put("externalId", Map.of(
                    "origin", tenant.getExternalIdOrigin(),
                    "value", tenant.getExternalIdValue()
            ));
            payload.put("organization", Map.of(
                    "id", tenant.getOrganizationId().toString(),
                    "name", tenant.getName(),
                    "roles", List.of(Map.of(
                            "partyRole", "MANAGER",
                            "role", role
                    ))
            ));
            payload.put("selfcareId", tenant.getSelfcareId().toString());
            payload.put("user-roles", role);
            payload.put("iat", now);
            payload.put("nbf", now);
            payload.put("exp", exp);
            payload.put("jti", UUID.randomUUID().toString());

            String unsignedToken = b64Url(objectMapper.writeValueAsString(header))
                    + "."
                    + b64Url(objectMapper.writeValueAsString(payload));

            SignRequest signRequest = SignRequest.builder()
                    .keyId(kid)
                    .message(SdkBytes.fromUtf8String(unsignedToken))
                    .signingAlgorithm(mapToKmsAlg(alg))
                    .build();

            SignResponse signResponse = kmsClient.sign(signRequest);
            String signature = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signResponse.signature().asByteArray());

            return unsignedToken + "." + signature;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to generate session token", e);
        }
    }

    /**
     * Calcola la chiave di cache in modo sicuro.
     * Se invocato fuori da uno scenario attivo (es. durante ispezioni dell'IDE o warmup),
     * restituisce una chiave di fallback evitando crash di sistema.
     */
    public String cacheKey() {
        UserContext userContext = userContextProvider.getIfAvailable();
        if (userContext == null) {
            return "no-active-scenario";
        }

        try {
            User u = userContext.getUser();
            Tenant t = userContext.getTenant();

            return String.join("|",
                    u.getUserId().toString(),
                    t.getOrganizationId().toString(),
                    t.getSelfcareId().toString(),
                    u.getRole().name(),
                    t.getExternalIdOrigin(),
                    t.getExternalIdValue()
            );
        } catch (IllegalStateException e) {
            return "no-active-session";
        }
    }

    private UserContext getUserContextOrThrow() {
        UserContext userContext = userContextProvider.getIfAvailable();
        if (userContext == null) {
            throw new IllegalStateException("UserContext is not available in the current execution scope.");
        }
        return userContext;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> fetchKidAlg(String url) throws IOException {
        StringBuilder response = getStringBuilder(url);

        Map<String, Object> json = objectMapper.readValue(response.toString(), new TypeReference<>() {
        });
        List<Map<String, Object>> keys = (List<Map<String, Object>>) json.get("keys");
        if (keys == null || keys.isEmpty()) {
            throw new IllegalStateException("No keys found in well-known response");
        }

        Map<String, Object> first = keys.get(0);
        return Map.of(
                "kid", String.valueOf(first.get("kid")),
                "alg", String.valueOf(first.get("alg"))
        );
    }

    private static @NonNull StringBuilder getStringBuilder(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            throw new IllegalStateException("Failed to fetch well-known: " + connection.getResponseCode());
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response;
    }

    private static String b64Url(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static SigningAlgorithmSpec mapToKmsAlg(String alg) {
        if ("RS256".equalsIgnoreCase(alg)) {
            return SigningAlgorithmSpec.RSASSA_PKCS1_V1_5_SHA_256;
        }
        throw new IllegalArgumentException("Unsupported alg for KMS mapping: " + alg);
    }
}