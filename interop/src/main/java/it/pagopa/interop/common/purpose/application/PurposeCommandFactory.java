package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface PurposeCommandFactory extends Plugin<Channel> {
    PurposeCreateCommand emptyCreateCommand();
    PurposeCreateCommand validFullPopulatedCreateCommand(EServiceRef eServiceRef);
}
