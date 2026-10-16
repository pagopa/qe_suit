package it.pagopa.send.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VerificationCodeProvider {

    private final RestClient restClient = RestClient.create();

    // Default vuoto: la proprietà serve solo agli scenari SERCQ e non deve bloccare l'avvio del contesto
    @Value("${verification.code.service.url:}")
    private String verificationCodeServiceUrl;

    public String makeRequest(String email) {
        if (verificationCodeServiceUrl.isBlank()) {
            throw new IllegalStateException("Proprietà 'verification.code.service.url' non configurata per il profilo attivo");
        }
        try {
            TimeUnit.SECONDS.sleep(5);
            //Attendi un po' prima di fare la richiesta per assicurarti che il codice sia generato
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrotto durante l'attesa: {}", e.getMessage());
        }
        // Implement the logic to make a request to the service that provides the verification code
        // using the restClient and the provided email.
        String otp = restClient.get()
                .uri(verificationCodeServiceUrl + "/external-channels/verification-code/{email}?metadataOnly=true", email)
                .retrieve()
                .body(String.class);
        log.info("OTP received for email {}: {}", email, otp);

        try {
            TimeUnit.SECONDS.sleep(5);
            // Attendi un po' dopo aver ricevuto il codice per assicurarti che l'OTP sia stato registrato come valido
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrotto durante l'attesa: {}", e.getMessage());
        }

        return otp;
    }

}
