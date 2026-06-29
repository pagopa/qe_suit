package it.pagopa.interop.common.contract.template.journey;

import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;

public interface Journey extends EServiceJourney<Journey> {
    Journey withProducer(Tenant tenant, User user);
    Journey withConsumer(Tenant tenant, User user);
}
