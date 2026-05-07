package it.pagopa.interop.domain.services.risk_analysis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RiskAnalysisDataInitializer {

    private final ObjectMapper objectMapper;

    @Getter
    private Map<String, RiskAnalysisTemplate> riskAnalysisData;

    @PostConstruct
    public void init() {
        try {
            riskAnalysisData = objectMapper.readValue(
                    new ClassPathResource("assets/risk_analysis_data.json").getInputStream(),
                    new TypeReference<>() {}
            );
            log.info("Risk analysis data loaded successfully");
        } catch (Exception e) {
            throw new IllegalStateException("Error loading risk_analysis_data.json", e);
        }
    }

    public record RiskAnalysisTemplate(
            RiskAnalysisAnswers completed,
            RiskAnalysisAnswers uncompleted
    ) {}

    public record RiskAnalysisAnswers(
            Map<String, List<String>> answers
    ) {
        public Map<String, List<String>> toMap() {
            return answers;
        }
    }
}