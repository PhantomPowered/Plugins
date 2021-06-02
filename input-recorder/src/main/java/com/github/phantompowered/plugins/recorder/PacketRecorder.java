package com.github.phantompowered.plugins.recorder;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.network.wrapper.DefaultProtoBuf;
import io.netty.buffer.Unpooled;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PacketRecorder {

    private final PathRecorder recorder;
    private final Player player;
    private final DataOutputStream outputStream;
    private final Runnable finishHandler;

    private final long beginTimestamp;

    protected PacketRecorder(PathRecorder recorder, Player player, OutputStream outputStream, Runnable finishHandler) {
        this.recorder = recorder;
        this.player = player;
        this.outputStream = new DataOutputStream(outputStream);
        this.finishHandler = finishHandler;

        this.beginTimestamp = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getBeginTimestamp() {
        return this.beginTimestamp;
    }

    protected void handlePacket(Packet packet) throws IOException {
        // TODO Is the recordpath command to stop recording also recorded?

        ProtoBuf buffer = new DefaultProtoBuf(47, Unpooled.buffer());
        packet.write(buffer, ProtocolDirection.TO_SERVER, 47);
        byte[] bytes = buffer.toArray();

        this.outputStream.writeInt((int) (System.currentTimeMillis() - this.beginTimestamp));
        this.outputStream.writeShort(packet.getId());
        this.outputStream.writeInt(bytes.length);
        this.outputStream.write(bytes);
    }

    public void close() throws IOException {
        this.recorder.getRecorders().remove(this);
        this.outputStream.close();
        this.finishHandler.run();
    }

}
