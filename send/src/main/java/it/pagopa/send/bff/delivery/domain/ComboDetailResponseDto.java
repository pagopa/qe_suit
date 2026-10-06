package it.pagopa.send.bff.delivery.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComboDetailResponseDto {

    private String iun;
    private String campaignId;
    private ComboCampaignType campaignType;
    private String senderPaId;
    private String senderPaName;
    private String recipientId;
    private String recipientName;
    private ComboRecipientType recipientType;
    private String messageSubject;
    private String messageBody;
    private ComboProcessStatus processStatus;
    private List<ChannelInfo> channels;
    private List<AttachmentInfo> attachments;
    private List<PaymentInfo> payments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelInfo {
        private String channelName;
        private ComboChannelStatus status;
        private String lastUpdateTimestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentInfo {
        private String attachmentId;
        private String name;
        private String contentType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private String noticeCode;
        private Double amount;
        private String status;
    }
}
