package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.journey.application.EServiceTemplateJourney;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EServiceTemplateJourneyImpl implements EServiceTemplateJourney<EServiceTemplateJourneyImpl> {
    private final EServiceTemplateUseCase useCase;

    @Override
    public EServiceTemplateJourneyImpl createPublishedEServiceTemplate(
            EServiceTechnology technology,
            String interfaceFixture
    ) {
        log.info("PIN-10024 template setup started: fixture={}, technology={}", interfaceFixture, technology);
        var template = useCase.createEServiceTemplate(command -> command.technology(technology));
        log.info("PIN-10024 template created: templateId={}, versionId={}",
                template.getId(), template.getVersions().get(0).getId());
        template = useCase.uploadInterface(template, interfaceFixture);
        template = useCase.publishEServiceTemplateVersion(template);
        useCase.downloadInterface(template);
        return this;
    }
}
