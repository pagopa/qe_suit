package it.pagopa.interop.m2m.eservice_template.infrastructure;

import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceTemplateVersion;
import org.mapstruct.Mapper;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class /*, M2MCommonMapper.class*/}
)
public interface M2MEServiceTemplateVersionMapper {

    //@Mapping(target = "version", expression = "java(source.getVersion() == null ? null : String.valueOf(source.getVersion()))")
    //    @Mapping(target = "interfaceDocument", expression = "java(toInterface(source.getInterface(), source.getEserviceTemplate().getId(), source.getId()))")
    //    @Mapping(target = "createdAt", ignore = true)
    //    @Mapping(target = "publishedAt", ignore = true)
    //    @Mapping(target = "suspendedAt", ignore = true)
    //    @Mapping(target = "archivedAt", ignore = true)
    //    @Mapping(target = "docs", source = "docs")
    //    @Mapping(target = "audience", expression = "java(new java.util.ArrayList<>())")
    //    @Mapping(target = "serverUrls", expression = "java(new java.util.ArrayList<>())")
    //    EServiceTemplateVersion toTemplateVersion(EServiceTemplateVersionDetails source);
    //
    //    @Mapping(target = "id", source = "eserviceTemplate.id")
    //    @Mapping(target = "creatorId", source = "eserviceTemplate.creator.id")
    //    @Mapping(target = "name", source = "eserviceTemplate.name")
    //    @Mapping(target = "description", source = "eserviceTemplate.description")
    //    @Mapping(target = "intendedTarget", source = "eserviceTemplate.intendedTarget")
    //    @Mapping(target = "technology", source = "eserviceTemplate.technology")
    //    @Mapping(target = "mode", source = "eserviceTemplate.mode")
    //    @Mapping(target = "state", source = "eserviceTemplate", qualifiedByName = "resolveTemplateState")
    //    @Mapping(target = "personalData", source = "eserviceTemplate.personalData")
    //    @Mapping(target = "createdAt", ignore = true)
    //    @Mapping(target = "updatedAt", ignore = true)
    //    @Mapping(target = "versions", ignore = true)
    //    @Mapping(target = "version", ignore = true)
    //    @Mapping(target = "riskAnalyses", ignore = true)
    //    @Mapping(target = "riskAnalysis", ignore = true)
    //    EServiceTemplate toTemplateBase(EServiceTemplateVersionDetails source);
    //
    //    @Mapping(target = "certified", source = "certified", qualifiedByName = "flattenAttributes")
    //    @Mapping(target = "declared", source = "declared", qualifiedByName = "flattenAttributes")
    //    @Mapping(target = "verified", source = "verified", qualifiedByName = "flattenAttributes")
    //    Attributes toAttributes(DescriptorAttributes source);
    //
    //    it.pagopa.interop.common.attribute.domain.Attribute toAttribute(DescriptorAttribute source);
    //
    //    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "UNKNOWN")
    //    EServiceTemplateVersionState toDomainVersionState(
    //            it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState source
    //    );

    default EServiceTemplate toTemplateWithUpsertVersion(
            EServiceTemplateVersion source,
            EServiceTemplate existingTemplate
    ) {
        if (source == null) {
            return existingTemplate;
        }

        //        EServiceTemplate updatedBase = toTemplateBase(source);
        //        EServiceTemplateVersion newVersion = toTemplateVersion(source);

        //        List<EServiceTemplateVersion> finalVersions = new ArrayList<>();
        //        if (existingTemplate != null && existingTemplate.getVersions() != null) {
        //            finalVersions.addAll(existingTemplate.getVersions());
        //        }

        //        upsertVersion(finalVersions, newVersion);

        //        List<it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis> riskAnalyses =
        //                existingTemplate != null && existingTemplate.getRiskAnalyses() != null
        //                        ? new ArrayList<>(existingTemplate.getRiskAnalyses())
        //                        : new ArrayList<>();

        //        return updatedBase.toBuilder()
        //                .versions(new ArrayList<>(finalVersions))
        //                .riskAnalyses(riskAnalyses)
        //                .build();
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
