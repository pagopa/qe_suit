package it.pagopa.send.common.informal_notification.infrastructure.factory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public class InformalNotificationDefaultsLoader {

    private static final String TEMPLATE_PATH = "notifications/templates/default-informal.yaml";

    @SuppressWarnings("unchecked")
    public Map<String, Object> load() {
        try (InputStream is = new ClassPathResource(TEMPLATE_PATH).getInputStream()) {
            return new Yaml().load(is);
        } catch (IOException e) {
            throw new RuntimeException("Impossibile caricare il template di default della comunicazione informale", e);
        }
    }
}
