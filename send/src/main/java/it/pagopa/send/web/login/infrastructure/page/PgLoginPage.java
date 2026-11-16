package it.pagopa.send.web.login.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.common.user.domain.User;
import org.assertj.core.api.Assertions;
import it.pagopa.send.web.login.infrastructure.page.AbstractOneIdPage;

/**
 * Pagina di login del portale imprese.
 * È anche la pagina su cui si atterra dopo l'uscita dal portale imprese: l'assertLoaded cerca il titolo
 * "Come vuoi accedere?" per testo, perché prima di arrivarci si passa da altre pagine.
 */
@Url("${url.notifiche.persona-giuridica.base}")
public interface PgLoginPage extends AbstractOneIdPage, Page {

    String TITLE = "Come vuoi accedere?";

    @XPath("//h3[normalize-space()=\"" + TITLE + "\"]")
    Readable<String> title();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div/div/div/div/div/div[2]/div/div")
    Clickable pgSelector();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[3]/div/button")
    Clickable pgLoginButton();

    @XPath("${selettore.login.pg.area-riservata-selettore-env}")
    Clickable reservedAreaEnvSelector();

    @Override
    default void loginWithSpid(User user) {
        AbstractOneIdPage.super.loginWithSpid(user);
        pgSelector().click();
        pgLoginButton().click();
        reservedAreaEnvSelector().click();
    }

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
    }
}
