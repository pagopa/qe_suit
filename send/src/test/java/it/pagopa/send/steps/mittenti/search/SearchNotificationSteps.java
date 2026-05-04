package it.pagopa.send.steps.mittenti.search;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.steps.mittenti.components.NotificationRow;
import it.pagopa.send.steps.mittenti.pages.DashboardPage;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.steps.mittenti.creazione_notifica.NotificationContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

@RequiredArgsConstructor
public class SearchNotificationSteps {
    private final NotificationContext context; 
    private final WebPresentationGateway browser;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @When("filtra per {word} con valore {string}")
    public void filterBy(String tipoFiltro, String valore) {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.removeFiltersIfPresent();
        dashboardPage.waitUntilReady();
        dashboardPage.filters().filterBy(tipoFiltro, valore);
        dashboardPage.assertLoaded();
    }

    @Given("leggo la prima notifica in lista")
    public void leggiPrimaNotifica() {
        DashboardPage dashboard = browser.bind(DashboardPage.class);
        dashboard.waitUntilReady();
        List<NotificationRow> rows = dashboard.rows();
        org.junit.jupiter.api.Assertions.assertFalse(rows.isEmpty(), "Nessuna notifica in lista");
        NotificationRow first = rows.get(0);
        context.setIun(first.iun().read().getText());
        context.setRecipientTaxCode(first.recipient().read().getText());
        context.setStatus(first.status().read().getText());
    }

    @When("filtra per IUN dalla notifica letta")
    public void filterByIunFromContext() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.removeFiltersIfPresent();
        dashboardPage.waitUntilReady();
        dashboardPage.filters().filterBy("IUN", context.getIun());
        dashboardPage.assertLoaded();
    }

    @When("filtra per TAX_CODE dalla notifica letta")
    public void filterByTaxCodeFromContext() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.removeFiltersIfPresent();
        dashboardPage.waitUntilReady();
        dashboardPage.filters().filterBy("TAX_CODE", context.getRecipientTaxCode());
        dashboardPage.assertLoaded();
    }

    @Then("i risultati contengono l'IUN della notifica letta")
    public void verificaRisultatiIun() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.waitUntilReady();
        List<NotificationRow> rows = dashboardPage.rows();
        Assertions.assertEquals(1, rows.size(), "Attesa esattamente 1 notifica per IUN univoco");
        Assertions.assertEquals(context.getIun(), rows.get(0).iun().read().getText());
    }

    @Then("i risultati contengono il TAX_CODE della notifica letta")
    public void verificaRisultatiTaxCode() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.waitUntilReady();
        List<NotificationRow> rows = dashboardPage.rows();
        Assertions.assertFalse(rows.isEmpty(), "Nessun risultato trovato");
        long matching = rows.stream()
            .filter(row -> row.recipient().read().getText().contains(context.getRecipientTaxCode()))
            .count();
        Assertions.assertEquals(rows.size(), (int) matching,
            "Non tutte le notifiche corrispondono al TAX_CODE filtrato");
    }

    @Then("la lista notifiche è vuota")
    public void verificaListaVuota() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.waitUntilReady();
        Assertions.assertTrue(dashboardPage.rows().isEmpty(), "Attesa lista vuota ma sono presenti risultati");
    }

    @When("imposta arco temporale dall'ultimo anno")
    public void impostaArcoTemporaleUltimoAnno() {
        String to = LocalDate.now().minusDays(7).format(DATE_FORMATTER);
        String from = LocalDate.now().minusDays(8).format(DATE_FORMATTER);
        context.setFromDate(from);
        context.setToDate(to);
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.removeFiltersIfPresent();
        dashboardPage.waitUntilReady();
        dashboardPage.filters().setDateRange(from, to);
    }

    @When("imposta arco temporale da {string} a {string}")
    public void impostaArcoTemporale(String from, String to) {
        context.setFromDate(from);
        context.setToDate(to);
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.removeFiltersIfPresent();
        dashboardPage.waitUntilReady();
        dashboardPage.filters().setDateRange(from, to);
    }

    @Then("il bottone Filtra è disabilitato")
    public void verificaBottoneFiltroDisabilitato() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        Assertions.assertTrue(
            dashboardPage.filters().hasDateRangeError(),
            "Attesi entrambi i campi data in stato di errore"
        );
    }

    @When("clicca su Filtra")
    public void cliccaSuFiltra() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.filters().filterButton().click();
        dashboardPage.waitUntilReady();
    }

    @Then("tutti i risultati visibili hanno data compresa nell'arco temporale impostato")
    public void verificaRisultatiNellArcoTemporale() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.waitUntilReady();
        List<NotificationRow> rows = dashboardPage.rows();
        Assertions.assertFalse(rows.isEmpty(), "Nessun risultato trovato");

        LocalDate from = LocalDate.parse(context.getFromDate(), DATE_FORMATTER);
        LocalDate to = LocalDate.parse(context.getToDate(), DATE_FORMATTER);

        rows.forEach(row -> {
            LocalDate rowDate = LocalDate.parse(row.date().read().getText(), DATE_FORMATTER);
            Assertions.assertTrue(
                !rowDate.isBefore(from) && !rowDate.isAfter(to),
                "Data notifica fuori dall'arco temporale: " + rowDate
            );
        });
    }

    @When("filtra per stato {string}")
    public void filtraPerStato(String stato) {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.removeFiltersIfPresent();
        dashboardPage.waitUntilReady();
        dashboardPage.filters().statusDropdown().click();
        browser.click(dashboardPage.filters().statusOptionSelector(stato));
        dashboardPage.filters().filterButton().click();
        dashboardPage.waitUntilReady();
    }

    @Then("tutti i risultati visibili hanno stato {string}")
    public void verificaRisultatiPerStato(String stato) {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.waitUntilReady();
        List<NotificationRow> rows = dashboardPage.rows();
        Assertions.assertFalse(rows.isEmpty(), "Nessun risultato trovato");
        rows.forEach(row ->
            Assertions.assertEquals(stato, row.status().read().getText())
        );
    }

    @After("@searchDateInvalid")
    public void resetFiltriDopoDateErrate() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.filters().clearDateRange();
    }

    @When("filtra per stato della notifica letta")
    public void filtraPerStatoFromContext() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.removeFiltersIfPresent();
        dashboardPage.waitUntilReady();
        dashboardPage.filters().statusDropdown().click();
        browser.click(dashboardPage.filters().statusOptionSelector(context.getStatus()));
        dashboardPage.filters().filterButton().click();
        dashboardPage.waitUntilReady();
    }

    @Then("tutti i risultati visibili hanno stato della notifica letta")
    public void verificaRisultatiPerStatoFromContext() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.waitUntilReady();
        List<NotificationRow> rows = dashboardPage.rows();
        Assertions.assertFalse(rows.isEmpty(), "Nessun risultato trovato");
        rows.forEach(row ->
            Assertions.assertEquals(context.getStatus(), row.status().read().getText())
        );
    }
}
