package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;
import java.util.NoSuchElementException;

@XPath(".//nav[contains(@class, 'MuiPagination-root')]")
public interface Pagination extends Component {

    String SELECTED_CLASS = "Mui-selected";

    @XPath("(.//li)[last()]")
    Button nextBtn();

    @XPath("(.//li)[1]")
    Button prevBtn();

    /**
     * Tutti i pulsanti "numero pagina" della nav, esclusi i pulsanti prev/next.
     * L'elemento dell'eventuale ellissi ("…") non è un {@code <button>} e quindi
     * viene naturalmente escluso da questa selezione.
     */
    @XPath("(.//li)[position() > 1 and position() < last()]//button")
    List<Button> pageButtons();

    /**
     * Numero dell'ultima pagina dichiarata dalla nav, letto dal penultimo {@code <li>}
     * (quello immediatamente precedente al pulsante "next"). Funziona sia nel caso con
     * ellissi ({@code < 1 .. N >}) sia nel caso compatto senza ellissi ({@code < 1 2 3 >}).
     */
    default int lastPageNumber() {
        List<Button> buttons = pageButtons();

        if (buttons.isEmpty()) {
            throw new NoSuchElementException(
                    "Impossibile determinare l'ultima pagina: nessun pulsante di paginazione trovato."
            );
        }

        String lastPageText = buttons.get(buttons.size() - 1).read().trim();
        return Integer.parseInt(lastPageText);
    }

    /**
     * Verifica che la nav di paginazione sia effettivamente utilizzabile, ovvero che
     * esista almeno un pulsante pagina e che nessuno di essi sia disabilitato.
     */
    default boolean isUsable() {
        List<Button> buttons = pageButtons();
        return !buttons.isEmpty() && buttons.stream().noneMatch(Button::isDisabled);
    }

    default boolean hasNext(){
        return !nextBtn().isDisabled();
    }

    default boolean hasPrevious(){
        return !prevBtn().isDisabled();
    }
}
