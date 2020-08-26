package com.github.phantompowered.plugins.gomme.events;

import com.github.phantompowered.plugins.gomme.GommeServerType;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.event.Event;
import org.jetbrains.annotations.NotNull;

public class GommeServerSwitchEvent extends Event {

    private final ServiceConnection connection;
    private final String matchId;
    private final GommeServerType gameMode;

    public GommeServerSwitchEvent(@NotNull ServiceConnection connection, @NotNull String matchId, @NotNull GommeServerType gameMode) {
        this.connection = connection;
        this.matchId = matchId;
        this.gameMode = gameMode;
    }

    @NotNull
    public ServiceConnection getConnection() {
        return this.connection;
    }

    @NotNull
    public String getMatchId() {
        return this.matchId;
    }

    @NotNull
    public GommeServerType getServerType() {
        return this.gameMode;
    }
}
