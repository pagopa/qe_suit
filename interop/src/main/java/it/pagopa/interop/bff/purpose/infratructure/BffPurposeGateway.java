package it.pagopa.interop.bff.purpose.infratructure;

import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.purpose.application.PurposeGateway;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffPurposeGateway implements PurposeGateway {

    @Override
    public Purpose createPurpose(EServiceRef eServiceRef, Tenant consumer, RiskAnalysisForm riskAnalysisForm) {
        return null;
    }

    @Override
    public Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef) {
        return null;
    }

    @Override
    public Purpose suspendPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef) {
        return null;
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
