package it.pagopa.send.common.legal_notification.infrastructure.factory;

import it.pagopa.send.common.legal_notification.domain.NotificationDefaults;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Carica i default statici della notifica da {@code notifications/templates/default.yaml}: un
 * unico baseline, sovrascrivibile puntualmente in fase di preparazione della notifica (vedi
 * {@link LegalNotificationRequestFactory#applyPreliminaryData}).
 */
@Component
public class NotificationDefaultsLoader {

    private static final String TEMPLATE_PATH = "notifications/templates/default.yaml";

    @SuppressWarnings("unchecked")
    public NotificationDefaults load() {
        try (InputStream is = new ClassPathResource(TEMPLATE_PATH).getInputStream()) {
            Map<String, Object> root = new Yaml().load(is);
            Map<String, Object> address = (Map<String, Object>) root.get("physicalAddress");

            return new NotificationDefaults(
                    (String) root.get("subject"),
                    (String) root.get("abstractText"),
                    (String) root.get("taxonomyCode"),
                    (String) root.get("notificationFeePolicy"),
                    (String) root.get("physicalCommunicationType"),
                    (Integer) root.get("paFee"),
                    (Integer) root.get("vat"),
                    (Integer) root.get("physicalCommunicationPriority"),
                    (String) root.get("pagoPaIntMode"),
                    new NotificationDefaults.PhysicalAddressDefaults(
                            (String) address.get("at"),
                            (String) address.get("address"),
                            (String) address.get("addressDetails"),
                            (String) address.get("zip"),
                            (String) address.get("municipality"),
                            (String) address.get("municipalityDetails"),
                            (String) address.get("province"),
                            (String) address.get("foreignState")
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("Impossibile caricare il template di default della notifica", e);
        }
    }
}
