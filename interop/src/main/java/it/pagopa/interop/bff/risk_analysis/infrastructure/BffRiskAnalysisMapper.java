package it.pagopa.interop.bff.risk_analysis.infrastructure;

import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.infrastructure.mapping.SharedMapper;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.DataType;
import it.pagopa.interop.generated.openapi.clients.bff.model.HideOption;
import it.pagopa.interop.generated.openapi.clients.bff.model.LocalizedText;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffRiskAnalysisMapper {

    @Mapping(
            target = "expiration",
            source = "expiration",
            qualifiedByName = "mapStringToInstant"
    )
    RiskAnalysisFormConfig toRiskAnalysisFormConfig(
            it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig source
    );

    RiskAnalysisFormConfig.Question toQuestion(
            it.pagopa.interop.generated.openapi.clients.bff.model.FormConfigQuestion source
    );

    RiskAnalysisFormConfig.Dependency toDependency(
            it.pagopa.interop.generated.openapi.clients.bff.model.Dependency source
    );

    RiskAnalysisFormConfig.Dependency toDependency(
            HideOption source
    );

    RiskAnalysisFormConfig.Validation toValidation(
            it.pagopa.interop.generated.openapi.clients.bff.model.ValidationOption source
    );

    RiskAnalysisFormConfig.Option toOption(
            it.pagopa.interop.generated.openapi.clients.bff.model.LabeledValue source
    );

    default Map<String, String> toLocalizedTextMap(LocalizedText source) {
        if (source == null) {
            return null;
        }

        return Map.of(
                "it", source.getIt(),
                "en", source.getEn()
        );
    }

    default String toDataType(DataType source) {
        return source == null ? null : source.getValue();
    }

    default Map<String, List<RiskAnalysisFormConfig.Dependency>> toHideOption(
            Map<String, List<HideOption>> source
    ) {
        if (source == null) {
            return null;
        }

        return source.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(this::toDependency)
                                .toList()
                ));
    }
}