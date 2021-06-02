package com.github.phantompowered.plugins.recorder;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.registry.packet.PacketRegistry;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.network.wrapper.DefaultProtoBuf;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientPlayerPosition;
import io.netty.buffer.Unpooled;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PathPlayer {

    private final PacketRegistry packetRegistry;

    public PathPlayer(ServiceRegistry registry) {
        this.packetRegistry = registry.getProviderUnchecked(PacketRegistry.class);
    }

    public void playPath(ServiceConnection connection, InputStream inputStream) throws IOException, InterruptedException {
        DataInputStream dataInput = new DataInputStream(inputStream);

        long beginTimestamp = System.currentTimeMillis();

        while (dataInput.available() > 0) {
            long playTimestamp = beginTimestamp + dataInput.readInt();
            long delay = playTimestamp - System.currentTimeMillis();
            if (delay > 0) {
                Thread.sleep(delay);
            }

            int packetId = dataInput.readShort();
            byte[] data = new byte[dataInput.readInt()];
            if (dataInput.read(data) != data.length) {
                throw new IllegalStateException("Invalid length read for packet with id " + packetId);
            }

            Packet packet = this.packetRegistry.getPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, packetId);
            ProtoBuf buffer = new DefaultProtoBuf(47, Unpooled.wrappedBuffer(data));
            packet.read(buffer, ProtocolDirection.TO_SERVER, 47);

            connection.sendPacket(packet);

            if (packet instanceof PacketPlayClientPlayerPosition && packet.getClass() != PacketPlayClientPlayerPosition.class) {
                connection.updateLocation(((PacketPlayClientPlayerPosition) packet).getLocation(connection.getLocation()));
            }
        }
    }

}
