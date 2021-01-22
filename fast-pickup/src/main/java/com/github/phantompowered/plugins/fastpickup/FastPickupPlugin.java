package com.github.phantompowered.plugins.fastpickup;

import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.PacketHandler;
import com.github.phantompowered.proxy.api.network.exception.CancelProceedException;
import com.github.phantompowered.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginState;
import com.github.phantompowered.proxy.api.plugin.annotation.Inject;
import com.github.phantompowered.proxy.api.plugin.annotation.Plugin;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.tick.TickHandlerProvider;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientPlayerPosition;

@Plugin(
        id = "com.github.phantompowered.plugins.fastpickup",
        displayName = "FastPickup",
        version = 1,
        website = "https://github.com/PhantomPowered/Plugins",
        authors = "derrop"
)
public class FastPickupPlugin {

    private static final String GROUND_PROPERTY = "fast_pickup_player_on_ground";
    private static final String INTERRUPT_SENDING = "fast_pickup_sending_interrupt";

    @Inject(state = PluginState.ENABLED)
    public void enable(PluginContainer container, ServiceRegistry registry) {
        ServiceConnector connector = registry.getProviderUnchecked(ServiceConnector.class);
        registry.getProviderUnchecked(TickHandlerProvider.class).registerHandler(() -> {
            for (ServiceConnection connection : connector.getOnlineClients()) {
                if (!connection.isConnected()) {
                    continue;
                }

                Boolean interrupt = connection.getProperty(INTERRUPT_SENDING);
                if (interrupt != null && interrupt) {
                    continue;
                }

                Boolean ground = connection.getProperty(GROUND_PROPERTY);
                connection.sendPacket(new PacketPlayClientPlayerPosition(ground != null && ground));
            }
        });
        registry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(container, this);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.FLYING)
    public void handleFlyingPacket(Player player, PacketPlayClientPlayerPosition packet) {
        ServiceConnection connection = player.getConnectedClient();
        if (connection == null) {
            return;
        }

        connection.setProperty(GROUND_PROPERTY, packet.isOnGround());
        connection.setProperty(INTERRUPT_SENDING, false);
        throw CancelProceedException.INSTANCE;
    }

    @PacketHandler(packetIds = {ProtocolIds.FromClient.Play.POSITION, ProtocolIds.FromClient.Play.POSITION_LOOK, ProtocolIds.FromClient.Play.LOOK})
    public void handleMovementPacket(Player player, Packet packet) {
        ServiceConnection connection = player.getConnectedClient();
        if (connection == null) {
            return;
        }

        connection.setProperty(INTERRUPT_SENDING, true);
    }

}
