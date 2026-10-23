package it.pagopa.interop.common.notification.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;

public interface CatalogNotificationJourney {
    void enableFor(Tenant consumer);
    void verifyUnreadFor(Tenant consumer, EService eService);
}
