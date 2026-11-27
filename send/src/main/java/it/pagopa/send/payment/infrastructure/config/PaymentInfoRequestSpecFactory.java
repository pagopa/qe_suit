package it.pagopa.send.payment.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.generated.openapi.clients.externalregistries.payment.JacksonObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

/**
 * Request specification del client {@code pn-payment-info-external-v1.yaml} di pn-external-registries.
 * <p>
 * Dal 2024-12 (PN-13569) queste operazioni non sono piu' pubblicate su API Gateway: i frontend passano dal BFF
 * ({@code /bff/v1/payments/*}). Si raggiungono sull'ALB interno, che instrada {@code /ext-registry/pagopa/*} al
 * servizio checkout di pn-external-registries, attraverso il tunnel SSM ({@code pn tunnel up}, porta 8886).
 * Sull'ALB non ci sono authorizer ne' WAF: la richiesta non porta credenziali.
 * <p>
 * La base URL e' {@code external-registries.payment.base-url}.
 */
@Component
public class PaymentInfoRequestSpecFactory {

    @Value("${external-registries.payment.base-url}")
    private String baseUrl;

    public RequestSpecBuilder create() {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setConfig(config().objectMapperConfig(
                        objectMapperConfig().defaultObjectMapper(JacksonObjectMapper.jackson())
                ))
                .addFilter(new HttpLoggingFilter());
    }
}
