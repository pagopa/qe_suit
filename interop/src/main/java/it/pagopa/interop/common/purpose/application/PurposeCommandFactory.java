package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface PurposeCommandFactory extends Plugin<Channel> {
    PurposeCreateCommand emptyCreateCommand();
    PurposeCreateCommand validFullPopulatedCreateCommand(EService eService);
}
