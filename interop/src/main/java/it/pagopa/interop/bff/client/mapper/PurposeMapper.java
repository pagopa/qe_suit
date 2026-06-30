package it.pagopa.interop.bff.client.mapper;

import it.pagopa.interop.bff.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.FormConfigQuestion;
import it.pagopa.interop.generated.openapi.clients.bff.model.HideOption;
import it.pagopa.interop.generated.openapi.clients.bff.model.LabeledValue;
import it.pagopa.interop.generated.openapi.clients.bff.model.LocalizedText;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(config = TestMapperConfig.class, uses = { SharedMapperUtils.class })
public interface PurposeMapper {

    it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysisFormConfig toRiskAnalysis(
            RiskAnalysisFormConfig riskAnalysisFormConfig
    );

    @Mapping(target = "dataType", expression = "java(source.getDataType() != null ? source.getDataType().getValue() : null)")
    @Mapping(target = "hideOption", expression = "java(mapHideOption(source.getHideOption()))")
    it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysisFormConfig.Question mapQuestion(
            FormConfigQuestion source
    );

    it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysisFormConfig.Dependency mapDependency(
            it.pagopa.interop.generated.openapi.clients.bff.model.Dependency source
    );

    it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysisFormConfig.Dependency mapDependency(
            HideOption source
    );

    it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysisFormConfig.Validation mapValidation(
            it.pagopa.interop.generated.openapi.clients.bff.model.ValidationOption source
    );

    it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysisFormConfig.Option mapOption(
            LabeledValue source
    );

    default Map<String, String> map(LocalizedText source) {
        if (source == null) {
            return null;
        }

        return Map.of(
                "it", source.getIt(),
                "en", source.getEn()
        );
    }

    default Map<String, List<it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysisFormConfig.Dependency>> mapHideOption(
            Map<String, List<HideOption>> source
    ) {
        if (source == null) {
            return null;
        }

        return source.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .map(this::mapDependency)
                                .toList()
                ));
    }
}