package it.pagopa.interop.new_arch.common.eservice.application;

import it.pagopa.interop.new_arch.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface EServiceGateway extends Plugin<Channel> {
    EService createEService(EServiceCreationCommand command);

    EService getEService(EServiceRef eServiceRef);
}
