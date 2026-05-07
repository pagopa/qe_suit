package it.pagopa.interop.domain.services.purpose;

public interface PurposeService {
    Purpose createEservicePurpose(Eservice eservice, PurposeCreationRequest request);
    Purpose associatePurposeToClient(Purpose purpose, Client client);
}
