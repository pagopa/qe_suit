package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/termini-di-servizio}
 * Pagina dei termini e condizioni d'uso di SEND della persona giuridica.
 * Si apre direttamente dall'URL: è una rotta pubblica, senza voce di menu.
 * La pagina non ha un titolo proprio: contiene solo il contenitore dell'informativa ({@code div role="article"} con
 * classe {@code otnotice}), che un widget OneTrust riempie con un indice e le sezioni del documento.
 * L'assertLoaded verifica che la pagina sia caricata, cioè che ci sia il contenitore e che OneTrust abbia caricato
 * almeno una sezione; titoli delle sezioni e indice sono verificati da {@code WebTermsOfServicePageContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.termini-di-servizio}")
public interface TermsOfServicePGPage extends Page {

    @XPath("//div[@role=\"article\" and contains(@class,\"otnotice\")]")
    Component notice();

    @XPath("(//section[starts-with(@id,\"otnotice-section-\")]//h2)[1]")
    Readable<String> title();

    @XPath("//section[starts-with(@id,\"otnotice-section-\")]")
    Readable<String> sections();

    @XPath("//section[starts-with(@id,\"otnotice-section-\")]/h2")
    Readable<String> sectionTitles();

    // indice per desktop: quello per mobile (otnotice-menu-mobile) è nascosto
    @XPath("//ul[@class=\"otnotice-menu\"]/li/a")
    Button indexLinks();

    // labels

    @XPath("(//section[starts-with(@id,\"otnotice-section-\")]//p)[1]")
    Readable<String> introduction();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(notice().get(FindPolicy.PRESENT)).as("contenitore dell'informativa OneTrust").isPresent();
        sections().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty());
    }
}
