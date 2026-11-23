package it.pagopa.send.web.partner_tecnologico.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.partner_tecnologico.application.PtInstitutionsGateway;
import it.pagopa.send.web.partner_tecnologico.infrastructure.page.PtInstitutionsPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Service;

/** Ricerca degli enti sul browser, dalla pagina "I tuoi enti" del BackOffice PT. */
@Service
@RequiredArgsConstructor
public class WebPtInstitutionsGateway implements PtInstitutionsGateway {
    private final WebPresentationGateway browser;

    @Override
    public void searchInstitutions(String text) {
        browser.bind(PtInstitutionsPage.class).searchInstitutions(text);
    }

    @Override
    public void verifySearchFilterIsEmpty() {
        browser.bind(PtInstitutionsPage.class).searchInput()
                .readAndAssert(h -> Assertions.assertThat(h).isEmpty());
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}
