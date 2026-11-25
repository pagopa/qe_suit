package it.pagopa.send.common.partner_tecnologico.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Casi d'uso sulla ricerca degli enti deleganti del Partner Tecnologico. Puro pass-through verso
 * {@link PtInstitutionsGateway}: sono le implementazioni del gateway (una per canale) a sapere come procedere.
 */
@Service
@RequiredArgsConstructor
public class PtInstitutionsUseCase {

    private final PtInstitutionsGateway ptInstitutionsGateway;

    public void searchInstitutions(String text) {
        ptInstitutionsGateway.searchInstitutions(text);
    }

    public void verifySearchFilterIsEmpty() {
        ptInstitutionsGateway.verifySearchFilterIsEmpty();
    }
}
