package it.pagopa.interop.common.purpose.application;

public interface SignRiskAnalysisCommand {
    SignRiskAnalysisCommand metadataVersionToSign(Integer metadataVersionToSign);

    Integer getMetadataVersionToSign();
}
