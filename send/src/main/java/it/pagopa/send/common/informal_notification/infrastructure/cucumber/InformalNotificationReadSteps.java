package it.pagopa.send.common.informal_notification.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.common.informal_notification.application.InformalNotificationUseCase;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationTimeline;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Consultazione, da parte della PA mittente, del dettaglio e della timeline di una comunicazione bonaria.
 * La risposta letta nel When resta nell'istanza degli step, che Cucumber crea per ogni scenario.
 */
@RequiredArgsConstructor
public class InformalNotificationReadSteps {

    private final InformalNotificationUseCase informalNotificationUseCase;
    private final CurrentUserSession currentUserSession;

    private int detailStatus;
    private InformalNotificationTimeline timeline;

    @When("la PA {user} richiede il dettaglio della comunicazione bonaria con IUN {string}")
    public void requestInformalNotificationDetail(User sender, String iun) {
        currentUserSession.setUser(sender);
        detailStatus = informalNotificationUseCase.getSenderDetailStatus(iun);
    }

    @Then("la richiesta viene rifiutata con status {int}")
    public void assertDetailRequestRejected(int expectedStatus) {
        Assertions.assertThat(detailStatus).as("status della richiesta di dettaglio").isEqualTo(expectedStatus);
    }

    @When("la PA {user} consulta la timeline della comunicazione bonaria")
    public void readInformalNotificationTimeline(User sender) {
        currentUserSession.setUser(sender);
        timeline = informalNotificationUseCase.getLastSenderTimeline();
    }

    @Then("gli stati della timeline sono in ordine cronologico decrescente")
    public void assertTimelineInDescendingOrder() {
        List<InformalNotificationTimeline.StatusChange> history = timeline.statusHistory();
        Assertions.assertThat(history).as("stati della timeline").isNotEmpty();
        for (int i = 0; i < history.size() - 1; i++) {
            OffsetDateTime current = history.get(i).activeFrom();
            OffsetDateTime next = history.get(i + 1).activeFrom();
            Assertions.assertThat(current)
                    .as("lo stato %d (%s) deve essere successivo o uguale allo stato %d (%s)", i, current, i + 1, next)
                    .isAfterOrEqualTo(next);
        }
    }

    @Then("la timeline traccia gli step dei canali")
    public void assertTimelineTracksChannelSteps() {
        Assertions.assertThat(timeline.statusHistory())
                .as("almeno uno stato della timeline deve avere gli step dei canali")
                .anyMatch(status -> status.channelSteps() > 0);
    }
}
