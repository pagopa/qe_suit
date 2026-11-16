package it.pagopa.interop.bff.notification.infrastructure;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.notification.application.NotificationCommandFactory;
import it.pagopa.interop.generated.openapi.clients.bff.model.NotificationConfig;
import org.springframework.stereotype.Component;

@Component
public class BffNotificationRequestFactory implements NotificationCommandFactory {
    public NotificationConfig allEnabledInAppConfig() {
        return new NotificationConfig()
                .agreementSuspendedUnsuspendedToProducer(true)
                .agreementManagementToProducer(true)
                .clientAddedRemovedToProducer(true)
                .purposeStatusChangedToProducer(true)
                .templateStatusChangedToProducer(true)
                .eserviceStateChangedToProducer(true)
                .agreementSuspendedUnsuspendedToConsumer(true)
                .eserviceStateChangedToConsumer(true)
                .agreementActivatedRejectedToConsumer(true)
                .purposeActivatedRejectedToConsumer(true)
                .purposeSuspendedUnsuspendedToConsumer(true)
                .newEserviceTemplateVersionToInstantiator(true)
                .eserviceTemplateNameChangedToInstantiator(true)
                .eserviceTemplateStatusChangedToInstantiator(true)
                .delegationApprovedRejectedToDelegator(true)
                .eserviceNewVersionSubmittedToDelegator(true)
                .eserviceNewVersionApprovedRejectedToDelegate(true)
                .delegationSubmittedRevokedToDelegate(true)
                .certifiedVerifiedAttributeAssignedRevokedToAssignee(true)
                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(true)
                .purposeQuotaAdjustmentRequestToProducer(true)
                .purposeOverQuotaStateToConsumer(true)
                .purposeRiskAnalysisAssignmentStatusToAdmin(true)
                .purposeRiskAnalysisAssignmentStatusToReviewer(true)
                .purposePublishedWithRiskAnalysisToReviewer(true)
                .eserviceArchivingRequestedToDelegator(true)
                .eserviceArchivingApprovedRejectedToDelegate(true);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
