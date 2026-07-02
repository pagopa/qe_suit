package it.pagopa.interop.new_arch.common.utils;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.PublicJwk;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class JwtUtils {

    public static String calculateKidFromPublicKey(PublicKey publicKey) throws NoSuchAlgorithmException, JsonProcessingException {
        PublicJwk<PublicKey> publicJwk = Jwks.builder().key(publicKey).build();

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
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

    }

}
