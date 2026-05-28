package it.pagopa.interop.services.dpop;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.utils.JwtBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Slf4j
@Service
public class DPoPProofService {

    @Value("${interop.auth.oauth.server}")
    private String defaultHtu;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String buildProof(KeyPair keyPair) {
        return buildProof(keyPair, HttpMethod.POST, defaultHtu, null);
    }

    public String buildProof(KeyPair keyPair, HttpMethod method, String htu) {
        return buildProof(keyPair, method, htu, null);
    }

    public String buildProofWithAth(KeyPair keyPair, HttpMethod method, String htu, String accessToken) {
        return buildProof(keyPair, method, htu, accessToken);
    }

    public String buildProofWithOverrides(KeyPair keyPair, List<JwtBuilderUtils.JwtClaimOverride> overrides) {
        String baseProof = buildProof(keyPair);
        if (overrides == null || overrides.isEmpty()) {
            return baseProof;
        }
        return applyOverridesAndResign(baseProof, keyPair, overrides);
    }

    private String buildProof(KeyPair keyPair, HttpMethod method, String htu, String accessToken) {
        try {
            long now = System.currentTimeMillis() / 1000;

            Map<String, Object> header = buildHeader(keyPair.getPublic());
            Map<String, Object> payload = buildPayload(method, htu, now, accessToken);

            return sign(header, payload, keyPair.getPrivate());
        } catch (Exception e) {
            throw new IllegalStateException("Errore nella creazione del DPoP proof", e);
        }
    }

    private Map<String, Object> buildHeader(PublicKey publicKey) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("typ", "dpop+jwt");
        header.put("alg", resolveAlg(publicKey));
        header.put("jwk", buildJwk(publicKey));
        return header;
    }

    private Map<String, Object> buildPayload(HttpMethod method, String htu, long now, String accessToken) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("jti", UUID.randomUUID().toString());
        payload.put("htm", method.name());
        payload.put("htu", htu);
        payload.put("iat", now);
        if (accessToken != null) {
            payload.put("ath", sha256Base64Url(accessToken));
        }
        return payload;
    }

    private Map<String, Object> buildJwk(PublicKey publicKey) {
        Map<String, Object> jwk = new LinkedHashMap<>();
        if (publicKey instanceof RSAPublicKey rsa) {
            jwk.put("kty", "RSA");
            jwk.put("n", b64(rsa.getModulus().toByteArray()));
            jwk.put("e", b64(rsa.getPublicExponent().toByteArray()));
        } else if (publicKey instanceof ECPublicKey ec) {
            jwk.put("kty", "EC");
            jwk.put("crv", "P-256");
            jwk.put("x", b64(ec.getW().getAffineX().toByteArray()));
            jwk.put("y", b64(ec.getW().getAffineY().toByteArray()));
        } else {
            throw new IllegalArgumentException("Algoritmo non supportato: " + publicKey.getAlgorithm());
        }
        return jwk;
    }

    private String resolveAlg(PublicKey publicKey) {
        return switch (publicKey.getAlgorithm()) {
            case "RSA" -> "RS256";
            case "EC" -> "ES256";
            default ->
                    throw new IllegalArgumentException("Algoritmo non supportato per DPoP: " + publicKey.getAlgorithm());
        };
    }

    private String sign(Map<String, Object> header, Map<String, Object> payload, PrivateKey privateKey) throws Exception {
        String headerB64 = b64(MAPPER.writeValueAsBytes(header));
        String payloadB64 = b64(MAPPER.writeValueAsBytes(payload));
        String signingInput = headerB64 + "." + payloadB64;

        String javaAlg = "RS256".equals(header.get("alg")) ? "SHA256withRSA" : "SHA256withECDSA";
        java.security.Signature sig = java.security.Signature.getInstance(javaAlg);
        sig.initSign(privateKey);
        sig.update(signingInput.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        return signingInput + "." + b64(sig.sign());
    }

    private String sha256Base64Url(String input) {
        try {
            byte[] hash = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return b64(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Errore nel calcolo SHA-256", e);
        }
    }

    private String applyOverridesAndResign(String jwt, KeyPair keyPair, List<JwtBuilderUtils.JwtClaimOverride> overrides) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("JWT malformato");

            Map<String, Object> header = parseB64Json(parts[0]);
            Map<String, Object> payload = parseB64Json(parts[1]);

            for (JwtBuilderUtils.JwtClaimOverride override : overrides) {
                String claim = override.claim();
                String value = override.value();

                if (claim.startsWith("header.")) {
                    String headerClaim = claim.substring("header.".length());
                    header.put(headerClaim, value);
                } else if ("__remove".equals(claim)) {
                    payload.remove(value);
                } else {
                    payload.put(claim, parseValue(value));
                }
            }

            return sign(header, payload, keyPair.getPrivate());

        } catch (Exception e) {
            throw new IllegalStateException("Errore nell'applicazione degli override al DPoP proof", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseB64Json(String b64) throws Exception {
        byte[] decoded = Base64.getUrlDecoder().decode(b64);
        return MAPPER.readValue(decoded, Map.class);
    }

    private Object parseValue(String value) {
        if (value == null) return null;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
        }
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        return value;
    }

    private static String b64(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}