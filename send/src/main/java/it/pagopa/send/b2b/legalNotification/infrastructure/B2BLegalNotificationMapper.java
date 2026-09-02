package it.pagopa.send.b2b.legalNotification.infrastructure;

import it.pagopa.send.common.domain.UserType;
import it.pagopa.send.common.infrastructure.config.SendMapperConfig;
import it.pagopa.send.common.notification.domain.DocumentRef;
import it.pagopa.send.common.notification.domain.FeePolicy;
import it.pagopa.send.common.notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.notification.domain.NotificationRecipientSummary;
import it.pagopa.send.common.notification.domain.NotificationStatus;
import it.pagopa.send.common.notification.domain.PagoPaIntMode;
import it.pagopa.send.common.notification.domain.PhysicalCommunicationType;
import it.pagopa.send.common.notification.domain.ResolvedF24Payment;
import it.pagopa.send.common.notification.domain.ResolvedPagoPaPayment;
import it.pagopa.send.common.notification.domain.ResolvedPayment;
import it.pagopa.send.common.notification.domain.ResolvedRecipient;
import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.generated.openapi.clients.bff.model.F24Payment;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationAttachmentBodyRef;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationAttachmentDigests;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationDigitalAddress;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationDocument;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationFeePolicy;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationMetadataAttachment;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationPaymentAttachment;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationPaymentItem;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationPhysicalAddress;
import it.pagopa.send.generated.openapi.clients.bff.model.NotificationRecipientV24;
import it.pagopa.send.generated.openapi.clients.bff.model.PagoPaPayment;
import it.pagopa.send.model.NotificationDefaults;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Unico punto del canale B2B che conosce sia il dominio interno ({@link
 * LegalNotificationCreationRequest}/{@link LegalNotificationDomain}) sia il DTO OpenAPI del BFF.
 * Le conversioni enum-to-enum a nomi allineati ({@link #toBffStatus}, {@link #toDomainStatus},
 * {@link #toBffFeePolicy}, ecc.) e la lista di {@link NotificationRecipientSummary} (campi
 * identici a {@code NotificationRecipientV24}) sono generate da MapStruct; le due traduzioni
 * strutturalmente più complesse ({@link #toBffRequest}, {@link #toDomain}) sono metodi default che
 * compongono quelle generate.
 */
@Mapper(config = SendMapperConfig.class)
public interface B2BLegalNotificationMapper {

    NotificationStatus toDomainStatus(BffNotificationStatus status);

    BffNotificationStatus toBffStatus(NotificationStatus status);

    NotificationFeePolicy toBffFeePolicy(FeePolicy feePolicy);

    BffNewNotificationRequest.PhysicalCommunicationTypeEnum toBffPhysicalCommunicationType(PhysicalCommunicationType type);

    BffNewNotificationRequest.PagoPaIntModeEnum toBffPagoPaIntMode(PagoPaIntMode mode);

    NotificationRecipientSummary toRecipientSummary(NotificationRecipientV24 recipient);

    List<NotificationRecipientSummary> toRecipientSummaries(List<NotificationRecipientV24> recipients);

    default LegalNotificationDomain toDomain(BffFullNotificationV1 source) {
        if (source == null) {
            return null;
        }
        return LegalNotificationDomain.builder()
                .iun(source.getIun())
                .senderTaxId(source.getSenderTaxId())
                .senderDenomination(source.getSenderDenomination())
                .subject(source.getSubject())
                .status(toDomainStatus(source.getNotificationStatus()))
                .recipients(toRecipientSummaries(source.getRecipients()))
                .build();
    }

    default BffNewNotificationRequest toBffRequest(LegalNotificationCreationRequest source) {
        BffNewNotificationRequest request = new BffNewNotificationRequest()
                .paProtocolNumber(source.getPaProtocolNumber())
                .subject(source.getSubject())
                ._abstract(source.getAbstractText())
                .addDocumentsItem(toDocument(source.getDocument()))
                .notificationFeePolicy(toBffFeePolicy(source.getFeePolicy()))
                .physicalCommunicationType(toBffPhysicalCommunicationType(source.getPhysicalCommunication()))
                .senderDenomination(source.getSenderDenomination())
                .senderTaxId(source.getSenderTaxId())
                .group(source.getGroup())
                .taxonomyCode(source.getTaxonomyCode())
                .paFee(source.getPaFee())
                .vat(source.getVat())
                .pagoPaIntMode(toBffPagoPaIntMode(source.getPagoPaIntMode()))
                .physicalCommunicationPriority(source.getPhysicalCommunicationPriority())
                .additionalLanguages(source.getAdditionalLanguages())
                .cancelledIun(source.getCancelledIun())
                .amount(source.getAmount())
                .paymentExpirationDate(source.getPaymentExpirationDate());

        source.getRecipients().forEach(recipient -> request.addRecipientsItem(toRecipient(recipient)));
        return request;
    }

    default NotificationDocument toDocument(DocumentRef ref) {
        if (ref == null) {
            return null;
        }
        return new NotificationDocument()
                .digests(new NotificationAttachmentDigests().sha256(ref.sha256()))
                .contentType(ref.contentType())
                .ref(new NotificationAttachmentBodyRef().key(ref.key()).versionToken(ref.versionToken()));
    }

    default NotificationRecipientV24 toRecipient(ResolvedRecipient recipient) {
        List<NotificationPaymentItem> paymentItems = recipient.payments().stream()
                .map(this::toPaymentItem)
                .toList();

        return new NotificationRecipientV24()
                .recipientType(toRecipientType(recipient.type()))
                .taxId(recipient.taxId())
                .denomination(recipient.denomination())
                .physicalAddress(toPhysicalAddress(recipient.physicalAddress()))
                .digitalDomicile(toDigitalDomicile(recipient.digitalDomicile()))
                .payments(paymentItems.isEmpty() ? null : paymentItems);
    }

    /**
     * Un destinatario di notifica è per costruzione PF o PG, mai PA.
     */
    default NotificationRecipientV24.RecipientTypeEnum toRecipientType(UserType type) {
        return switch (type) {
            case PF -> NotificationRecipientV24.RecipientTypeEnum.PF;
            case PG -> NotificationRecipientV24.RecipientTypeEnum.PG;
            case PA -> throw new IllegalStateException(type + " non può essere destinatario di una notifica");
        };
    }

    default NotificationPhysicalAddress toPhysicalAddress(NotificationDefaults.PhysicalAddressDefaults address) {
        if (address == null) {
            return null;
        }
        return new NotificationPhysicalAddress()
                .at(address.at())
                .address(address.address())
                .addressDetails(address.addressDetails())
                .zip(address.zip())
                .municipality(address.municipality())
                .municipalityDetails(address.municipalityDetails())
                .province(address.province())
                .foreignState(address.foreignState());
    }

    default NotificationDigitalAddress toDigitalDomicile(String address) {
        if (address == null) {
            return null;
        }
        return new NotificationDigitalAddress().type(NotificationDigitalAddress.TypeEnum.PEC).address(address);
    }

    default NotificationPaymentItem toPaymentItem(ResolvedPayment payment) {
        if (payment instanceof ResolvedPagoPaPayment pagoPa) {
            return new NotificationPaymentItem().pagoPa(toPagoPaPayment(pagoPa));
        }
        if (payment instanceof ResolvedF24Payment f24) {
            return new NotificationPaymentItem().f24(toF24Payment(f24));
        }
        throw new IllegalArgumentException("Tipo di pagamento non gestito: " + payment.getClass());
    }

    default PagoPaPayment toPagoPaPayment(ResolvedPagoPaPayment payment) {
        return new PagoPaPayment()
                .noticeCode(payment.noticeCode())
                .creditorTaxId(payment.creditorTaxId())
                .applyCost(false)
                .attachment(toPaymentAttachment(payment.attachment()));
    }

    default F24Payment toF24Payment(ResolvedF24Payment payment) {
        return new F24Payment()
                .title(payment.title())
                .applyCost(false)
                .metadataAttachment(toMetadataAttachment(payment.attachment()));
    }

    default NotificationPaymentAttachment toPaymentAttachment(DocumentRef ref) {
        return new NotificationPaymentAttachment()
                .digests(new NotificationAttachmentDigests().sha256(ref.sha256()))
                .contentType(ref.contentType())
                .ref(new NotificationAttachmentBodyRef().key(ref.key()).versionToken(ref.versionToken()));
    }

    default NotificationMetadataAttachment toMetadataAttachment(DocumentRef ref) {
        return new NotificationMetadataAttachment()
                .digests(new NotificationAttachmentDigests().sha256(ref.sha256()))
                .contentType(ref.contentType())
                .ref(new NotificationAttachmentBodyRef().key(ref.key()).versionToken(ref.versionToken()));
    }
}
