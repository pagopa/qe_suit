package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.DownloadLegalFactsGateway;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineItemComponent.TimelineGroupComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Scarica le attestazioni opponibili a terzi della notifica (es. "notifica presa in carico", "notifica digitale")
 * dalla pagina "Stato della notifica" del portale della PA, con i gruppi dei destinatari espansi perché alcune stanno
 * negli eventi di invio. Per ogni attestazione la pagina si riapre, perché il portale apre il PDF nella stessa scheda.
 */
@Service
@RequiredArgsConstructor
public class WebDownloadLegalFactsGateway implements DownloadLegalFactsGateway {

    private final TimelineDetailsPage timelineDetailsPage;
    private final WebPdfDownloader pdfDownloader;

    @Override
    public List<String> downloadLegalFacts(String iun) {
        openTimeline(iun);
        int legalFacts = legalFactButtons().size();
        return IntStream.range(0, legalFacts)
                .mapToObj(index -> pdfDownloader.download(() -> clickLegalFact(iun, index),
                        "L'attestazione " + (index + 1) + " di " + legalFacts + " della notifica " + iun))
                .toList();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }

    // apre la cronologia con tutti i gruppi dei destinatari espansi, così sono visibili anche le loro attestazioni
    private void openTimeline(String iun) {
        timelineDetailsPage.navigateTo(iun);
        timelineDetailsPage.assertLoaded();
        timelineDetailsPage.timeline().groupHeaders().stream()
                .filter(WebDownloadLegalFactsGateway::isCollapsed)
                .forEach(TimelineGroupComponent.HeaderButton::click);
    }

    private static boolean isCollapsed(TimelineGroupComponent.HeaderButton header) {
        return header.get()
                .map(element -> "false".equals(element.getAttributes().get("aria-expanded")))
                .orElse(false);
    }

    private List<Button> legalFactButtons() {
        return timelineDetailsPage.timeline().legalFactButtons();
    }

    private void clickLegalFact(String iun, int index) {
        openTimeline(iun);
        List<Button> buttons = legalFactButtons();
        if (index < buttons.size()) {
            buttons.get(index).click();
        }
    }
}
