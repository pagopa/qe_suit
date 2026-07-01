package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;

public interface IBaseJourney<SELF extends IBaseJourney<SELF>> extends
        IEServiceJourney<SELF>, IAgreementJourney<SELF>, IClientJourney<SELF>, IPurposeJourney<SELF> {

    SELF withProducer(Tenant tenant, User user);

    SELF withConsumer(Tenant tenant, User user);
}