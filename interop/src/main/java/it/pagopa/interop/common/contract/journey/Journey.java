package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;
import org.springframework.plugin.core.Plugin;

public interface Journey extends
        EServiceJourney<Journey>, AgreementJourney<Journey>, PurposeJourney<Journey>,
        Plugin<Channel> {
    Journey withProducer(Tenant tenant, User user);
    Journey withConsumer(Tenant tenant, User user);
}
