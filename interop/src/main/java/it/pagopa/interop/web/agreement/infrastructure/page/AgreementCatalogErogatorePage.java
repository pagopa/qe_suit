package it.pagopa.interop.web.agreement.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.common.eservice.domain.EService;
import org.openqa.selenium.Keys;

@Url("${interop.web.agreement-detail-erogatore}")
public interface AgreementCatalogErogatorePage extends Page {

    @XPath(".//label[text()='Cerca per e-service']/following-sibling::div//input")
    TextField cercaPerEService();

    @XPath(".//a[normalize-space()='Visualizza']")
    Button visualizza();

    default String getFruitionRequestId(EService eService) {
        cercaPerEService().cleanAndWrite(eService.getName());
        cercaPerEService().write(Keys.ARROW_DOWN.toString());
        cercaPerEService().write(Keys.ENTER.toString());

        // URL corrente della pagina caricata dopo il click
String href = visualizza().get()
        .map(el -> el.getAttributes().get("href"))
        .filter(value -> !value.isBlank())
        .orElseThrow(() -> new IllegalStateException("Visualizza link for the e-service was not found"));
String requestId = href.substring(href.lastIndexOf('/') + 1);
if (requestId.isBlank()) {
    throw new IllegalStateException("Visualizza link does not contain a request ID");
}
return requestId;
    }
}

