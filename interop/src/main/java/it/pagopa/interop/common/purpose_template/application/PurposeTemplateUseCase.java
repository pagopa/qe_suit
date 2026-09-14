package it.pagopa.interop.common.purpose_template.application;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurposeTemplateUseCase {

    private final PurposeTemplateGateway purposeTemplateGateway;

    public PurposeTemplate createPurposeTemplate(Tenant creator) {
        return purposeTemplateGateway.createPurposeTemplate(creator);
    }

    public void assertGeneralInformationPageDisplayed(Tenant creator) {
        purposeTemplateGateway.assertGeneralInformationPageDisplayed(creator);
    }
}

