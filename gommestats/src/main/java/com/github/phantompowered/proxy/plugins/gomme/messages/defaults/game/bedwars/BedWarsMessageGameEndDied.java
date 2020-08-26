package com.github.phantompowered.proxy.plugins.gomme.messages.defaults.game.bedwars;

import com.github.phantompowered.proxy.plugins.gomme.match.event.global.match.MatchEndDiedEvent;
import com.github.phantompowered.proxy.plugins.gomme.messages.Language;
import com.github.phantompowered.proxy.plugins.gomme.messages.MessageType;
import com.github.phantompowered.proxy.plugins.gomme.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.phantompowered.proxy.plugins.gomme.messages.defaults.game.SpecificGameMessageRegistrar;

public class BedWarsMessageGameEndDied extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerMessage(Language.GERMANY, MessageType.GAME_END_DIED, "[BedWars] Du bist nun Zuschauer!", MatchEndDiedEvent::new);
        registrar.registerMessage(Language.AUSTRIA, MessageType.GAME_END_DIED, "[BedWars] Du bist jetz Zuschaua!", MatchEndDiedEvent::new);
    }
}
