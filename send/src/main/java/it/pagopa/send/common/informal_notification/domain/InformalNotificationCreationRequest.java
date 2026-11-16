package it.pagopa.send.common.informal_notification.domain;

import it.pagopa.send.common.legal_notification.domain.DocumentRef;

import java.util.ArrayList;
import java.util.List;

public class InformalNotificationCreationRequest {

    private String paProtocolNumber;
    private String subject;
    private String campaignId;
    private String senderDenomination;
    private String group;
    private final List<DocumentRef> documents = new ArrayList<>();
    private final List<ResolvedInformalRecipient> recipients = new ArrayList<>();

    public InformalNotificationCreationRequest paProtocolNumber(String paProtocolNumber) {
        this.paProtocolNumber = paProtocolNumber;
        return this;
    }

    public InformalNotificationCreationRequest subject(String subject) {
        this.subject = subject;
        return this;
    }

    public InformalNotificationCreationRequest campaignId(String campaignId) {
        this.campaignId = campaignId;
        return this;
    }

    public InformalNotificationCreationRequest senderDenomination(String senderDenomination) {
        this.senderDenomination = senderDenomination;
        return this;
    }

    public InformalNotificationCreationRequest group(String group) {
        this.group = group;
        return this;
    }

    public InformalNotificationCreationRequest document(DocumentRef document) {
        this.documents.clear();
        if (document != null) {
            this.documents.add(document);
        }
        return this;
    }

    public InformalNotificationCreationRequest addDocument(DocumentRef document) {
        if (document != null) {
            this.documents.add(document);
        }
        return this;
    }

    public InformalNotificationCreationRequest addRecipient(ResolvedInformalRecipient recipient) {
        if (recipient != null) {
            this.recipients.add(recipient);
        }
        return this;
    }

    public String paProtocolNumber() {
        return paProtocolNumber;
    }

    public String subject() {
        return subject;
    }

    public String campaignId() {
        return campaignId;
    }

    public String senderDenomination() {
        return senderDenomination;
    }

    public String group() {
        return group;
    }

    public List<DocumentRef> documents() {
        return documents;
    }

    public List<ResolvedInformalRecipient> recipients() {
        return recipients;
    }
}
