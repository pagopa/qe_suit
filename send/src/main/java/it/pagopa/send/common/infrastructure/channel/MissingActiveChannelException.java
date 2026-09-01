package it.pagopa.send.common.infrastructure.channel;

public class MissingActiveChannelException extends IllegalStateException {

    public MissingActiveChannelException() {
        super(String.format("%n" +
                "================================================================================%n" +
                " [ERRORE DI CONFIGURAZIONE TEST] - CANALE ATTIVO NON TROVATO%n" +
                "================================================================================%n" +
                " Impossibile eseguire l'operazione: l'interceptor non ha trovato un canale attivo%n" +
                " nel 'ChannelContext'.%n%n" +
                " Come risolvere il problema:%n" +
                " 1. Inserisci esplicitamente lo step che dichiara il canale oppure annota lo Scenario con @B2B o @WEB_BROWSER %n" +
                "================================================================================"));
    }
}
