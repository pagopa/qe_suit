package it.pagopa.send.bff.delegation.infrastructure;

import it.pagopa.send.common.delegation.application.DelegationCreationCommand;
import it.pagopa.send.common.delegation.domain.Delegation;
import it.pagopa.send.common.delegation.domain.DelegationStatus;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.generated.openapi.clients.mandate.model.BffAcceptRequest;
import it.pagopa.send.generated.openapi.clients.mandate.model.BffNewMandateRequest;
import it.pagopa.send.generated.openapi.clients.mandate.model.BffSearchMandateRequest;
import it.pagopa.send.generated.openapi.clients.mandate.model.MandateDto;
import it.pagopa.send.generated.openapi.clients.mandate.model.UserDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Conversioni tra il dominio delle deleghe e i DTO OpenAPI del BFF.
 */
@Component
public class BffDelegationMapper {

    public BffSearchMandateRequest toSearchRequest(User delegator, List<DelegationStatus> statuses) {
        return new BffSearchMandateRequest()
                .taxId(delegator.getTaxId())
                .status(statuses.stream().map(DelegationStatus::getValue).toList());
    }

    // il delegato è un'impresa: la delega è intestata alla ragione sociale e alla partita IVA
    public BffNewMandateRequest toNewMandateRequest(User delegate, DelegationCreationCommand command) {
        return new BffNewMandateRequest()
                .delegate(new UserDto()
                        .displayName(delegate.getOrganization())
                        .companyName(delegate.getOrganization())
                        .fiscalCode(delegate.getTaxId())
                        .person(false))
                .verificationCode(command.verificationCode())
                .dateto(command.dateTo().toString());
    }

    public BffAcceptRequest toAcceptRequest(String verificationCode) {
        return new BffAcceptRequest().verificationCode(verificationCode);
    }

    public Delegation toDomain(MandateDto mandate) {
        return Delegation.builder()
                .mandateId(mandate.getMandateId())
                .delegatorTaxId(mandate.getDelegator() == null ? null : mandate.getDelegator().getFiscalCode())
                .status(mandate.getStatus() == null ? DelegationStatus.UNKNOWN : DelegationStatus.fromValue(mandate.getStatus().getValue()))
                .build();
    }
}
