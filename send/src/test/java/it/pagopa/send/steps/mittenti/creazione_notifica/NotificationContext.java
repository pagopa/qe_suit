package it.pagopa.send.steps.mittenti.creazione_notifica;

import org.springframework.stereotype.Component;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.send.model.NotificationData;
import lombok.Data;

@Component
@ScenarioScope
@Data 
public class NotificationContext {
    private NotificationData notifica;
    private String iun;
    private String recipientTaxCode;
    private String fromDate;
    private String toDate;
    private String status;

    public NotificationContext() {
        // Empty constructor for Cucumber DI
    }
}
