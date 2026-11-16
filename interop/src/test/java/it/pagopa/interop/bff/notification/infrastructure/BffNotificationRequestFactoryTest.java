package it.pagopa.interop.bff.notification.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BffNotificationRequestFactoryTest {
    @Test
    void enablesEveryGeneratedCategoryWithoutSharingMutableConfigurations() {
        var factory = new BffNotificationRequestFactory();
        var config = factory.allEnabledInAppConfig();
        var fields = new ObjectMapper().valueToTree(config).fields();
        int count = 0;
        while (fields.hasNext()) {
            var field = fields.next();
            assertThat(field.getValue().isBoolean()).as(field.getKey()).isTrue();
            assertThat(field.getValue().booleanValue()).as(field.getKey()).isTrue();
            count++;
        }
        assertThat(count).isPositive();
        config.eserviceStateChangedToConsumer(false);
        assertThat(factory.allEnabledInAppConfig().getEserviceStateChangedToConsumer()).isTrue();
        assertThat(factory.supports(Channel.BFF)).isTrue();
        assertThat(factory.supports(Channel.WEB_BROWSER)).isFalse();
    }
}
