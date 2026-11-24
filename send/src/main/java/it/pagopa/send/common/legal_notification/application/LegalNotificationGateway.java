package it.pagopa.send.common.legal_notification.application;

/**
 * Contratto unico della notifica legale, in via di sostituzione con le interfacce atomiche che estende: resta finché
 * le implementazioni di ciascun canale non sono scomposte per operazione.
 */
public interface LegalNotificationGateway extends PrepareNotificationGateway, AddRecipientGateway, SendNotificationGateway,
        WaitForNotificationStatusGateway, DeleteNotificationGateway, ReadNotificationGateway, SearchNotificationGateway {
}
