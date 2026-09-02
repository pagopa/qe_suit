package it.pagopa.send.common.notification.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Richiesta di creazione di una notifica legale, agnostica dal canale e dal DTO OpenAPI
 * (equivalente interno di {@code BffNewNotificationRequest}). Costruita da
 * {@code LegalNotificationRequestFactory}; solo l'implementazione del gateway del canale scelto
 * (es. {@code B2BLegalNotificationGateway}) la traduce nel DTO OpenAPI del proprio canale.
 * <p>
 * Espone setter fluenti con lo stesso nome dei campi (nessun prefisso {@code set}) così che
 * {@code RequestOverrideApplier} (già generico via reflection) continui a funzionare invariato,
 * applicato a questo oggetto invece che al DTO OpenAPI.
 */
public class LegalNotificationCreationRequest {

    private String paProtocolNumber;
    private String subject;
    private String abstractText;
    private DocumentRef document;
    private FeePolicy feePolicy;
    private PhysicalCommunicationType physicalCommunication;
    private String senderDenomination;
    private String senderTaxId;
    private String group;
    private String taxonomyCode;
    private Integer paFee;
    private Integer vat;
    private PagoPaIntMode pagoPaIntMode;
    private Integer physicalCommunicationPriority;
    private List<String> additionalLanguages;
    private String cancelledIun;
    private Integer amount;
    private String paymentExpirationDate;
    private final List<ResolvedRecipient> recipients = new ArrayList<>();

    public LegalNotificationCreationRequest paProtocolNumber(String paProtocolNumber) {
        this.paProtocolNumber = paProtocolNumber;
        return this;
    }

    public LegalNotificationCreationRequest subject(String subject) {
        this.subject = subject;
        return this;
    }

    public LegalNotificationCreationRequest abstractText(String abstractText) {
        this.abstractText = abstractText;
        return this;
    }

    public LegalNotificationCreationRequest document(DocumentRef document) {
        this.document = document;
        return this;
    }

    public LegalNotificationCreationRequest feePolicy(FeePolicy feePolicy) {
        this.feePolicy = feePolicy;
        return this;
    }

    public LegalNotificationCreationRequest physicalCommunication(PhysicalCommunicationType physicalCommunication) {
        this.physicalCommunication = physicalCommunication;
        return this;
    }

    public LegalNotificationCreationRequest senderDenomination(String senderDenomination) {
        this.senderDenomination = senderDenomination;
        return this;
    }

    public LegalNotificationCreationRequest senderTaxId(String senderTaxId) {
        this.senderTaxId = senderTaxId;
        return this;
    }

    public LegalNotificationCreationRequest group(String group) {
        this.group = group;
        return this;
    }

    public LegalNotificationCreationRequest taxonomyCode(String taxonomyCode) {
        this.taxonomyCode = taxonomyCode;
        return this;
    }

    public LegalNotificationCreationRequest paFee(Integer paFee) {
        this.paFee = paFee;
        return this;
    }

    public LegalNotificationCreationRequest vat(Integer vat) {
        this.vat = vat;
        return this;
    }

    public LegalNotificationCreationRequest pagoPaIntMode(PagoPaIntMode pagoPaIntMode) {
        this.pagoPaIntMode = pagoPaIntMode;
        return this;
    }

    public LegalNotificationCreationRequest physicalCommunicationPriority(Integer physicalCommunicationPriority) {
        this.physicalCommunicationPriority = physicalCommunicationPriority;
        return this;
    }

    public LegalNotificationCreationRequest additionalLanguages(List<String> additionalLanguages) {
        this.additionalLanguages = additionalLanguages;
        return this;
    }

    public LegalNotificationCreationRequest cancelledIun(String cancelledIun) {
        this.cancelledIun = cancelledIun;
        return this;
    }

    public LegalNotificationCreationRequest amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public LegalNotificationCreationRequest paymentExpirationDate(String paymentExpirationDate) {
        this.paymentExpirationDate = paymentExpirationDate;
        return this;
    }

    public LegalNotificationCreationRequest addRecipient(ResolvedRecipient recipient) {
        this.recipients.add(recipient);
        return this;
    }

    public String getPaProtocolNumber() {
        return paProtocolNumber;
    }

    public String getSubject() {
        return subject;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public DocumentRef getDocument() {
        return document;
    }

    public FeePolicy getFeePolicy() {
        return feePolicy;
    }

    public PhysicalCommunicationType getPhysicalCommunication() {
        return physicalCommunication;
    }

    public String getSenderDenomination() {
        return senderDenomination;
    }

    public String getSenderTaxId() {
        return senderTaxId;
    }

    public String getGroup() {
        return group;
    }

    public String getTaxonomyCode() {
        return taxonomyCode;
    }

    public Integer getPaFee() {
        return paFee;
    }

    public Integer getVat() {
        return vat;
    }

    public PagoPaIntMode getPagoPaIntMode() {
        return pagoPaIntMode;
    }

    public Integer getPhysicalCommunicationPriority() {
        return physicalCommunicationPriority;
    }

    public List<String> getAdditionalLanguages() {
        return additionalLanguages;
    }

    public String getCancelledIun() {
        return cancelledIun;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getPaymentExpirationDate() {
        return paymentExpirationDate;
    }

    public List<ResolvedRecipient> getRecipients() {
        return recipients;
    }
}
