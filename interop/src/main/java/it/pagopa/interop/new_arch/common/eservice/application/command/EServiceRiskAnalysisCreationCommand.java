package it.pagopa.interop.new_arch.common.eservice.application.command;

import java.util.List;
import java.util.Map;

public interface EServiceRiskAnalysisCreationCommand {

    EServiceRiskAnalysisCreationCommand version(String version);

    EServiceRiskAnalysisCreationCommand answer(Map<String, List<String>> answer);
}
