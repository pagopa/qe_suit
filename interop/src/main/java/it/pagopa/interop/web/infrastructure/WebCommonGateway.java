package it.pagopa.interop.new_arch.web.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.new_arch.web.infrastructure.suit.component.Snackbar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
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
