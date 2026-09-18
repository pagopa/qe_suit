package it.pagopa.send.bff.delivery.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.bff.delivery.domain.ComboDetailResponseDto;
import it.pagopa.send.bff.delivery.domain.ComboTimelineResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BffComboRestClient extends RestClient {

    public BffComboRestClient(TestChainFactory testChainFactory) {
        super(testChainFactory);
    }

    public TestChain<ComboDetailResponseDto> getComboDetails(String campaignId, String iun) {
        return execute(
                () -> (io.restassured.response.Response) null,
                ComboDetailResponseDto.class
        );
    }

    public TestChain<ComboTimelineResponseDto> getComboTimeline(String campaignId, String iun) {
        return execute(
                () -> (io.restassured.response.Response) null,
                ComboTimelineResponseDto.class
        );
    }
}
