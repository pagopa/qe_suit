package it.pagopa.interop.m2m.v3.infrastructure.security;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import it.pagopa.kernel.security.AccessToken;
import it.pagopa.kernel.security.DPoPProof;
import it.pagopa.kernel.security.DPoPProofService;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;

public class DPoPAuthenticationFilter implements Filter {

    private final AccessToken accessToken;
    private final KeyPair dpopKeyPair;
    private final DPoPProofService dpopProofService;

    public DPoPAuthenticationFilter(
            AccessToken accessToken,
            KeyPair dpopKeyPair,
            DPoPProofService dpopProofService) {

        this.accessToken = accessToken;
        this.dpopKeyPair = dpopKeyPair;
        this.dpopProofService = dpopProofService;
    }

    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext ctx) {

        String method = requestSpec.getMethod();

        String htu = buildHtu(requestSpec);

        DPoPProof proof = dpopProofService.buildDPoPProof(
                dpopKeyPair,
                DPoPProofService.HttpMethod.valueOf(method),
                htu,
                accessToken.jwt()
        );

        requestSpec.header(
                "Authorization",
                "DPoP " + accessToken.jwt()
        );

        requestSpec.header(
                "DPoP",
                proof.getJwt()
        );

        return ctx.next(requestSpec, responseSpec);
    }

    private String buildHtu(FilterableRequestSpecification requestSpec) {
        URI uri = URI.create(requestSpec.getURI());

        try {
            return new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null,
                    null
            ).toString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(
                    "Unable to build DPoP htu from request URI: " + uri,
                    e
            );
        }
    }
}
