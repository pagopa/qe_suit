package it.pagopa.send.suite.contract.partner_tecnologico;

import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.partner_tecnologico.infrastructure.page.PtInstitutionsPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
    import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test dei link dell'header e del footer del BackOffice dei Partner Tecnologici, sulla pagina "I tuoi enti"
 * aperta con la sessione del PT {@code PT_1}.
 * <p>
 * I link sono quelli del portale ente mittente: per i link esterni si confronta l'indirizzo completo, per quelli interni
 * (informativa privacy e termini e condizioni) il percorso, perché il dominio è quello del portale in cui ci si trova.
 * "Assistenza" apre il client di posta al click e l'indirizzo non si legge dalla pagina: si verifica solo che il
 * pulsante ci sia.
 * <p>
 * Gira solo negli ambienti in cui il BackOffice PT è disponibile ({@code partner-tecnologico.enabled}).
 */
@EnabledIf(expression = "${partner-tecnologico.enabled}", loadContext = true)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebPtHeaderFooterContractTest {

    // indirizzi attesi, come nel portale ente mittente

    private static final String SERCQ_STATEMENT =
            "https://notifichedigitali.it/assets/MO_006_Service_Practice_Statement_Pago_PA_S_p_A_v_1_6_signed_0d50fefc36.pdf";
    private static final String ACCESSIBILITY = "https://form.agid.gov.it/view/978876c0-df2d-11ef-8637-9f856ac3da10";
    private static final String PRIVACY_POLICY_PATH = "/informativa-privacy";
    private static final String TERMS_OF_SERVICE_PATH = "/termini-di-servizio";
    private static final String ASSISTANCE = "Assistenza";

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    /**
     * Link dell'header e del footer:
     * <ul>
     *     <li>header: pulsante "Assistenza";</li>
     *     <li>footer: "Informativa Privacy" e "Termini e Condizioni" verso le pagine interne del portale;</li>
     *     <li>footer: "Service practice statement - SERCQ" e "Accessibilità" verso i documenti esterni.</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldLinkHeaderAndFooterLikeSenderPortal() {
        return webContractValidator.as(userProvider.resolve(UserAlias.PT_1))
                .on(PtInstitutionsPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "header: pulsante assistenza",
                                page -> {},
                                page -> page.header().assistanceButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ASSISTANCE))
                        ),

                        new WebScenario<>(
                                "footer: informativa privacy",
                                page -> {},
                                page -> Assertions.assertThat(path(href(page.footer().privacyPolicyLink()))).isEqualTo(PRIVACY_POLICY_PATH)
                        ),

                        new WebScenario<>(
                                "footer: termini e condizioni",
                                page -> {},
                                page -> Assertions.assertThat(path(href(page.footer().termsOfServiceLink()))).isEqualTo(TERMS_OF_SERVICE_PATH)
                        ),

                        new WebScenario<>(
                                "footer: service practice statement sercq",
                                page -> {},
                                page -> Assertions.assertThat(href(page.footer().sercqStatementLink())).isEqualTo(SERCQ_STATEMENT)
                        ),

                        new WebScenario<>(
                                "footer: accessibilità",
                                page -> {},
                                page -> Assertions.assertThat(href(page.footer().accessibilityLink())).isEqualTo(ACCESSIBILITY)
                        )
                ));
    }

    // indirizzo del link: fallisce se il link non c'è
    private static String href(Button link) {
        List<WebPresentationElement> elements = link.getAll().orElse(List.of());
        Assertions.assertThat(elements).as("link presente").isNotEmpty();
        return elements.get(0).getAttributes().get("href");
    }

    // percorso di un link interno, senza il dominio del portale
    private static String path(String href) {
        return URI.create(href).getPath();
    }
}
