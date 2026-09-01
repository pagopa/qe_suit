package it.pagopa.infrastructure.channel;

import it.pagopa.kernel.ChannelKind;

import java.util.List;
import java.util.Optional;

public interface ChannelPluginRegistry<T, C extends Enum<C> & ChannelKind> {

    Optional<T> getPluginFor(C channel);

    List<T> getPlugins();
}