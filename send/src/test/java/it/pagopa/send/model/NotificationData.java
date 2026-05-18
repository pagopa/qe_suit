package it.pagopa.send.model;

import lombok.Data;

@Data
public class NotificationData {
    private String subject;
    private String protocolNumber;
    private String taxonomyCode;
    private String group;
    private String language;
    private String physicalCommunicationType;
    private String paymentType;

    private String recipientType;
    private String taxId;
    private String firstName;
    private String lastName;
    private String addressLookup;
    private String pec;

    private String documentTitle;
    private String documentFilePath;
}
