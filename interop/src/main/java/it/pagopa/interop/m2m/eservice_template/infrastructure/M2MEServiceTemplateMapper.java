package it.pagopa.interop.m2m.eservice_template.infrastructure;

import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = TestMapperConfig.class)
public interface M2MEServiceTemplateMapper {

    @Mapping(target = "id", source = "source.id")
    @Mapping(target = "creatorId", source = "source.creatorId")
    @Mapping(target = "name", source = "source.name")
    @Mapping(target = "intendedTarget", source = "source.intendedTarget")
    @Mapping(target = "description", source = "source.description")
    @Mapping(target = "technology", source = "source.technology")
    @Mapping(target = "mode", source = "source.mode")
    @Mapping(target = "isSignalHubEnabled", source = "source.isSignalHubEnabled")
    @Mapping(target = "personalData", source = "source.personalData")
    @Mapping(target = "state", expression = "java(preserveState(existing))")
    @Mapping(target = "asyncExchange", expression = "java(existing == null ? null : existing.getAsyncExchange())")
    @Mapping(target = "createdAt", expression = "java(existing == null ? null : existing.getCreatedAt())")
    @Mapping(target = "updatedAt", expression = "java(existing == null ? null : existing.getUpdatedAt())")
    @Mapping(target = "versions", expression = "java(copyVersions(existing))")
    @Mapping(target = "riskAnalyses", expression = "java(copyRiskAnalyses(existing))")
    EServiceTemplate toTemplate(
            it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceTemplate source,
            EServiceTemplate existing
    );

    default EServiceTemplateState preserveState(EServiceTemplate existing) {
        return existing == null ? EServiceTemplateState.UNKNOWN : existing.getState();
    }

    default List<EServiceTemplateVersion> copyVersions(EServiceTemplate existing) {
        return existing == null || existing.getVersions() == null
                ? new ArrayList<>()
                : new ArrayList<>(existing.getVersions());
    }

    default List<EServiceRiskAnalysis> copyRiskAnalyses(EServiceTemplate existing) {
        return existing == null || existing.getRiskAnalyses() == null
                ? new ArrayList<>()
                : new ArrayList<>(existing.getRiskAnalyses());
    }

}
