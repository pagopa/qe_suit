package it.pagopa.interop.common.purpose.application;

public interface RejectRiskAnalysisCommand {
    RejectRiskAnalysisCommand rejectionReason(String rejectionReason);

    String getRejectionReason();
}
