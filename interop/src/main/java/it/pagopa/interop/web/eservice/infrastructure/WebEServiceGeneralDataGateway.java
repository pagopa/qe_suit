package it.pagopa.interop.web.eservice.infrastructure;

import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.kernel.utils.async.DelayUtils;
import it.pagopa.interop.web.eservice.application.WebEServiceGeneralData;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCreationPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard.GeneralDataWizard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebEServiceGeneralDataGateway {

    private final IWebPresentationApiAdapter adapter;
    private final EServiceCreationPage eServiceCreationPage;

    public EService fillEServiceGeneralData(WebEServiceGeneralData model) {
        validateAsyncExchangeMode(model);
        GeneralDataWizard generalDataWizard = eServiceCreationPage.generalDataStep();

        generalDataWizard
                .setName(model.eservice().getName())
                .setDescription(model.eservice().getDescription())
                .setAsyncExchange(model.eservice().getAsyncExchange())
                .setTechnology(model.eservice().getTechnology())
                .setPersonalData(model.eservice().getPersonalData())
                .setMode(model.eservice().getMode());

        EService eService = readEServiceGeneralData(generalDataWizard);

        eServiceCreationPage.saveDraftButton().click();

        // attendi il redirect e leggi l'URL corrente
        EServiceUrlIds ids = waitAndParseEServiceUrl(Duration.ofSeconds(10), Duration.ofMillis(500));
        return getFullEServiceGeneralData(eService, ids);
    }

    private record EServiceUrlIds(UUID eserviceId, UUID descriptorId) {}

    private EServiceUrlIds waitAndParseEServiceUrl(Duration timeout, Duration pollInterval) {
        final Pattern ESERVICE_URL = Pattern.compile("/e-service/([0-9a-fA-F-]{36})/([0-9a-fA-F-]{36})");
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        String lastUrl = null;

        while (System.currentTimeMillis() < deadline) {
            lastUrl = adapter.getLocation().getUrl();
            Matcher m = ESERVICE_URL.matcher(lastUrl);
            if (m.find()) {
                return new EServiceUrlIds(
                        UUID.fromString(m.group(1)),
                        UUID.fromString(m.group(2)));
            }
            DelayUtils.waitForMillis(pollInterval.toMillis());
        }

        throw new IllegalStateException(
                "Redirect e-service non avvenuto entro " + timeout + ". Ultimo URL: " + lastUrl);
    }

    private EService getFullEServiceGeneralData(EService eService, EServiceUrlIds ids) {
        UUID eserviceId = ids.eserviceId();
        UUID descriptorId = ids.descriptorId();

        List<EServiceDescriptor> descriptors = eService.getDescriptors();
        int size = descriptors.size();
        descriptors.add(
                // TODO valutare se va aggiunto altro
                EServiceDescriptor.builder()
                        .id(descriptorId)
                        .version(String.valueOf(size+1))
                        .state(EServiceDescriptorState.DRAFT)
                        .build()
        );

        return EService.builder()
                .name(eService.getName())
                .description(eService.getDescription())
                .technology(eService.getTechnology())
                .asyncExchange(eService.getAsyncExchange())
                .mode(eService.getMode())
                .personalData(eService.getPersonalData())
                .descriptors(descriptors)
                .id(eserviceId)
                .build();
    }

    private EService readEServiceGeneralData(GeneralDataWizard generalDataWizard) {
        return EService.builder()
                .name(generalDataWizard.name().read())
                .description(generalDataWizard.description().read())
                .technology(EServiceTechnology.valueOf(generalDataWizard.getTechnology().name()))
                .asyncExchange(generalDataWizard.getAsyncExchange())
                .mode(EServiceMode.valueOf(generalDataWizard.getMode().name()))
                .personalData(generalDataWizard.getPersonalData())
                .descriptors(new ArrayList<>())
                .build();
    }

    private void validateAsyncExchangeMode(WebEServiceGeneralData model) {
        boolean hasAsyncExchange = Boolean.TRUE.equals(model.eservice().getAsyncExchange());
        if (hasAsyncExchange) {
            throw new IllegalStateException(
                    "Cannot set MODE for an async eService, but got: " + model.eservice().getMode()
            );
        }
    }
}