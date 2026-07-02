package it.pagopa.interop.new_arch.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.utils.JwtBuilderUtils;
import it.pagopa.interop.common.utils.KeyPairUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtParameterTypes {

    @ParameterType("RSA|EC|ED25519")
    public KeyPairUtils.KeyAlgorithm keyAlgorithm(String alg) {
        return KeyPairUtils.KeyAlgorithm.valueOf(alg);
    }

    @DataTableType
    public JwtBuilderUtils.JwtClaimOverride jwtBuilder(Map<String, String> row) {
        String claim = row.get("claim");
        String value = row.get("value");

        if (claim == null || claim.isBlank()) {
            throw new IllegalArgumentException("Il campo 'claim' è obbligatorio");
        }

        // value può essere vuoto/null: utile per simulare claim mancanti o header non valorizzati
        return new JwtBuilderUtils.JwtClaimOverride(claim.trim(), value);
    }
}
