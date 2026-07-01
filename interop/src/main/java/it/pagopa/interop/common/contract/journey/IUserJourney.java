package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;
import it.pagopa.interop.common.contract.model.shared.enums.UserRole;

public interface IUserJourney<SELF extends IUserJourney<SELF>> {
    SELF withProducer(Tenant tenant, User user);

    SELF withConsumer(Tenant tenant, User user);

    SELF withProducer(Tenant tenant, UserRole role);

    SELF withConsumer(Tenant tenant, UserRole role);
}
