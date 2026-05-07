package it.pagopa.interop.infrastructure.http.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.function.Supplier;

public class BearerAuthInterceptor implements ClientHttpRequestInterceptor {

    private final Supplier<String> tokenSupplier;

    public BearerAuthInterceptor(Supplier<String> tokenSupplier) {
        this.tokenSupplier = tokenSupplier;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String token = tokenSupplier.get();
        request.getHeaders().setBearerAuth(token);
        return execution.execute(request, body);
    }
}