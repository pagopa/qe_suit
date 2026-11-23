package it.pagopa.infrastructure.channel;

import it.pagopa.application.ChannelKind;
import java.util.Locale;

public interface CurrentChannel<C extends ChannelKind> {
    C getCurrentChannel();

    void setCurrentChannel(C currentChannel);

    /** Legacy implementations remain source-compatible; runtime implementations should override. */
    default Locale getCurrentLanguage() { return Locale.ITALIAN; }

    /** Override in implementations that track scenario language. */
    default void setCurrentLanguage(Locale currentLanguage) {}
}
