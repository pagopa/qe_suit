package it.pagopa.interop.domain.services.dpop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class DPoPProofService {

    @Value("${interop.auth.oauth.server}")
    private String defaultHtu;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Proof standard: POST sull'URL di token di default.
     */
    public String buildProof(KeyPair keyPair) {
        return buildProof(keyPair, HttpMethod.POST, defaultHtu);
    }

    /**
     * Proof con metodo e HTU custom.
     */
    public String buildProof(KeyPair keyPair, HttpMethod method, String htu) {
        return buildProof(keyPair, method, htu, null);
    }

    /**
     * Proof con ath (binding all'access token).
     */
    public String buildProofWithAth(KeyPair keyPair, HttpMethod method, String htu, String accessToken) {
        return buildProof(keyPair, method, htu, accessToken);
    }

    private String buildProof(KeyPair keyPair, HttpMethod method, String htu, String accessToken) {
        try {
            long now = System.currentTimeMillis() / 1000;

            Map<String, Object> header = new LinkedHashMap<>();
            header.put("typ", "dpop+jwt");
            header.put("alg", resolveAlg(keyPair.getPublic()));
            header.put("jwk", buildJwk(keyPair.getPublic()));

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("jti", UUID.randomUUID().toString());
            payload.put("htm", method.name());
            payload.put("htu", htu);
            payload.put("iat", now);

            if (accessToken != null) {
                payload.put("ath", sha256Base64Url(accessToken));
            }

            return sign(header, payload, keyPair.getPrivate());

        } catch (Exception e) {
            throw new IllegalStateException("Errore nella creazione del DPoP proof", e);
        }
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

    private String sha256Base64Url(String input) throws Exception {
        byte[] hash = java.security.MessageDigest.getInstance("SHA-256")
                .digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return b64(hash);
    }

    private static String b64(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}