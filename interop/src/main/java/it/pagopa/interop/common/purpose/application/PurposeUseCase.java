package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PurposeUseCase {
    private final PurposeGateway purposeGateway;
    private final PurposeCommandFactory purposeCommandFactory;

    public Purpose addDraftPurpose(Consumer<PurposeCreateCommand> commandConfig) {
        PurposeCreateCommand defaultCommand = purposeCommandFactory.emptyCreateCommand();
        commandConfig.accept(defaultCommand);

        return purposeGateway.createPurpose(defaultCommand);
    }

    public Purpose addDraftPurpose(EService eService) {
        PurposeCreateCommand command = purposeCommandFactory.validFullPopulatedCreateCommand(eService);
        return purposeGateway.createPurpose(command);
    }

    public Purpose activatePurpose(Purpose purpose, PurposeVersion purposeVersion, @Nullable DelegationRef delegationRef) {
        return purposeGateway.activatePurpose(purpose.getRef(), purposeVersion.getRef(), delegationRef);
    }

    public Purpose suspendPurpose(Purpose purpose, PurposeVersion purposeVersion, @Nullable DelegationRef delegationRef) {
        return purposeGateway.suspendPurpose(purpose.getRef(), purposeVersion.getRef(), delegationRef);
    }
}
