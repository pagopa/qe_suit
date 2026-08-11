package it.pagopa.interop.common.kernel.utils.jwt;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.PublicJwk;
import it.pagopa.interop.common.kernel.domain.KeyAlgorithm;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class JwtUtils {

    private JwtUtils() {
    }

    public static String calculateKidFromPublicKey(PublicKey publicKey) throws NoSuchAlgorithmException, JsonProcessingException {

        PublicJwk<PublicKey> publicJwk = Jwks.builder()
                .key(publicKey)
                .build();

        LinkedHashMap<String, Object> sortedJwk = publicJwk.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        String jsonJwk = new ObjectMapper().writeValueAsString(sortedJwk);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonJwk.getBytes(StandardCharsets.UTF_8));

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(hash);
    }

    public static String encodeDelimitedPublicKeyBase64(PublicKey publicKey) {
        String encodedKey = Base64.getMimeEncoder(
                        64,
                        "\n".getBytes(StandardCharsets.UTF_8)
                )
                .encodeToString(publicKey.getEncoded());

        String pem = """
            -----BEGIN PUBLIC KEY-----
            %s
            -----END PUBLIC KEY-----
            """.formatted(encodedKey);

        return Base64.getEncoder()
                .encodeToString(pem.getBytes(StandardCharsets.UTF_8));
    }

    public static String resolveAlgorithm(KeyAlgorithm algorithm) {
        return switch (algorithm) {
            case RSA -> "RS256";
            case EC -> "ES256";
            default -> throw new IllegalArgumentException(
                    "Unsupported key type: " + algorithm
            );
        };
    }
}