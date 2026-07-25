package it.pagopa.interop.new_arch.common.infrastructure.security;

import it.pagopa.interop.new_arch.common.kernel.domain.KeyAlgorithm;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public final class KeyPairUtils {

    private KeyPairUtils() {
        throw new AssertionError("Utility class");
    }

    public static KeyPair generate(KeyAlgorithm algorithm) {
        return generate(algorithm, 2048);
    }

    public static KeyPair generate(KeyAlgorithm algorithm, int keyLength) {
        try {
            KeyPairGenerator generator;
            switch (algorithm) {
                case RSA -> {
                    generator = KeyPairGenerator.getInstance("RSA");
                    generator.initialize(keyLength);
                }
                case EC -> {
                    generator = KeyPairGenerator.getInstance("EC");
                    generator.initialize(new ECGenParameterSpec("secp256r1"));
                }
                case ED25519 -> generator = KeyPairGenerator.getInstance("Ed25519");
                default -> throw new IllegalArgumentException("Unsupported key algorithm: " + algorithm);
            }
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Error generating KeyPair for algorithm " + algorithm, e);
        }
    }

    public static String toPem(Key key) {
        String header = key instanceof PrivateKey
                ? "-----BEGIN PRIVATE KEY-----"
                : "-----BEGIN PUBLIC KEY-----";
        String footer = key instanceof PrivateKey
                ? "-----END PRIVATE KEY-----"
                : "-----END PUBLIC KEY-----";

        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        return "%s%n%s%n%s".formatted(header, encoded, footer);
    }

    public static String toBase64Pem(Key key) {
        return Base64.getEncoder().encodeToString(toPem(key).getBytes(StandardCharsets.UTF_8));
    }

    public static String toBase64PemContent(Key key) {
        String pem = toPem(key)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .trim();
        return Base64.getEncoder().encodeToString(pem.getBytes(StandardCharsets.UTF_8));
    }
}