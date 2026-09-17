package it.pagopa.interop.web.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.infrastructure.suit.component.Snackbar;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("cucumber")
@RequiredArgsConstructor
public class WebCommonGateway {

    private final WebPresentationGateway presentationGateway;

    public String getSnackbarErrorMessage() {
        Snackbar snackbar = presentationGateway.bind(Snackbar.class);

        if (!snackbar.alert().isError())
            throw new IllegalStateException("Snackbar non in stato di errore, impossibile leggere il messaggio");

        return snackbar.alert().message().read();
    }
}
