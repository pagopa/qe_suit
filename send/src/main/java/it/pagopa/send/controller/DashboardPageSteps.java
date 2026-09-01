package it.pagopa.send.controller;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import it.pagopa.send.utils.IUNHelper;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.send.web.notification_details.infrastructure.NotificationDetailsProxy;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DashboardPageSteps {

    private static final DateTimeFormatter SEARCH_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final NotificationDetailsProxy notificationDetailsProxy;
    private final NotificationContext notificationContext;

    @When("viene aperto il dettaglio di una notifica")
    public void openNotificationDetail() {
        notificationDetailsProxy.goToNotificationDetails();
    }

    @When("viene aperto il dettaglio della notifica che soddisfa i seguenti criteri:")
    @When("viene effettuata una ricerca notifica tramite i seguenti parametri:")
    public void searchNotificationWithFilter(DataTable parameters) {
        Map<String, String> searchParams = new LinkedHashMap<>();
        parameters.asMap(String.class, String.class).forEach((key, value) -> searchParams.put(key, resolveToken(value)));

        notificationDetailsProxy.searchNotification(searchParams);
        notificationDetailsProxy.goToNotificationDetails();
    }

    @Then("la notifica è in stato annullata")
    public void assertNotificationChipStatus() {
        Chip statusChip = notificationDetailsProxy.notificationStatusSection().statusChip();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(statusChip.isWarning())
                    .as("L'alert è di tipo warning")
                    .isTrue();

            softly.assertThat(statusChip.text().read())
                    .as("Il messaggio dell'alert")
                    .isIn("Annullata");
        });
    }

    /**
     * Risolve i token dinamici ammessi nella DataTable di ricerca: {@code $currentIUN} è lo IUN
     * dell'ultima notifica creata nello scenario tramite,
     * {@code $currentDate} è la data odierna nel formato atteso dal campo di ricerca (gg/mm/aaaa).
     * Qualunque altro valore passa invariato, per permettere override letterali.
     */
    private String resolveToken(String rawValue) {
        String resolvedValue;
        if(rawValue.equals("$currentIUN"))
            resolvedValue = IUNHelper.extractFromBffNewNotificationResponse(notificationContext.getBffNewNotificationResponse());
        else if(rawValue.equals("$currentDate"))
            resolvedValue = LocalDate.now().format(SEARCH_DATE_FORMAT);
        else if(rawValue.equals("$currentNotificationIUN"))
            resolvedValue = IUNHelper.extractFromBffNewNotificationResponse(notificationContext.getBffNewNotificationResponse());
        else if(rawValue.contains("DaysAgo")) {
            long days = Long.parseLong(rawValue
                    .replace("DaysAgo","")
                    .replace("$",""));
            resolvedValue = LocalDate.now().minusDays(days).format(SEARCH_DATE_FORMAT);
        }
        else
            resolvedValue = rawValue;
        return resolvedValue;
    }

}
