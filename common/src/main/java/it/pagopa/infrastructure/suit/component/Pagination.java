package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;
import java.util.NoSuchElementException;

@XPath(".//nav[contains(@class, 'MuiPagination-root')]")
public interface Pagination extends Component {

    List<Button> buttons();

    default Button prevBtn() {
        List<Button> buttons = buttons();
        requirePaginationButtons(buttons);
        return buttons.get(0);
    }

    default Button nextBtn() {
        List<Button> buttons = buttons();
        requirePaginationButtons(buttons);
        return buttons.get(buttons.size() - 1);
    }

    default void goBack() {
        Button previous = prevBtn();

        if (previous.isDisabled()) {
            throw new NoSuchElementException(
                    "Impossibile tornare indietro: pulsante precedente disabilitato."
            );
        }

        previous.click();
    }

    default void goNext() {
        Button next = nextBtn();

        if (next.isDisabled()) {
            throw new NoSuchElementException(
                    "Impossibile andare avanti: pulsante successivo disabilitato."
            );
        }

        next.click();
    }

    /**
     * Restituisce il numero dell'ultima pagina, letto dal pulsante
     * immediatamente precedente al pulsante "next".
     * Funziona sia con ellissi sia senza ellissi.
     */
    default int lastPageNumber() {
        List<Button> buttons = buttons();
        requirePaginationButtons(buttons);

        String lastPageText = buttons.get(buttons.size() - 2).read().trim();
        return Integer.parseInt(lastPageText);
    }

    /**
     * Verifica che esista almeno un pulsante pagina e che tutti
     * i pulsanti pagina siano abilitati.
     * I pulsanti precedente e successivo sono esclusi dal controllo.
     */
    default boolean isUsable() {

        if (buttons().size() < 3) {
            return false;
        }

        for (int i = 1; i < buttons().size() - 1; i++) {
            if (buttons().get(i).isDisabled()) {
                return false;
            }
        }

        return true;
    }

    default boolean hasNext() {
        List<Button> buttons = buttons();

        return buttons.size() >= 3
                && !buttons.get(buttons.size() - 1).isDisabled();
    }

    default boolean hasPrevious() {
        List<Button> buttons = buttons();

        return buttons.size() >= 3
                && !buttons.get(0).isDisabled();
    }

    private static void requirePaginationButtons(List<Button> buttons) {
        if (buttons.size() < 3) {
            throw new NoSuchElementException(
                    "Paginazione incompleta: attesi i pulsanti precedente, pagina e successivo."
            );
        }
    }
}