package it.pagopa.interop.common.infrastructure.http.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

public class HttpLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger("it.pagopa.interop.infrastructure.http");
    private static final Set<String> SENSITIVE = Set.of();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("HTTP OUT -> {} {}", request.getMethod(), request.getURI());
            request.getHeaders().forEach((k, v) -> {
                String key = k == null ? "" : k.toLowerCase(Locale.ROOT);
                boolean sensitive = SENSITIVE.stream().anyMatch(key::contains);
                log.debug("HTTP OUT HEADER -> {}: {}", k, sensitive ? "***MASKED***" : v);
            });
            if (body != null && body.length > 0) {
                log.debug("HTTP OUT BODY -> {}", new String(body, StandardCharsets.UTF_8));
            }
        }

        ClientHttpResponse response = execution.execute(request, body);

        if (log.isDebugEnabled()) {
            log.debug("HTTP IN <- {} {}", response.getStatusCode().value(), response.getStatusText());
            response.getHeaders().forEach((k, v) -> log.debug("HTTP IN HEADER <- {}: {}", k, v));
            String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
            if (!responseBody.isBlank()) {
                log.debug("HTTP IN BODY <- {}", responseBody);
            }
        }
        return response;
    }

    public static ClientHttpRequestFactory bufferingFactory() {
        return new BufferingClientHttpRequestFactory(new JdkClientHttpRequestFactory());
    }
}