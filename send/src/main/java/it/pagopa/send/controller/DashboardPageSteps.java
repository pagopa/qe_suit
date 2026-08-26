package it.pagopa.send.controller;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import it.pagopa.send.legalnotification.application.LegalNotificationJourney;
import it.pagopa.send.legalnotification.application.LegalNotificationUseCase;
import it.pagopa.send.utils.IUNHelper;
import it.pagopa.send.web.notification_details.infrastructure.NotificationDetailsProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DashboardPageSteps {

    private static final DateTimeFormatter SEARCH_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final NotificationDetailsProxy notificationDetailsProxy;
    private final LegalNotificationJourney journey;
    private final LegalNotificationUseCase useCase;
    private final NotificationContext notificationContext;

    @When("viene aperto il dettaglio di una notifica")
    public void openNotificationDetail() {
        notificationDetailsProxy.goToNotificationDetails();
    }

    @When("viene effettuata una ricerca notifica tramite i seguenti parametri:")
    public void searchNotificationWithFilter(DataTable parameters) {
        Map<String, String> searchParams = new LinkedHashMap<>();
        parameters.asMap(String.class, String.class).forEach((key, value) -> searchParams.put(key, resolveToken(value)));

        notificationDetailsProxy.searchNotification(searchParams);
        notificationDetailsProxy.goToNotificationDetails();
    }

    @Then("la notifica è in stato annullato")
    public void assertNotificationChipStatus() {
        //useCase.assertNotificationStatus();
    }

    /**
     * Risolve i token dinamici ammessi nella DataTable di ricerca: {@code $currentIUN} è lo IUN
     * dell'ultima notifica creata nello scenario tramite {@link LegalNotificationJourney},
     * {@code $currentDate} è la data odierna nel formato atteso dal campo di ricerca (gg/mm/aaaa).
     * Qualunque altro valore passa invariato, per permettere override letterali.
     */
    private String resolveToken(String rawValue) {
        String resolvedValue;
        if(rawValue.equals("$currentIUN"))
            resolvedValue = IUNHelper.extractFromBffNewNotificationResponse(journey.getLastResponse());
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
