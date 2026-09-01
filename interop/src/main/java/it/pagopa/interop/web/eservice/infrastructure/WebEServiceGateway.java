package it.pagopa.interop.web.eservice.infrastructure;

import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.web.eservice.application.WebEServiceCreationCommand;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCreationPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.catalog.EServiceCatalogPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.provision.EServiceProvisionCatalogPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebEServiceGateway implements EServiceGateway {

    private final EServiceProvisionCatalogPage eserviceProvisionCatalogPage;
    private final EServiceCatalogPage eserviceCatalogPage;
    private final EServiceCreationPage eServiceCreationPage;
    private final WebEServiceGeneralDataGateway generalDataGateway;

    @Override
    public EService createEService(EServiceCreationCommand command) {
        if (!(command instanceof WebEServiceCreationCommand creationCommand))
            throw new IllegalArgumentException("Invalid command type");

        eServiceCreationPage.navigateTo();
        eServiceCreationPage.assertLoaded();

        return generalDataGateway.fillEServiceGeneralData(creationCommand.getWebEServiceGeneralData());
    }

    @Override
    public EService getEService(EServiceRef eServiceRef) {
        return null;
    }

    @Override
    public void verifySubscribeButtonDisabledForPreviousVersions(EService eService) {
        // 1) vai a pagina catalogo eservice
        eserviceCatalogPage.navigateTo();
        eserviceCatalogPage.assertLoaded();

        // 2) visualizza specifica pagina eservice
//        eserviceCatalogPage.navigateToEService(eService);


        // 3) verifica presenza del button
    }

    @Override
    public void addDescriptor(EService eService) {
        // 1) vai a pagina dei eservice erogati
        //  e visualizza l'eservice in questione
        eserviceProvisionCatalogPage.navigateToEService(eService);
        eserviceProvisionCatalogPage.assertLoaded();

        // 2) crea nuovo eservice

    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }
}
