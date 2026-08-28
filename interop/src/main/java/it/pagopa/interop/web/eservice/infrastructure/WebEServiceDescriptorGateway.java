package it.pagopa.interop.web.eservice.infrastructure;

import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.web.eservice.application.WebUpdateEServiceDescriptorCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebEServiceDescriptorGateway implements EServiceDescriptorGateway {

    private final WebPresentationGateway webPresentationGateway;
    private final Environment environment;
    private String dailyCallsPerConsumer;
    private String dailyCallsTotal;
    private String audience;

    @Override
    public EServiceDescriptor getEServiceDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        throw new UnsupportedOperationException("getEServiceDescriptor Not supported yet.");
    }

    @Override
    public EServiceDescriptor updateDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, UpdateEServiceDescriptorCommand command) {
        if (!(command instanceof WebUpdateEServiceDescriptorCommand webCommand))
            throw new IllegalArgumentException("Command must be an instance of WebUpdateEServiceDescriptorCommand");

        UpdateEServiceDescriptorSeed payload = webCommand.getWebPayload();

        // 1) costruzione URL custom con id generati a runtime
        String base = environment.resolvePlaceholders("${interop.web.base-url}");
        String url = base + "/erogazione/e-service/" + eServiceRef.id() + "/" + descriptorRef.id() + "/modifica";

        // 2) navigazione Selenium alla URL custom
        webPresentationGateway.navigateTo(Url.of(url));

        // 3) pagina SOGLIE E ATTRIBUTI
        //      a) chiamate api / gg per fruitore
        dailyCallsPerConsumer = webPresentationGateway
                .findElement(XPathSelector.of(".//input[@id='dailyCallsPerConsumer']"))
                .map(WebPresentationElement::getText)   // per input = value
                .orElse("");
        //      b) chiamate api / gg totali
        dailyCallsTotal =  webPresentationGateway
                .findElement(XPathSelector.of(".//input[@id='dailyCallsTotal']"))
                .map(WebPresentationElement::getText)   // per input = value
                .orElse("");

        // 4) vai avanti
        webPresentationGateway.click(
                XPathSelector.of(".//button[contains(@class,'MuiButton-root') and normalize-space()='Salva bozza e prosegui']")
        );

        return EServiceDescriptor.builder()
                .id(descriptorRef.id())
                .state(EServiceDescriptorState.DRAFT)
                .dailyCallsPerConsumer(Integer.valueOf(dailyCallsPerConsumer))
                .dailyCallsTotal(Integer.valueOf(dailyCallsTotal))
                .build();
    }

    @Override
    public EServiceDescriptor linkOpenApiInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String openApiInterfacePath) {
        // 5) pagina SPECIFICHE TECNICHE
        //      a) carica file swagger
        try {
            String swaggerPath = new ClassPathResource("assets/origin-interface.yaml")
                    .getFilePath().toAbsolutePath().toString();

            //  aa) invio diretto del file all'input nascosto (niente dialog nativo)
            webPresentationGateway.sendFile(
                    XPathSelector.of(".//div[@data-testid='fileInput']//input[@type='file']"),
                    swaggerPath
            );
        } catch (Exception e) {
            throw new IllegalStateException("Impossibile caricare il file swagger dagli assets", e);
        }

        //      b) conferma caricamento file swagger con "Salva documento"
        webPresentationGateway.click(
                XPathSelector.of(".//button[contains(@class,'MuiButton-root') and normalize-space()='Salva documento']")
        );
        //      c) modifica input con id "audience" inserendo "https://eservice.pa.it/api/v1"


//        audience = "https://eservice.pa.it/api/v1";
        audience = "eservice.pa.it/api/v1";
        webPresentationGateway.clearAndSendText(
                XPathSelector.of(".//input[@id='audience']"),
                audience
        );

        // 6) vai avanti
        webPresentationGateway.click(
                XPathSelector.of(".//button[contains(@class,'MuiButton-root') and normalize-space()='Salva bozza e prosegui']")
        );

        return EServiceDescriptor.builder()
                .id(descriptorRef.id())
                .state(EServiceDescriptorState.DRAFT)
                .audience(Collections.singleton(audience))
                .dailyCallsPerConsumer(Integer.valueOf(dailyCallsPerConsumer))
                .dailyCallsTotal(Integer.valueOf(dailyCallsTotal))
                .interfaceDocument(
                        Document.builder()
                                .id(UUID.randomUUID())
                                .contentType("application/text")
                                .name("interfaccia-swagger")
                                .build()
                )
                .build();
    }

    @Override
    public EServiceDescriptor publishDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        // 7) inserisci descrizione versione eservice
        webPresentationGateway.sendText(
                XPathSelector.of("//textarea[@id='description']"),
                "Lorem ipsum dolor sit amet"
        );

        // 8) clicca su "Vai al riepilogo"
        webPresentationGateway.click(
                XPathSelector.of(".//button[contains(@class,'MuiButton-root') and normalize-space()='Vai al riepilogo']")
        );

        // 9) clicca su "Pubblica"
        webPresentationGateway.click(
                XPathSelector.of(".//button[contains(@class,'MuiButton-root') and normalize-space()='Pubblica']")
        );

        // 10) clicca su "Pubblica" nel modale di conferma

        webPresentationGateway.click(
//                XPathSelector.of(".//button[contains(@class, 'MuiButton-root') and not(.//span[@class='MuiButton-icon']) and normalize-space()='Pubblica']")
                XPathSelector.of(
                        ".//div[.//*[contains(normalize-space(),'Pubblica e-service')]]" +
                                "//button[normalize-space()='Pubblica']"
                )
        );

        return EServiceDescriptor.builder()
                .id(descriptorRef.id())
                .state(EServiceDescriptorState.PUBLISHED)
                .audience(Collections.singleton(audience))
                .dailyCallsPerConsumer(Integer.valueOf(dailyCallsPerConsumer))
                .dailyCallsTotal(Integer.valueOf(dailyCallsTotal))
                .interfaceDocument(
                        Document.builder()
                                .id(UUID.randomUUID())
                                .contentType("application/text")
                                .name("interfaccia-swagger")
                                .build()
                )
                .publishedAt(Instant.now())
                .build();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}
