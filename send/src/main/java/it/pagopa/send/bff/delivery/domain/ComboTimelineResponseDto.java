package it.pagopa.send.bff.delivery.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComboTimelineResponseDto {

    private String iun;
    private List<String> recipients;
    private ComboProcessStatus latestStatus;
    private List<TimelineEventDto> events;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineEventDto {
        private String eventId;
        private String title;
        private String description;
        private String channel;
        private ComboChannelStatus channelStatus;
        private Instant timestamp;
    }
}
