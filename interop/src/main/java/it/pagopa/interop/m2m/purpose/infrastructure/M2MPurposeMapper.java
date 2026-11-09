package it.pagopa.interop.m2m.purpose.infrastructure;

import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.purpose.application.PurposeCreateCommand;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class}
)
public interface M2MPurposeMapper {

    @Mapping(target = "versions", expression = "java(mapVersions(source))")
    Purpose toPurpose(it.pagopa.interop.generated.openapi.clients.m2m.v3.model.Purpose source);

    @Mapping(target = "purposeVersionState", source = "state")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "suspendedAt", source = "suspendedAt", qualifiedByName = "mapStringToInstant")
    PurposeVersion toPurposeVersion(it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeVersion source);

    @Mapping(target = "eserviceId", expression = "java(createCommand.eserviceId())")
    @Mapping(target = "delegationId", ignore = true)
    @Mapping(target = "title", expression = "java(createCommand.title())")
    @Mapping(target = "description", expression = "java(createCommand.description())")
    @Mapping(target = "isFreeOfCharge", expression = "java(createCommand.isFreeOfCharge())")
    @Mapping(target = "freeOfChargeReason", expression = "java(createCommand.freeOfChargeReason())")
    @Mapping(target = "dailyCalls", expression = "java(createCommand.dailyCalls())")
    @Mapping(target = "riskAnalysisForm", expression = "java(toM2MRiskAnalysisFormSeed(createCommand.riskAnalysisForm()))")
    it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeSeed toM2MPurposeSeed(PurposeCreateCommand createCommand);

    it.pagopa.interop.generated.openapi.clients.m2m.v3.model.RiskAnalysisFormSeed toM2MRiskAnalysisFormSeed(RiskAnalysisForm source);

    default List<PurposeVersion> mapVersions(it.pagopa.interop.generated.openapi.clients.m2m.v3.model.Purpose source) {
        return Stream.of(source.getCurrentVersion(), source.getWaitingForApprovalVersion(), source.getRejectedVersion())
                .filter(Objects::nonNull)
                .map(this::toPurposeVersion)
                .toList();
    }
}
