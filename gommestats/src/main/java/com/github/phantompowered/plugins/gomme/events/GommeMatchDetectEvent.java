package com.github.phantompowered.plugins.gomme.events;

import com.github.phantompowered.plugins.gomme.match.MatchInfo;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.event.Event;
import org.jetbrains.annotations.NotNull;

public class GommeMatchDetectEvent extends Event {

    private final ServiceConnection connection;
    private final MatchInfo matchInfo;

    public GommeMatchDetectEvent(@NotNull ServiceConnection connection, @NotNull MatchInfo matchInfo) {
        this.connection = connection;
        this.matchInfo = matchInfo;
    }

    @NotNull
    public ServiceConnection getConnection() {
        return this.connection;
    }

    @NotNull
    public MatchInfo getMatchInfo() {
        return this.matchInfo;
    }
}
