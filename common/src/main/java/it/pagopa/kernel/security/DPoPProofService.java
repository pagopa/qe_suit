package it.pagopa.kernel.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.utils.jwt.JwtBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class DPoPProofService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public enum HttpMethod {
        GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS
    }

    public DPoPProof buildDPoPProof(KeyPair keyPair, HttpMethod method, String htu) {
        return internalBuildDPoPProof(keyPair, method, htu, null);
    }

    public DPoPProof buildDPoPProof(KeyPair keyPair, HttpMethod method, String htu, String accessToken) {
        return internalBuildDPoPProof(keyPair, method, htu, accessToken);
    }

    public DPoPProof buildDPoPProofWithOverrides(KeyPair keyPair, HttpMethod method, String htu, String accessToken, List<JwtBuilder.JwtClaimOverride> overrides) {
        String baseProof = internalBuildDPoPProof(keyPair, method, htu, accessToken);
        if (overrides == null || overrides.isEmpty()) {
            return baseProof;
        }
        return applyOverridesAndResign(baseProof, keyPair, overrides);
    }

    public DPoPProof buildDPoPProofWithOverrides(KeyPair keyPair, HttpMethod method, String htu, List<JwtBuilder.JwtClaimOverride> overrides) {
       return buildDPoPProofWithOverrides(keyPair, method, htu, null, overrides);
    }

    public void verifyDpopProof(DPoPProof dpopProof) {
        try {
            // 1. Parsing del JWT
            SignedJWT signedJWT = SignedJWT.parse(dpopJwtRaw);
            JWSHeader header = signedJWT.getHeader();

            // 2. Controllo 'typ' = 'dpop+jwt'
            if (header.getType() == null || !"dpop+jwt".equalsIgnoreCase(header.getType().toString())) {
                throw new IllegalArgumentException("Header 'typ' must be 'dpop+jwt'");
            }

            // 3. Estrazione JWK (chiave pubblica)
            JWK jwk = header.getJWK();
            if (jwk == null) {
                throw new IllegalArgumentException("Missing JWK in DPoP header");
            }

            // 4. Costruzione del verificatore
            JWSVerifier verifier;
            if (jwk instanceof ECKey ecKey) {
                verifier = new ECDSAVerifier(ecKey);
            } else if (jwk instanceof RSAKey rsaKey) {
                verifier = new RSASSAVerifier(rsaKey);
            } else {
                throw new IllegalArgumentException("Unsupported key type: " + jwk.getKeyType());
            }

            // 5. Verifica della firma
            if (!signedJWT.verify(verifier)) {
                throw new SecurityException("DPoP proof signature is invalid");
            }

            // 6. Parsing del payload
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // 7. Verifica htm = POST
            String htm = (String) claims.getClaim("htm");
            if (!"POST".equalsIgnoreCase(htm)) {
                throw new IllegalArgumentException("Invalid 'htm' claim: expected POST");
            }

            // 8. Verifica htu = expected URL
            String htu = (String) claims.getClaim("htu");
            String expectedHtu = dpopHtu;
            if (!expectedHtu.equalsIgnoreCase(htu)) {
                throw new IllegalArgumentException("Invalid 'htu' claim: unexpected URI");
            }

            // 9. Verifica iat (entro 60 secondi)
            Date issuedAt = claims.getIssueTime();
            if (issuedAt == null) {
                throw new IllegalArgumentException("Missing 'iat' claim");
            }
            long now = System.currentTimeMillis();
            long issuedAtTime = issuedAt.getTime();
            if (Math.abs(now - issuedAtTime) > 60_000) {
                throw new IllegalArgumentException("DPoP proof is outside the valid time window (60s)");
            }

            // 10. Presenza del jti
            String jti = claims.getJWTID();
            if (jti == null) {
                throw new IllegalArgumentException("Missing 'jti' claim");
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore nella verifica della firma DPoP: " + e.getMessage(), e);
        }
    }

    private String internalBuildDPoPProof(KeyPair keyPair, HttpMethod method, String htu, String accessToken) {
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

    private String applyOverridesAndResign(String jwt, KeyPair keyPair, List<JwtBuilder.JwtClaimOverride> overrides) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("JWT malformato");

            Map<String, Object> header = parseB64Json(parts[0]);
            Map<String, Object> payload = parseB64Json(parts[1]);

            for (JwtBuilder.JwtClaimOverride override : overrides) {
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
