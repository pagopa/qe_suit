package it.pagopa.interop.bff.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.TenantKind;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAnalysisDataInitializer {

    private final ObjectMapper objectMapper;
    private Map<String, RiskAnalysisTemplate> riskAnalysisData;

    @PostConstruct
    public void init() {
        try {
            riskAnalysisData = objectMapper.readValue(
                    new ClassPathResource("assets/risk_analysis_data.json").getInputStream(),
                    new TypeReference<>() {
                    }
            );
            log.info("Risk analysis data loaded successfully");
        } catch (Exception e) {
            throw new IllegalStateException("Error loading assets/risk_analysis_data.json", e);
        }
    }

    public record RiskAnalysisTemplate(
            Map<String, List<String>> completed,
            Map<String, List<String>> uncompleted
    ) {
    }

    public Map<String, java.util.List<String>> getTemplateForTenant(Tenant tenant, boolean completed) {
        String templateKey = tenant.getTenantType() == TenantKind.PA ? "PA" : "Privato/GSP";
        var template = riskAnalysisData.get(templateKey);

        if (template == null)
            throw new IllegalStateException("No risk analysis template for: " + templateKey);

        if (completed) return template.completed;
        else return template.uncompleted;
    }
}