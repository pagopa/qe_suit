package it.pagopa.interop.common.contract.model.risk_analysis;

import it.pagopa.interop.common.contract.model.TestModel;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class RiskAnalysisFormConfig implements TestModel {
    UUID id = UUID.randomUUID();
    String version;
    Instant expiration;
    List<Question> questions;

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class Question {
        String id;
        Map<String, String> label;
        Map<String, String> infoLabel;
        String dataType;
        Boolean required;
        List<Dependency> dependencies;
        String visualType;
        List<String> defaultValue;
        Map<String, List<Dependency>> hideOption;
        Validation validation;
        List<Option> options;
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class Dependency {
        String id;
        String value;
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class Validation {
        Integer maxLength;
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class Option {
        Map<String, String> label;
        String value;
    }
}