package com.github.phantompowered.plugins.recorder;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.event.EventPriority;
import com.github.phantompowered.proxy.api.network.PacketHandler;
import com.github.phantompowered.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.network.wrapper.DecodedPacket;
import com.github.phantompowered.proxy.protocol.play.shared.PacketPlayKeepAlive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class PathRecorder {

    private final Collection<PacketRecorder> recorders = new ArrayList<>();

    protected PathRecorder(ServiceRegistry registry, PluginContainer container) {
        registry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(container, this);
    }

    public void recordPath(Player player, OutputStream outputStream, Runnable finishHandler) {
        this.recorders.add(new PacketRecorder(this, player, outputStream, finishHandler));
    }

    public Collection<PacketRecorder> getRecorders() {
        return this.recorders;
    }

    public PacketRecorder getRecorder(Player player) {
        return this.recorders.stream().filter(recorder -> recorder.getPlayer() == player).findFirst().orElse(null);
    }

    @PacketHandler(protocolState = ProtocolState.PLAY, directions = ProtocolDirection.TO_SERVER, priority = EventPriority.LAST)
    public void handleGeneral(Player player, DecodedPacket packet) throws IOException {
        if (packet.getPacket() instanceof PacketPlayKeepAlive) {
            return;
        }

        for (PacketRecorder recorder : this.recorders) {
            if (recorder.getPlayer() == player) {
                recorder.handlePacket(packet.getPacket());
            }
        }
    }

}
