package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Component;

@XPath("//div[contains(@class, 'MuiSnackbar-root')][1]")
public interface Snackbar extends Component {
    @XPath(".//div[contains(@class, 'MuiAlert-root')]")
    Alert alert();

    default String getErrorMessage() {
        if (!alert().isError())
            throw new IllegalStateException("Snackbar non in stato di errore, impossibile leggere il messaggio");

        return alert().message().read();
    }

    default String getInfoMessage() {
        if (!alert().isInfo())
            throw new IllegalStateException("Snackbar non in stato di info, impossibile leggere il messaggio");

        return alert().message().read();
    }

    default String getSuccessMessage() {
        if (!alert().isSuccess())
            throw new IllegalStateException("Snackbar non in stato di successo, impossibile leggere il messaggio");

        return alert().message().read();
    }
}
