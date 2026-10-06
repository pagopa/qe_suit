package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.DocumentRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handwritten mapper: the template domain only needs the fields observed by the tests.
 */
@Component
public class BffEServiceTemplateMapper {

    public EServiceTemplate toEServiceTemplate(EServiceTemplateVersionDetails source) {
        EServiceTemplateDetails template = source.getEserviceTemplate();
        EServiceTemplateVersionState versionState = toVersionState(source);

        EServiceTemplateVersion version = EServiceTemplateVersion.builder()
                .id(source.getId())
                .version(source.getVersion() == null ? null : String.valueOf(source.getVersion()))
                .state(versionState)
                .voucherLifespan(source.getVoucherLifespan())
                .dailyCallsPerConsumer(source.getDailyCallsPerConsumer())
                .dailyCallsTotal(source.getDailyCallsTotal())
                .interfaceDocument(source.getInterface() == null ? null : new DocumentRef(source.getInterface().getId()))
                .docs(source.getDocs() == null ? List.of() : source.getDocs().stream().map(doc -> new DocumentRef(doc.getId())).toList())
                .build();

        return EServiceTemplate.builder()
                .id(template.getId())
                .creatorId(template.getCreator() == null ? null : template.getCreator().getId())
                .name(template.getName())
                .description(template.getDescription())
                .intendedTarget(template.getIntendedTarget())
                .technology(EServiceTechnology.valueOf(template.getTechnology().getValue()))
                .mode(EServiceMode.valueOf(template.getMode().getValue()))
                .personalData(template.getPersonalData())
                .state(EServiceTemplateState.valueOf(versionState.name()))
                .version(version)
                .build();
    }

    private EServiceTemplateVersionState toVersionState(EServiceTemplateVersionDetails source) {
        if (source.getState() == null) {
            return EServiceTemplateVersionState.UNKNOWN;
        }
        try {
            return EServiceTemplateVersionState.valueOf(source.getState().getValue());
        } catch (IllegalArgumentException e) {
            return EServiceTemplateVersionState.UNKNOWN;
        }
    }
}

