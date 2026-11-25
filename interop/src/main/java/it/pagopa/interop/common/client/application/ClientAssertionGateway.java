package it.pagopa.interop.common.client.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientAssertion;
import it.pagopa.interop.common.client.domain.ClientAssertionClaimOverride;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.kernel.security.AccessToken;
import it.pagopa.kernel.security.DPoPProof;

import javax.annotation.Nullable;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface ClientAssertionGateway {
    ClientAssertion createClientAssertion(Client client, Purpose purpose, KeyPair keyPair, List<ClientAssertionClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException;

    /** Exchanges a signed assertion through the shared Auth service; API clients do not require a purpose. */
    AccessToken generateVoucher(Client client, @Nullable Purpose purpose, @Nullable DPoPProof dPoPProof)
            throws NoSuchAlgorithmException, JsonProcessingException;
}
