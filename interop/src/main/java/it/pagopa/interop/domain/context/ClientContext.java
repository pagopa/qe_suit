package it.pagopa.interop.domain.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.domain.model.Client;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ClientContext extends AbstractContext<Client> {
    @Override
    public void upsert(Client item) {
        super.findById(item.getUniqueIdentifier())
                .ifPresent(existing -> item.addAllKeyPairs(existing.getKeyPairs()));

        super.upsert(item);
    }
}
