package it.pagopa.interop.web.eservice.infrastructure;

import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.web.eservice.application.WebEServiceCreationCommand;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebEServiceGateway implements EServiceGateway {

    private final EServiceCreationPage eServiceCreationPage;
    private final WebEServiceGeneralDataGateway generalDataGateway;

    @Override
    public EService createEService(EServiceCreationCommand command) {
        if (!(command instanceof WebEServiceCreationCommand creationCommand))
            throw new IllegalArgumentException("Invalid command type");

        eServiceCreationPage.navigateTo();
        eServiceCreationPage.assertLoaded();

        generalDataGateway.fillEServiceGeneralData(creationCommand.getWebEServiceGeneralData());

        return generalDataGateway.readEServiceGeneralData();
    }

    @Override
    public EService getEService(EServiceRef eServiceRef) {
        return null;
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }
}
